package com.meiqinggao.mysql.stock.repository;

import com.meiqinggao.mysql.stock.model.StockConcept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StockConceptRepository extends JpaRepository<StockConcept, Long> {
    @Query(value = "select stock_code from stock_concept where concept= ?1", nativeQuery = true)
    List<String> findStockCodesByConcept(String concept);
}