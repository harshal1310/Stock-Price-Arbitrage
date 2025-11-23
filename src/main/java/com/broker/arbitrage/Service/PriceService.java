package com.broker.arbitrage.Service;

import com.broker.arbitrage.Model.Stock;
import com.broker.arbitrage.Model.StockInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.round;

@Service
public class PriceService {

    private static final String BASE_URL = "https://yahoo-finance15.p.rapidapi.com/api/v1/markets/stock/quotes?ticker=";
    private static final String hostHeader = "yahoo-finance15.p.rapidapi.com";
    //https://yahoo-finance15.p.rapidapi.com/api/v1/markets/stock/quotes?ticker=TCS.BO

    private RestTemplate restTemplate;
    private StockInfo stock;

    public PriceService(RestTemplate restTemplate, StockInfo stock) {
        this.restTemplate = restTemplate;
        this.stock = stock;
    }

    @Value("${rapidapi.key}")
    private String rapidApiKey;

    private final Set<String> monitored = ConcurrentHashMap.newKeySet();
    private final Map<String, StockInfo> latest = new ConcurrentHashMap<>();

    public void addStock(String symbol) {
        monitored.add(symbol.toUpperCase());
    }

    public Collection<StockInfo> getLatestPrices() {
        return latest.values();
    }

    public Double fetchPricesDiff() {
        Instant now = Instant.now();

        for (String symbol : monitored) {
            try {
                Double nse = fetchPriceFromApi(symbol + ".NS");
                Double bse = fetchPriceFromApi(symbol + ".BO");
                latest.put(symbol, stock);
                if (nse ==null) {
                    return bse;
                } else if (bse == null) {
                    return nse;
                } else {
                   return Math.abs(nse - bse);
                }

            } catch (Exception e) {
                System.out.println("Error fetching price for " + symbol + ": " + e.getMessage());
            }
        }
        return null;
    }

    private Double fetchPriceFromApi(String symbol) {

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
            double price = stock.getStockinfo().get(0).getPrice();
            return price;
        } catch (Exception e) {
            System.out.println("Error fetching price for " + symbol + ": " + e.getMessage());
            return null;
        }
    }


}
