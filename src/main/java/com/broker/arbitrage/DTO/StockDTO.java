package com.broker.arbitrage.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockDTO {
    @NotBlank(message = "Stock name should not empty")
    private String name;

    @NotBlank(message = "Stock symbol should not empty")
    private String symbol;

}
