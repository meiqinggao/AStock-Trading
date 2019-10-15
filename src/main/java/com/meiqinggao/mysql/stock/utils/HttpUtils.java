package com.meiqinggao.mysql.stock.utils;

import com.meiqinggao.mysql.stock.model.SearchEntity;
import org.springframework.ui.Model;

public class HttpUtils {
    public static String getDefaultHomeModel(Model model) {
        SearchEntity searchEntity = new SearchEntity();
        searchEntity.setType("All");
        model.addAttribute("searchEntity", searchEntity);
        return "home";
    }
}
