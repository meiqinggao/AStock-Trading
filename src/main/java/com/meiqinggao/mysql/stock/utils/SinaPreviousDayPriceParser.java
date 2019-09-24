package com.meiqinggao.mysql.stock.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meiqinggao.mysql.stock.constant.ConstantField;
import com.meiqinggao.mysql.stock.model.Stock;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component("sinaPreviousDayPriceParser")
@Qualifier("sinaPreviousDayPriceParser")
@AllArgsConstructor
public class SinaPreviousDayPriceParser{
    private static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }

    public static List<Stock> parseStockCodeAndName(String stockInfo, List<String> excludeStocks) {
        if (Strings.isBlank(stockInfo) || "null".equalsIgnoreCase(stockInfo)) {
            return new ArrayList<>();
        }

        List<Stock> stocks = new ArrayList<>();
        //用！！来做分隔符
        String splitableString = stockInfo.replace("[{", "{").replace("}]", "}").replace("},", "}!!");

        String[] previsouDayStockInfo = splitableString.split("!!");
        log.info("parse previous day price, size: " + previsouDayStockInfo.length);
        for (String s : previsouDayStockInfo) {

            Map<String, Object> stockMap = new HashMap<>();
            // convert JSON string to Map
            try {
                stockMap = mapper.readValue(s, new TypeReference<Map<String, Object>>() {});
            } catch (IOException e) {
                e.printStackTrace();
                log.info("Fail to convert json string to map for previous stock data");
            }

            String code = stockMap.get(ConstantField.CODE).toString();
            String name = stockMap.get(ConstantField.NAME).toString();
            Stock stock = new Stock();
            stock.setCode(code);
            stock.setStock_name(name);
            stocks.add(stock);
        }

        return stocks.stream()
                .filter(stock -> !excludeStocks.contains(stock.getCode()))
//                .filter(stock -> !stock.getCode().startsWith("688"))
                .collect(Collectors.toList());
    }
}
