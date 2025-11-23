package com.broker.arbitrage.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockInfo {
    @JsonProperty("regularMarketPrice")
    double price;
    String name;
    String symbol;
    Exchange exchange;
    private Instant time;
    Double priceDiff;
}
