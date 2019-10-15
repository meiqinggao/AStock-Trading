package com.meiqinggao.mysql.stock.controller;

import com.meiqinggao.mysql.stock.repository.StockRepository;
import com.meiqinggao.mysql.stock.utils.HttpUtils;
import com.meiqinggao.mysql.stock.utils.StockUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

@Slf4j
@Controller
@AllArgsConstructor
public class RefreshController {
    @Autowired
    private StockRepository stockRepository;

    @GetMapping("/refresh/{days}")
    public String refreshStockUpLimit(@PathVariable("days") int days, Model model) throws FileNotFoundException, UnsupportedEncodingException {
        StockUtils.saveStockDate_ZT(stockRepository, days);
        return HttpUtils.getDefaultHomeModel(model);
    }

    @GetMapping("/refresh")
    public String refreshStockUpLimitForOneDay(Model model) throws FileNotFoundException, UnsupportedEncodingException {
        StockUtils.saveStockDate_ZT(stockRepository, 2);
        return HttpUtils.getDefaultHomeModel(model);
    }
}