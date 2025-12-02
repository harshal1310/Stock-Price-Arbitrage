package com.broker.arbitrage.Service;

import com.broker.arbitrage.DTO.StockDTO;
import com.broker.arbitrage.Entity.StockEntity;
import com.broker.arbitrage.Model.Stock;
import com.broker.arbitrage.Repository.StockRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public Page<StockEntity> getProcessStock(int page, int size) {
        return stockRepo.findAll(PageRequest.of(page, size));
    }
    public void addStock(StockDTO stockdto) {
        StockEntity stock = new StockEntity();
        stock.setName(stockdto.getName());
        stock.setSymbol(stockdto.getSymbol());
        stockRepo.save(stock);
    }

    public void deleteStock(String symbol) {
        getStock(symbol).ifPresent(stockRepo::delete);
    }
}
