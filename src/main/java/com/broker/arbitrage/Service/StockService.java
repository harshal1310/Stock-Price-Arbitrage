package com.broker.arbitrage.Service;

import com.broker.arbitrage.DTO.StockEntity;
import com.broker.arbitrage.Repository.StockRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class StockService {

    private StockRepo stockRepo;

    public StockService(StockRepo stockRepo) {
        this.stockRepo = stockRepo;
    }

    public Optional<StockEntity>getStock(String symbol) {
        return stockRepo.findBySymbol(symbol);
    }

    public List<StockEntity> getStocks() {
        return stockRepo.findAll();
    }
}
