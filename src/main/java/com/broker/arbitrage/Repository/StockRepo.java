package com.broker.arbitrage.Repository;

import com.broker.arbitrage.DTO.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepo extends JpaRepository<StockEntity, Long> {
    Optional<StockEntity> findBySymbol(String symbol);
}
