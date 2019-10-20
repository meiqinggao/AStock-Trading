package com.meiqinggao.mysql.stock.utils;

import com.meiqinggao.mysql.stock.constant.ConceptType;
import com.meiqinggao.mysql.stock.constant.ConstantField;
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
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class StockUtils {
    public static void refreshAllStockCodeAndName(StockRepository stockRepository) {
        int PAGE_NUM = 50;

        for (int i = 0; i < PAGE_NUM; i++) {
            List<String> excludeStocks = stockRepository.findAll().stream().map(Stock::getCode).collect(Collectors.toList());
            log.info("Start received stock page: " + i);
            String stocksInfoForPage = StockDataRetriever.getPreviousDayStocksInfoForPage(i);
            List<Stock> subStocks = SinaPreviousDayPriceParser.parseStockCodeAndName(stocksInfoForPage, excludeStocks);
            if (subStocks.size() > 0) {
                stockRepository.saveAll(subStocks);
            }
        }
    }

    public static void refreshAllStockConcept(StockRepository stockRepository, StockConceptRepository stockConceptRepository, int sleepTimeInMillis){
        List<String> allStockCodes = stockRepository.findAll().stream().map(Stock::getCode).collect(Collectors.toList());
        Set<String> excludeStockConcepts = stockConceptRepository.findAll().stream()
                .filter(stockConcept ->ConceptType.CONCEPT.equals(stockConcept.getConcept_type()))
                .map(StockConcept::getStock_code)
                .collect(Collectors.toSet());
        log.info("Already available size is " + excludeStockConcepts.size());
        int count = 0;
        for (String code : allStockCodes) {
            if (excludeStockConcepts.contains(code)) {
                continue;
            }
            log.info("Processed concept count: " + count);
            sleepInMillis(sleepTimeInMillis);
            String url = ConnectionUtils.getConceptUrl(code);
            String response = ConnectionUtils.getHttpEntityString(url, "GBK");
            List<String> concepts = HtmlParser.parseHtmlConcepts(response);

            for (String concept : concepts) {
                StockConcept stockConcept = new StockConcept();
                stockConcept.setConcept(concept);
                stockConcept.setConcept_type(ConceptType.CONCEPT);
                stockConcept.setStock_code(code);
                stockConcept.setSource(StockSource.THS);

                if (stockConceptRepository.findStockCodeByUniqueness(stockConcept.getStock_code(), stockConcept.getConcept(), stockConcept.getConcept_type()) == 0) {
                    stockConceptRepository.save(stockConcept);
                }

            }
            count++;
        }
    }

    public static void refreshAllStockField(StockRepository stockRepository, StockConceptRepository stockConceptRepository, int sleepTimeInMillis) {
        List<String> allStockCodes = stockRepository.findAll().stream().map(Stock::getCode).collect(Collectors.toList());
        Set<String> excludeStockFields = stockConceptRepository.findAll().stream()
                .filter(stockConcept ->ConceptType.FIRST.equals(stockConcept.getConcept_type()))
                .map(StockConcept::getStock_code)
                .collect(Collectors.toSet());

        int count = 0;
        for (String code : allStockCodes) {
            if (excludeStockFields.contains(code)) {
                continue;
            }
            log.info("Processed field count: " + count);
            sleepInMillis(sleepTimeInMillis);
            String fieldUrl = ConnectionUtils.getFieldUrl(code);
            String response = ConnectionUtils.getHttpEntityString(fieldUrl, "GBK");
            Map<String, String> fieldMap = HtmlParser.parseHtmlField(response);
            for (Map.Entry<String, String> fieldEntry : fieldMap.entrySet()) {
                StockConcept stockConcept = new StockConcept();
                stockConcept.setStock_code(code);
                stockConcept.setConcept(fieldEntry.getValue());
                stockConcept.setConcept_type(fieldEntry.getKey());
                stockConcept.setSource(StockSource.THS);
                if (code.equals("603868")) {
                    System.out.println();
                }

                if (stockConceptRepository.findStockCodeByUniqueness(stockConcept.getStock_code(), stockConcept.getConcept(), stockConcept.getConcept_type()) == 0) {
                    stockConceptRepository.save(stockConcept);
                }
            }

            count++;
        }
    }

    public static void refreshStockDate_ZT(StockRepository stockRepository, int daysBeforeToday) throws FileNotFoundException, UnsupportedEncodingException {
        List<String> codes = stockRepository.findAll().stream().map(Stock::getCode).collect(Collectors.toList());
        DateTime dateTime = new DateTime(DateTimeZone.forID("Asia/Shanghai"));

        int days = 0;
        String lastTradeDate = "";
        while (true) {
            int dayOfWeek = dateTime.getDayOfWeek();
            if (dayOfWeek >= 1 && dayOfWeek <= 5) {
                String date = dateTime.toString("YYYYMMdd");
                Response response = ConnectionUtils.getDailyStockDetailedInfo(date);
                if (response.getData().getItems().size() > 0){
                    saveDailyZhangTingStocksToDB(stockRepository, codes, response, dateTime);
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

    private static Map<String, Integer> initPreviousZhangTingStocks(StockRepository stockRepository) throws FileNotFoundException, UnsupportedEncodingException {
        List<String> codes = stockRepository.findAll().stream().map(Stock::getCode).collect(Collectors.toList());
        DateTime dateTime = new DateTime(DateTimeZone.forID("Asia/Shanghai"));
        boolean firstTime = true;
        HashMap<String, Integer> zhangTingCount = new HashMap<>();
        Set<Object> lastZhangTings = new HashSet<>();
        Set<Object> consecutiveZhangTings = new HashSet<>();

        while (true) {
            int dayOfWeek = dateTime.getDayOfWeek();
            if (dayOfWeek >= 1 && dayOfWeek <= 5) {
                String date = dateTime.toString("YYYYMMdd");
                Response response = ConnectionUtils.getDailyStockDetailedInfo(date);
                if (response.getData().getItems().size() > 0){
                    List<String> dailyZhangTingCodes = getDailyZhangTingCodes(codes, response);
                    if (firstTime) {
                        dailyZhangTingCodes.forEach(code -> {
                            zhangTingCount.put(code, 1);
                            consecutiveZhangTings.add(code);
                        });
                        firstTime = false;
                    } else {
                        for (String code : dailyZhangTingCodes) {
                            if (lastZhangTings.contains(code) && zhangTingCount.containsKey(code)) {
                                zhangTingCount.put(code, zhangTingCount.get(code) + 1);
                                consecutiveZhangTings.add(code);
                            }
                        }
                    }
                }
            }
            dateTime = dateTime.minusDays(1);
            if (consecutiveZhangTings.size() == 0 && !firstTime) {
                break;
            }
            lastZhangTings.clear();
            lastZhangTings.addAll(consecutiveZhangTings);
            consecutiveZhangTings.clear();
        }

        return zhangTingCount;
    }


    public static void saveDailyZhangTingStocksToDB(StockRepository stockRepository, List<String> codes, Response response, DateTime dateTime) throws FileNotFoundException, UnsupportedEncodingException {
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
                if (stock.getRecentZt() == null || stock.getRecentZt().before(dateTime.toDate())){
                    stock.setRecentZt(dateTime.toDate());
                    stockRepository.save(stock);
                }
            }
        }
    }

    public static List<String> getDailyZhangTingCodes(List<String> codes, Response response) {
        List<String> fields = response.getData().getFields();
        int ts_codeIndex = fields.indexOf("ts_code");
        int pct_chgIndex = fields.indexOf("pct_chg");
        int pre_closeIndex = fields.indexOf("pre_close");
        int closeIndex = fields.indexOf("close");
        List<List<Object>> items = response.getData().getItems();
        LinkedList<String> zhangTingCodes = new LinkedList<>();
        for (List<Object> item : items) {
            String code = item.get(ts_codeIndex).toString().substring(0, 6);
            double pct_chg = (double)item.get(pct_chgIndex);
            double pre_close = (double)item.get(pre_closeIndex);
            double close = (double)item.get(closeIndex);
            if (isZhangTingForCode(code, pct_chg, close, pre_close) && codes.contains(code)) {
                zhangTingCodes.add(code);
            }
        }
        return zhangTingCodes;
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

    private static List<String> allStockNames;
    private static List<String> allConcepts;
    private static List<String> allSymbols;
    private static Map<String, String> allStocksMap;
    private static Map<String, Integer> consecutiveZhangTings;

    public static void init(StockRepository stockRepository, StockConceptRepository stockConceptRepository) throws FileNotFoundException, UnsupportedEncodingException {
        List<Stock> allStocks = stockRepository.findAll();
        allSymbols = allStocks.stream().map(stock -> addStockCodePrefix(stock.getCode())).collect(Collectors.toList());
        allStocksMap = allStocks.stream().collect(Collectors.toMap(Stock::getCode, Stock::getStockName));
        allStockNames = new ArrayList<>(allStocksMap.values());
        allConcepts = stockConceptRepository.findAll().stream().map(StockConcept::getConcept).distinct().collect(Collectors.toList());
        initConsecutiveZhangTings(stockRepository);
    }

    public static void initConsecutiveZhangTings(StockRepository stockRepository) throws FileNotFoundException, UnsupportedEncodingException {
        consecutiveZhangTings = StockUtils.initPreviousZhangTingStocks(stockRepository);
    }

    public static List<String> getAllStockNames() {
        return allStockNames;
    }

    public static List<String> getAllConcepts() {
        return allConcepts;
    }

    public static List<String> getAllSymbols() {
        return allSymbols;
    }

    public static Map<String, String> getAllStocksMap() {
        return allStocksMap;
    }

    public static Map<String, Integer> getConsecutiveZhangTings() {
        return consecutiveZhangTings;
    }

    public static List<String> getSuggestedConcepts(String concept){
        return allConcepts.stream().filter(item -> item.contains(concept)).limit(10).collect(Collectors.toList());
    }

    private static String addStockCodePrefix(String code) {
        if (ConstantField.INDEX_LABELS.contains(code)) {
            return ConstantField.INDEX_MAP.get(code);
        }

        if (code.length() != 6) {
            return code;
        }
// logic from tushare _code_to_symbol
        if (code.startsWith("6") || code.startsWith("5") || code.startsWith("9") || code.startsWith("11") || code.startsWith("13")) {
            return "sh" + code;
        }

        return "sz" + code;
    }

    private static void sleepInMillis(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}