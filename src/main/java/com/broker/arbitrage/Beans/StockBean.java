package com.broker.arbitrage.Beans;

import com.broker.arbitrage.Model.StockInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StockBean {

    @Bean
    public StockInfo stock() {
        return new StockInfo();
    }
}
