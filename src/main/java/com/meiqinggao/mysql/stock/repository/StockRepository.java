package com.meiqinggao.mysql.stock.repository;

import com.meiqinggao.mysql.stock.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Stock findByCode(String code);

    List<Stock> findStocksByCodeInAndRecentZtIsNotNull(List<String> codes);

    List<Stock> findStocksByCodeIn(List<String> codes);
}