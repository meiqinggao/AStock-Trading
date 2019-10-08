package com.meiqinggao.mysql.stock.controller;

import com.meiqinggao.mysql.stock.repository.StockRepository;
import com.meiqinggao.mysql.stock.utils.StockUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

@Slf4j
@Controller
@AllArgsConstructor
public class RefreshController {
    @Autowired
    private StockRepository stockRepository;

    @GetMapping("/refresh")
    public void refreshStockUpLimit() throws FileNotFoundException, UnsupportedEncodingException {
        StockUtils.saveStockDate_ZT(stockRepository, 15);
    }
}