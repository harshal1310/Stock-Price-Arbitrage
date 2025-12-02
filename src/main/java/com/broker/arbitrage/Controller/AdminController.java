package com.broker.arbitrage.Controller;

import com.broker.arbitrage.DTO.StockDTO;
import com.broker.arbitrage.Model.Stock;
import com.broker.arbitrage.Service.StockService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AdminController {
    StockService stockService;

    @PostMapping("/add-stock-db")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<String> addStockDb(@Valid @RequestBody StockDTO stock) {
        try {
            stockService.addStock(stock);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Added stock", HttpStatus.OK);
    }

    @DeleteMapping("/delete-stock")
    public ResponseEntity<String> deleteStock(String symbol) {
        try {
            stockService.deleteStock(symbol);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }
        return new ResponseEntity<>("Deleted stock", HttpStatus.OK);
    }
}
