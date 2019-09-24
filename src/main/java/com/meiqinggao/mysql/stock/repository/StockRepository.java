package com.meiqinggao.mysql.stock.repository;

import com.meiqinggao.mysql.stock.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Stock findByCode(String code);
}
