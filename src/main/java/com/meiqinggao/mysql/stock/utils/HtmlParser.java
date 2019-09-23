package com.meiqinggao.mysql.stock.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HtmlParser {

    public static List<String> parseHtmlConcepts(String conceptHtml) {
        Document document = Jsoup.parse(conceptHtml);
        Elements elements = document.getElementsByClass("J_popLink");
        return elements.stream().map(element -> element.text()).collect(Collectors.toList());
    }

    public static List<String> parseHtmlField(String fieldHtml) {
        Document document = Jsoup.parse(fieldHtml);
        return new ArrayList<>();
    }
}
