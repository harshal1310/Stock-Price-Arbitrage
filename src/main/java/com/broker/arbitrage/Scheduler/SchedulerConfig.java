package com.broker.arbitrage.Scheduler;

import com.broker.arbitrage.Service.PriceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerConfig {
    private final PriceService priceService;

    public SchedulerConfig(PriceService priceService) {
        this.priceService = priceService;
    }

    // runs every 60 sec
    @Scheduled(fixedRate = 30000)
    public void updatePrices() {
       Double diff = priceService.fetchPricesDiff();

    }
}
