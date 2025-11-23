package com.broker.arbitrage.Controller;

import com.broker.arbitrage.Model.Stock;
import com.broker.arbitrage.Service.PriceService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Collection;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class StockController {

    private final PriceService service;
    private static final int fetchInterval = 60000;

    public StockController(PriceService service) {
        this.service = service;
    }


    @PostMapping("/addstock")
    public Map<String, String> addStock(@RequestParam String symbol) {
        System.out.println("Adding stock to symbol: " + symbol);
        service.addStock(symbol);
        return Map.of("status", "added1");
    }

    @GetMapping(value = "/prices-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamPrices() {
        SseEmitter emitter = new SseEmitter();

        new Thread(() -> {
            try {
                while (true) {
                    Collection<Stock> prices = service.getLatestPrices();
                    System.out.println("pricess: " + prices);
                    emitter.send(prices);  // Push latest prices to frontend

                    Thread.sleep(fetchInterval);   // Every 60 sec
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;

    }
}
