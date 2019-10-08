package com.meiqinggao.mysql.stock.controller;

import com.meiqinggao.mysql.stock.model.SearchEntity;
import com.meiqinggao.mysql.stock.model.Stock;
import com.meiqinggao.mysql.stock.repository.StockConceptRepository;
import com.meiqinggao.mysql.stock.repository.StockRepository;
import com.meiqinggao.mysql.stock.utils.StockUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Controller
@AllArgsConstructor
public class SearchController {
//    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    @Autowired
    private StockConceptRepository stockConceptRepository;
    @Autowired
    private StockRepository stockRepository;

    @GetMapping("/")
    public String home(Model model) {
        SearchEntity searchEntity = new SearchEntity();
        searchEntity.setType("All");
        model.addAttribute("searchEntity", searchEntity);
        return "home";
    }

    @PostMapping(value = "/search")
    public String search(@ModelAttribute SearchEntity searchEntity, Model model) {
        if (StockUtils.getAllConcepts().contains(searchEntity.getText())) {
            List<String> stockCodes = stockConceptRepository.findStockCodesByConcept(searchEntity.getText());
            List<Stock> stocks = stockRepository.findStocksByCodeInAndRecentZtIsNotNull(stockCodes);

            stocks.sort(Comparator.comparing(Stock::getRecentZt).reversed());

            if (stocks.size() > 10) {
                stocks = stocks.subList(0, 10);
            }
            model.addAttribute("stocks", stocks);
            return "concept";
        } else if (StockUtils.getAllStockNames().contains(searchEntity.getText())) {
            String msg = "您输入的是股票，暂时不支持该功能，请搜索概念或行业";
            model.addAttribute("msg", msg);
            return "error";
        } else {
            String msg = "输入的股票或概念不存在，返回搜索界面重新输入";
            model.addAttribute("msg", msg);
            return "error";
        }
    }
}