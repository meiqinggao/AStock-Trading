package com.meiqinggao.mysql.stock.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "stock_concept")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockConcept {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String stock_code;
    private String concept;
    private String concept_type;
    private String source;
}