package com.meiqinggao.mysql.stock.controller;

import com.meiqinggao.mysql.stock.model.SearchEntity;
import com.meiqinggao.mysql.stock.model.Stock;
import com.meiqinggao.mysql.stock.repository.StockConceptRepository;
import com.meiqinggao.mysql.stock.repository.StockRepository;
import com.meiqinggao.mysql.stock.utils.StockUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Controller
public class SearchController {
    private String conceptLastTime = "";
    @Autowired
    private StockConceptRepository stockConceptRepository;
    @Autowired
    private StockRepository stockRepository;

    @GetMapping({"/", "/search"})
    public String home(Model model) {
        SearchEntity searchEntity = new SearchEntity();
        searchEntity.setType("All");
        model.addAttribute("searchEntity", searchEntity);
        return "home";
    }

    @GetMapping("/search/{concept}")
    public String searchConcept(@PathVariable("concept") String concept, Model model) {
        if (conceptLastTime.equals(concept)) {
            conceptLastTime = "";
            return "redirect:/";
        } else {
            model.addAttribute("stocks", searchStock(concept));
            conceptLastTime = concept;
            return "concept";
        }
    }

    @PostMapping(value = "/search")
    public String search(@ModelAttribute SearchEntity searchEntity, Model model) {

        if (StockUtils.getAllConcepts().contains(searchEntity.getText())) {
            model.addAttribute("stocks", searchStock(searchEntity.getText()));
            return "concept";
        } else if (StockUtils.getAllStockNames().contains(searchEntity.getText())) {
            String msg = "您输入的是股票，暂时不支持该功能，请搜索概念或行业";
            model.addAttribute("msg", msg);
            return "error";
        } else {
            List<String> suggestedConcepts = StockUtils.getSuggestedConcepts(searchEntity.getText());
            if (suggestedConcepts.size() == 1) {
                model.addAttribute("stocks", searchStock(suggestedConcepts.get(0)));
                return "concept";
            } else if (suggestedConcepts.size() > 0) {
                model.addAttribute("suggests", suggestedConcepts);
                return "home";
            } else {
                String msg = "输入的股票或概念不存在，返回搜索界面重新输入";
                model.addAttribute("msg", msg);
                return "error";
            }
        }
    }

    private List<Stock> searchStock(String concept) {
        List<String> stockCodes = stockConceptRepository.findStockCodesByConcept(concept);
        List<Stock> stocks = stockRepository.findStocksByCodeInAndRecentZtIsNotNull(stockCodes);

        stocks.sort(Comparator.comparing(Stock::getRecentZt).reversed());

        if (stocks.size() > 10) {
            stocks = stocks.subList(0, 10);
        }

        return stocks;
    }
}