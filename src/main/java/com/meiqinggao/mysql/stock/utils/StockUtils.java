package com.meiqinggao.mysql.stock.utils;

import com.meiqinggao.mysql.stock.constant.ConceptType;
import com.meiqinggao.mysql.stock.constant.StockSource;
import com.meiqinggao.mysql.stock.model.Response;
import com.meiqinggao.mysql.stock.model.Stock;
import com.meiqinggao.mysql.stock.model.StockConcept;
import com.meiqinggao.mysql.stock.repository.StockConceptRepository;
import com.meiqinggao.mysql.stock.repository.StockRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class StockUtils {
    public static void saveAllStockCodeAndName(StockRepository stockRepository, StockDataRetriever stockDataRetriever) {
        int PAGE_NUM = 50;

        for (int i = 0; i < PAGE_NUM; i++) {
            List<String> excludeStocks = stockRepository.findAll().stream().map(Stock::getCode).collect(Collectors.toList());
            log.info("Start received stock page: " + i);
            String stocksInfoForPage = stockDataRetriever.getPreviousDayStocksInfoForPage(i);
            List<Stock> subStocks = SinaPreviousDayPriceParser.parseStockCodeAndName(stocksInfoForPage, excludeStocks);
            if (subStocks.size() > 0) {
                stockRepository.saveAll(subStocks);
            }
        }
    }

    public static void saveAllStockConcept(StockRepository stockRepository, StockConceptRepository stockConceptRepository){
        List<String> allStockCodes = stockRepository.findAll().stream().map(Stock::getCode).collect(Collectors.toList());
        Set<String> excludeStockConcepts = stockConceptRepository.findAll().stream()
                .filter(stockConcept ->ConceptType.CONCEPT.equals(stockConcept.getConcept_type()))
                .map(StockConcept::getStock_code)
                .collect(Collectors.toSet());
        log.info(excludeStockConcepts + " size is " + excludeStockConcepts.size());
        int count = 0;
        for (String code : allStockCodes) {
            if (excludeStockConcepts.contains(code)) {
                continue;
            }
            log.info("Processed concept count: " + count);

            String url = ConnectionUtils.getConceptUrl(code);
            String response = ConnectionUtils.getHttpEntityString(url, "GBK");
            List<String> concepts = HtmlParser.parseHtmlConcepts(response);

            for (String concept : concepts) {
                StockConcept stockConcept = new StockConcept();
                stockConcept.setConcept(concept);
                stockConcept.setConcept_type(ConceptType.CONCEPT);
                stockConcept.setStock_code(code);
                stockConcept.setSource(StockSource.THS);
                stockConceptRepository.save(stockConcept);
            }
            count++;
        }
    }

    public static void saveAllStockField(StockRepository stockRepository, StockConceptRepository stockConceptRepository) {
        List<String> allStockCodes = stockRepository.findAll().stream().map(Stock::getCode).collect(Collectors.toList());
        Set<String> excludeStockFields = stockConceptRepository.findAll().stream()
                .filter(stockConcept ->ConceptType.FIRST.equals(stockConcept.getConcept_type()))
                .map(StockConcept::getStock_code)
                .collect(Collectors.toSet());

        int count = 0;
        for (String code : allStockCodes) {
            log.info("Processed field count: " + count);
            String fieldUrl = ConnectionUtils.getFieldUrl(code);
            String response = ConnectionUtils.getHttpEntityString(fieldUrl, "GBK");
            Map<String, String> fieldMap = HtmlParser.parseHtmlField(response);
            for (Map.Entry<String, String> fieldentry : fieldMap.entrySet()) {
                StockConcept stockConcept = new StockConcept();
                stockConcept.setStock_code(code);
                stockConcept.setConcept(fieldentry.getValue());
                stockConcept.setConcept_type(fieldentry.getKey());
                stockConceptRepository.save(stockConcept);
            }

            count++;
	    }
    }

    public static void saveStockDate_ZT(StockRepository stockRepository, int daysBeforeToday) throws FileNotFoundException, UnsupportedEncodingException {
        List<String> codes = stockRepository.findAll().stream().map(Stock::getCode).collect(Collectors.toList());
        DateTime dateTime = new DateTime(DateTimeZone.forID("Asia/Shanghai"));
        dateTime = dateTime.minusDays(1);

        int days = 0;
        String lastTradeDate = "";
        while (true) {
            int dayOfWeek = dateTime.getDayOfWeek();
            if (dayOfWeek >= 1 && dayOfWeek <= 5) {
                String date = dateTime.toString("YYYYMMdd");
                Response response = ConnectionUtils.getDailyStockDetailedInfo(date);
                if (response.getData().getItems().size() > 0){
                    parseDailyStockResponse(stockRepository, codes, response, dateTime);
                    log.info("date that has response is " + date);
                    days++;
                    if (days == 1) {
                        lastTradeDate = date;
                        log.info("lastTradeDate is " + lastTradeDate);
                    }
                }
            }
            if (days == daysBeforeToday) {
                break;
            }
            dateTime = dateTime.minusDays(1);
        }
    }


    public static void parseDailyStockResponse(StockRepository stockRepository, List<String> codes, Response response, DateTime dateTime) throws FileNotFoundException, UnsupportedEncodingException {
        List<String> fields = response.getData().getFields();
        int ts_codeIndex = fields.indexOf("ts_code");
        int pct_chgIndex = fields.indexOf("pct_chg");
        int pre_closeIndex = fields.indexOf("pre_close");
        int closeIndex = fields.indexOf("close");
        List<List<Object>> items = response.getData().getItems();
        for (List<Object> item : items) {
            String code = item.get(ts_codeIndex).toString().substring(0, 6);
            double pct_chg = (double)item.get(pct_chgIndex);
            double pre_close = (double)item.get(pre_closeIndex);
            double close = (double)item.get(closeIndex);
            if (isZhangTingForCode(code, pct_chg, close, pre_close) && codes.contains(code)) {
                Stock stock = stockRepository.findByCode(code);
                if (stock.getRecent_zt() == null || stock.getRecent_zt().before(dateTime.toDate())){
                    stock.setRecent_zt(dateTime.toDate());
                    stockRepository.save(stock);
                }
            }
        }
    }

    public static boolean isZhangTingForCode(String code, double pct_chg, double close, double pre_close) {
        //涨幅小于9.85的直接返回false，提高判断速度
        if (pct_chg < 9.85) {
            return false;
        }

        BigDecimal close_decimal = new BigDecimal(close).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal pre_close_decimal = new BigDecimal(pre_close).setScale(2, RoundingMode.HALF_EVEN);
        // 688开头表示科创板 科创板涨幅20%，主板10%
        if (!code.startsWith("688")) {
            return isZhangting(1.1, pre_close_decimal, close_decimal);
        } else {
            return isZhangting(1.2, pre_close_decimal, close_decimal);
        }
    }

    private static boolean isZhangting(double zhangTingFactor, BigDecimal pre_close_decimal, BigDecimal close_decimal) {
        BigDecimal zhangTingPrice = pre_close_decimal.multiply(new BigDecimal(zhangTingFactor)).setScale(2, RoundingMode.HALF_EVEN);
        return zhangTingPrice.compareTo(close_decimal) == 0;
    }
}