package com.meiqinggao.mysql.stock.repository;

import com.meiqinggao.mysql.stock.model.StockConcept;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockConceptRepository extends JpaRepository<StockConcept, Long> {
}
