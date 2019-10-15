package com.meiqinggao.mysql.stock.controller;

import com.meiqinggao.mysql.stock.model.SearchEntity;
import com.meiqinggao.mysql.stock.model.Stock;
import com.meiqinggao.mysql.stock.repository.StockConceptRepository;
import com.meiqinggao.mysql.stock.repository.StockRepository;
import com.meiqinggao.mysql.stock.utils.HttpUtils;
import com.meiqinggao.mysql.stock.utils.StockUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.Arrays;
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
        return HttpUtils.getDefaultHomeModel(model);
    }

    @GetMapping("/search/{concept}")
    public String searchConcept(@PathVariable("concept") String concept, Model model) {
        if (conceptLastTime.equals(concept)) {
            conceptLastTime = "";
            return "redirect:/";
        } else {
            model.addAttribute("stocks", searchRecentZtStockByConcept(concept));
            conceptLastTime = concept;
            return "concept";
        }
    }

    @PostMapping(value = "/search")
    public String search(@ModelAttribute SearchEntity searchEntity, Model model) {
        if (StringUtils.isEmpty(searchEntity.getText())) {
            return "/";
        }

        if (searchEntity.getText().contains("&")) {
            List<String> concepts = Arrays.asList(searchEntity.getText().split("&"));
            List<String> notExistConcepts = notExistConcepts(concepts);
            if (notExistConcepts.size() != 0) {
                String msg = "您输入的概念不存在：" + notExistConcepts;
                model.addAttribute("msg", msg);
                return "error";
            }
            model.addAttribute("stocks", searchStockByConcepts(concepts));
            return "concept";
        }

        if (StockUtils.getAllConcepts().contains(searchEntity.getText())) {
            model.addAttribute("stocks", searchRecentZtStockByConcept(searchEntity.getText()));
            return "concept";
        } else if (StockUtils.getAllStockNames().contains(searchEntity.getText())) {
            String msg = "您输入的是股票，暂时不支持该功能，请搜索概念或行业";
            model.addAttribute("msg", msg);
            return "error";
        } else {
            List<String> suggestedConcepts = StockUtils.getSuggestedConcepts(searchEntity.getText());
            if (suggestedConcepts.size() == 1) {
                model.addAttribute("stocks", searchRecentZtStockByConcept(suggestedConcepts.get(0)));
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

    private List<Stock> searchRecentZtStockByConcept(String concept) {
        List<String> stockCodes = stockConceptRepository.findStockCodesByConcept(concept);
        List<Stock> stocks = stockRepository.findStocksByCodeInAndRecentZtIsNotNull(stockCodes);

        stocks.sort(Comparator.comparing(Stock::getRecentZt).reversed());

        if (stocks.size() > 10) {
            stocks = stocks.subList(0, 10);
        }

        return stocks;
    }

    private List<Stock> searchStockByConcepts(List<String> concepts) {
        List<String> codes = searchStockCodesByConcepts(concepts);
        List<Stock> stocks = stockRepository.findStocksByCodeIn(codes);

        if (stocks.size() > 10) {
            stocks = stocks.subList(0, 10);
        }

        return stocks;
    }

    private List<String> searchStockCodesByConcepts(List<String> concepts) {
        List<String> stockCodes = new ArrayList<>();

        for (String concept : concepts) {
            List<String> stockCodesByConcept = stockConceptRepository.findStockCodesByConcept(concept);
            if (stockCodes.size() == 0) {
                stockCodes.addAll(stockCodesByConcept);
            } else {
                stockCodes.retainAll(stockCodesByConcept);
            }
        }

        return stockCodes;
    }

    private List<String> notExistConcepts(List<String> concepts) {
        ArrayList<String> notExistConcepts = new ArrayList<>();
        for (String concept : concepts) {
            if (!StockUtils.getAllConcepts().contains(concept.trim())) {
                notExistConcepts.add(concept);
            }
        }
        return notExistConcepts;
    }
}