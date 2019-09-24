package com.meiqinggao.mysql.stock.utils;

import com.meiqinggao.mysql.stock.constant.ConceptType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class HtmlParser {

    public static List<String> parseHtmlConcepts(String conceptHtml) {
        if (conceptHtml == null) {
            return new ArrayList<>();
        }
        Document document = Jsoup.parse(conceptHtml);
        Elements elements = document.getElementsByClass("J_popLink");
        return elements.stream().map(Element::text).collect(Collectors.toList());
    }

    public static List<String> parseHtmlConcepts(File conceptFile) {
        Document document = null;
        try {
            document = Jsoup.parse(conceptFile, "GBK");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert document != null;

        Elements elements = document.getElementsByClass("J_popLink");
        return elements.stream().map(Element::text).collect(Collectors.toList());
    }

    public static Map<String, String> parseHtmlField(String fieldHtml) {
        Document document = Jsoup.parse(fieldHtml);
        return parseHtmlFieldFromDocument(document);
    }

    public static Map<String, String> parseHtmlField(File fieldFile) {
        Document document = null;
        try {
            document = Jsoup.parse(fieldFile, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert document != null;
        return parseHtmlFieldFromDocument(document);
    }

    private static Map<String, String> parseHtmlFieldFromDocument(Document document) {
        Elements elements = document.getElementsByClass("tip f14");
        List<String> fields = elements.stream().map(Element::text).sorted(Comparator.comparingInt(String::length).reversed()).collect(Collectors.toList());
        String fieldsString = fields.get(0);
        int bracketIndex = fieldsString.indexOf('（');
        fieldsString = fieldsString.substring(0, bracketIndex);
        List<String> fieldList = Arrays.stream(fieldsString.split("--")).map(String::trim).collect(Collectors.toList());
        return new HashMap<String, String>() {
            {
                put(ConceptType.FIRST, fieldList.get(0));
                put(ConceptType.SECOND, fieldList.get(1));
                put(ConceptType.THIRD, fieldList.get(2));
            }
        };
    }
}