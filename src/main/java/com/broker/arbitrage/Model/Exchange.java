package com.broker.arbitrage.Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Exchange {
    BO,
    NS;

    @JsonCreator
    public static Exchange fromString(String key) {
        try {
            return Exchange.valueOf(key);
        } catch (Exception e) {
            return null; // or Exchange.UNKNOWN
        }
    }
}
