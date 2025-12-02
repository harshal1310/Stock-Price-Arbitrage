package com.broker.arbitrage.Controller;

import com.broker.arbitrage.Model.Stock;
import com.broker.arbitrage.Entity.StockEntity;
import com.broker.arbitrage.Repository.StockRepo;
import com.broker.arbitrage.Service.PriceService;
import com.broker.arbitrage.Service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@RestController
public class StockController {

    private final PriceService service;
    private final StockRepo stockRepo;
    private static final int fetchInterval = 60000;
    private final StockService stockService;

    public StockController(PriceService service, StockRepo stockRepo, StockService stockService) {
        this.service = service;
        this.stockRepo = stockRepo;
        this.stockService = stockService;
    }


    @PostMapping("/addstock")
    public ResponseEntity<?> addStockToMonitor(@RequestParam String symbol) {
        System.out.println("Adding stock to database");
        if (symbol == null || symbol.isBlank()) {
            return new ResponseEntity<>("Please check Stock symbol", HttpStatus.BAD_REQUEST);
        }

       Optional<StockEntity> stock = stockService.getStock(symbol);

        List<StockEntity> stocks = stockService.getStocks();

        if(!stocks.isEmpty()) {
            System.out.println("sTOCKS : " + stocks);
        }
        if (!stock.isPresent()) {
            return new ResponseEntity<>("Stock not found", HttpStatus.NOT_FOUND);
        }


        System.out.println("Adding stock to symbol: " + symbol);
        service.addStock(symbol);
        return  new ResponseEntity<>("Added Successfully", HttpStatus.OK);
    }

    @GetMapping(value = "/prices-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamPrices(@RequestParam(defaultValue = "0") int page) {
        SseEmitter emitter = new SseEmitter();

        new Thread(() -> {
            try {
                while (true) {
                    Collection<Stock> stocks = service.getProcessStock(page, 5);
                    System.out.println("stocks: " + stocks);
                    emitter.send(stocks);  // Push latest prices to frontend

                    Thread.sleep(fetchInterval);   // Every 60 sec
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;

    }
}
