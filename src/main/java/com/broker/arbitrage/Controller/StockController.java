package com.broker.arbitrage.Controller;

import com.broker.arbitrage.Model.StockInfo;
import com.broker.arbitrage.Service.PriceService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StockController {

    private final PriceService service;

    public StockController(PriceService service) {
        this.service = service;
    }


    @PostMapping("/addstock")
    public Map<String, String> addStock(@RequestParam String symbol) {
        System.out.println("Adding stock to symbol: " + symbol);
        service.addStock(symbol);
        return Map.of("status", "added1");
    }

    @GetMapping("/prices")
    public Collection<StockInfo> getPrices() {
        return service.getLatestPrices();
    }

}
