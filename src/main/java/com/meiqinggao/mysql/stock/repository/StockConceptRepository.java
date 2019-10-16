package com.meiqinggao.mysql.stock.repository;

import com.meiqinggao.mysql.stock.model.StockConcept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StockConceptRepository extends JpaRepository<StockConcept, Long> {
    @Query(value = "select stock_code from stock_concept where concept= ?1", nativeQuery = true)
    List<String> findStockCodesByConcept(String concept);

    @Query(value = "select concept from stock_concept where stock_code= ?1 and concept_type != \"first\" ", nativeQuery = true)
    List<String> findStockConceptsByStock_code(String code);

    @Query(value = "select count(*) from stock_concept where stock_code= ?1 and concept = ?2 and concept_type = ?3 ", nativeQuery = true)
    Integer findStockCodeByUniqueness(String code, String concept, String concept_type);

    @Modifying
    @Query(value = "truncate table stock_concept", nativeQuery = true)
    void truncateStockConcept();
}