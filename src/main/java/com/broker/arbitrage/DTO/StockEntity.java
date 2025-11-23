package com.broker.arbitrage.DTO;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="stocks")
@Data
public class StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String symbol;

    @Column(nullable = false)
    private String name;
}
