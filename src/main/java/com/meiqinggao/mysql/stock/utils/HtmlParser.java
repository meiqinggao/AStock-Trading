package com.meiqinggao.mysql.stock.utils;

import com.meiqinggao.mysql.stock.constant.ConceptType;
import com.meiqinggao.mysql.stock.model.IPBean;
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

    public static List<IPBean> parseHtmlFileIpBeans(String pathToFile) {
        Document document = parseHtmlFileToDocument(pathToFile);
        assert document != null;
        Elements eles = document.selectFirst("table").select("tr");
        ArrayList<IPBean> ipList = new ArrayList<>();
        for (int i = 0; i < eles.size(); i++){
            if (i == 0) continue;
            Element ele = eles.get(i);
            String ip = ele.children().get(1).text();
            int port = Integer.parseInt(ele.children().get(2).text().trim());
            String typeStr = ele.children().get(5).text().trim();

            if ("HTTP".equalsIgnoreCase(typeStr)){
                ipList.add(new IPBean(ip, port, "HTTP"));
            } else {
                ipList.add(new IPBean(ip, port, "HTTPS"));
            }
        }
        return ipList;
    }

    public static Document parseHtmlFileToDocument(String pathToFile) {
        Document document = null;
        try {
            return Jsoup.parse(new File(pathToFile), "GBK");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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