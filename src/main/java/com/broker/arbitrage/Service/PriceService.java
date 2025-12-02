package com.broker.arbitrage.Service;

import com.broker.arbitrage.Model.Stock;
import com.broker.arbitrage.Model.StockInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.round;

@Service
public class PriceService {

    private static final String BASE_URL = "https://yahoo-finance15.p.rapidapi.com/api/v1/markets/stock/quotes?ticker=";
    private static final String hostHeader = "yahoo-finance15.p.rapidapi.com";
    //https://yahoo-finance15.p.rapidapi.com/api/v1/markets/stock/quotes?ticker=TCS.BO

    private RestTemplate restTemplate;
    private Stock stock;

    public PriceService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

    }

    @Value("${rapidapi.key}")
    private String rapidApiKey;

    private final Set<String> monitored = ConcurrentHashMap.newKeySet();
    private final Map<String, Stock> latest = new ConcurrentHashMap<>();

    public void addStock(String symbol) {
        monitored.add(symbol.toUpperCase());
    }

    public Collection<Stock> getProcessStock(int page, int size) {
        List<Stock> list = new ArrayList<>(latest.values());

        // Optional: sort by symbol or id (stable ordering)
        list.sort(Comparator.comparing(stock->stock.getStockinfo().get(0).getPriceDiff(),
                Comparator.nullsLast(Double::compareTo)));

        int start = page * size;
        int end = Math.min(start + size, list.size());

        if (start >= list.size()) return Collections.emptyList();

        return list.subList(start, end);
    }


    public void fetchPricesAndUpdate() {
        Instant now = Instant.now();

        for (String symbol : monitored) {
            try {
                stock = new Stock();
                Stock nseStock = fetchStockDataFromApi(symbol + ".NS");
                Stock bseStock = fetchStockDataFromApi(symbol + ".BO");
                Double nsePrice = getPrice(nseStock);
                Double bsePrice = getPrice(bseStock);

                Double priceDiff = getPriceDiff(nseStock, bseStock);
                if (priceDiff == null) {
                    priceDiff = 0.0;
                }
                String name = extractName(nseStock);
                if(name == null) {
                    name = extractName(bseStock);
                }

                symbol = extractSymbol(nseStock);
                if(symbol == null) {
                    symbol = extractSymbol(bseStock);
                }

                StockInfo stockInfo = new StockInfo();
                stockInfo.setSymbol(symbol);
                stockInfo.setNsePrice(nsePrice);
                stockInfo.setBsePrice(bsePrice);
                stockInfo.setPriceDiff(priceDiff);
                stockInfo.setName(name);
                stockInfo.setSymbol(symbol);
                stock.setStockinfo(List.of(stockInfo));

                latest.put(symbol, stock);

            } catch (Exception e) {
                System.out.println("Error fetching price for " + symbol + ": " + e.getMessage());
            }
        }
    }

    private Double getPrice(Stock stock) {
        if (stock == null ||
                stock.getStockinfo() == null ||
                stock.getStockinfo().isEmpty()) {
            return null;
        }

        return stock.getStockinfo().get(0).getPrice();
    }




    private Double getPriceDiff(Stock nseStock, Stock bseStock) {
        Double nsePrice = getPrice(nseStock);
        Double bsePrice = getPrice(bseStock);

        System.out.println("nsePrice: " + nsePrice);
        System.out.println("bsePrice: " + bsePrice);


        if (nsePrice == null && bsePrice == null) {
            return null;
        }

        if (nsePrice == null) {
            return bsePrice; // only BSE available
        }

        if (bsePrice == null) {
            return nsePrice; // only NSE available
        }

        return Math.abs(nsePrice - bsePrice);
    }

    private String extractName(Stock stock) {
        try {
            if (stock != null &&
                    stock.getStockinfo() != null &&
                    !stock.getStockinfo().isEmpty() &&
                    stock.getStockinfo().get(0).getName() != null) {

                return stock.getStockinfo().get(0).getName();
            }
        } catch (Exception e) {
            System.out.println("Error extracting name: " + e.getMessage());
        }
        return null;
    }

    private String extractSymbol(Stock stock) {
        try {
            if (stock != null &&
                    stock.getStockinfo() != null &&
                    !stock.getStockinfo().isEmpty() &&
                    stock.getStockinfo().get(0).getSymbol() != null) {

                return stock.getStockinfo().get(0).getSymbol();
            }
        } catch (Exception e) {
            System.out.println("Error extracting name: " + e.getMessage());
        }
        return null;
    }

    private Stock fetchStockDataFromApi(String symbol) {

        String url = BASE_URL + symbol;

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-key", rapidApiKey);
        headers.set("x-rapidapi-host", hostHeader);
        try {
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            System.out.println("response : " + response);
            String json = response.getBody();

            System.out.println("Fetching price for " + symbol + ": " + json);

            ObjectMapper mapper = new ObjectMapper();
            Stock stock = mapper.readValue(json, Stock.class);
            return stock;
        } catch (Exception e) {
            System.out.println("Error fetching price for " + symbol + ": " + e.getMessage());
        }
        return null;
    }


    //multi threading
    @Async("stockExecutor")
    public CompletableFuture<Void> processStock(String symbol) {

        try {
            Stock nseStock = fetchStockDataFromApi(symbol + ".NS");
            Stock bseStock = fetchStockDataFromApi(symbol + ".BO");

            Double nsePrice = getPrice(nseStock);
            Double bsePrice = getPrice(bseStock);

            Double priceDiff = getPriceDiff(nseStock, bseStock);
            if (priceDiff == null) priceDiff = 0.0;

            String name = extractName(nseStock);
            if (name == null) name = extractName(bseStock);

            String finalSymbol = extractSymbol(nseStock);
            if (finalSymbol == null) finalSymbol = extractSymbol(bseStock);

            StockInfo info = new StockInfo();
            info.setSymbol(finalSymbol);
            info.setNsePrice(nsePrice);
            info.setBsePrice(bsePrice);
            info.setPriceDiff(priceDiff);
            info.setName(name);

            Stock stock = new Stock();
            stock.setStockinfo(List.of(info));

            latest.put(finalSymbol, stock); // thread-safe beacuse of concurrenthashmap

        } catch (Exception e) {
            System.out.println("Error processing stock " + symbol + ": " + e.getMessage());
        }

        return CompletableFuture.completedFuture(null);
    }



    public void fetchPricesAndUpdateWithMultiThreading() {

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String symbol : monitored) {
            futures.add(processStock(symbol)); // runs async
        }

        // Wait for all threads to finish
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

}
