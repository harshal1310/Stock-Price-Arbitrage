package com.broker.arbitrage.Beans;

import com.broker.arbitrage.Model.Stock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StockBean {

    @Bean
    public Stock stock() {
        return new Stock();
    }
}
