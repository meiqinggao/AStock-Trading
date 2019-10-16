package com.meiqinggao.mysql.stock.scheduler;

import com.meiqinggao.mysql.stock.model.RealtimeStock;
import com.meiqinggao.mysql.stock.model.ZhangTingStock;
import com.meiqinggao.mysql.stock.model.ZhangTingStocks;
import com.meiqinggao.mysql.stock.utils.StockDataRetriever;
import com.meiqinggao.mysql.stock.utils.StockParser;
import com.meiqinggao.mysql.stock.utils.StockUtils;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RealtimeDataRetrieveScheduler {
    @Autowired
    private ZhangTingStocks zhangTingStocks;

    @Scheduled(cron = "0 0 9 ? * 1-5", zone = "CTT")
    public void resetOnMorning() {
        zhangTingStocks.clear();
    }

    @Scheduled(cron = "30 25 9 ? * 1-5", zone = "CTT")
    @Scheduled(cron = "30 26 9 ? * 1-5", zone = "CTT")
    @Scheduled(cron = "30 30-59/1 9 ? * 1-5", zone = "CTT")
    @Scheduled(cron = "30 */1 10 ? * 1-5", zone = "CTT")
    @Scheduled(cron = "30 0-30/1 11 ? * 1-5", zone = "CTT")
    @Scheduled(cron = "30 */1 13,14 ? * 1-5", zone = "CTT")
    @Scheduled(cron = "30 0 15 ? * 1-5", zone = "CTT")
    public void getAllZhangTingStocks() {
        retrieveAndParseStockData();
    }

    private void retrieveAndParseStockData() {
        String retrievedStockData = StockDataRetriever.retrieveAllSinaRealtimeStocksData(StockUtils.getAllSymbols());
        List<RealtimeStock> realtimeStocks = StockParser.parseSourceDataToStocks(retrievedStockData);
        List<String> codesZhangTing = realtimeStocks.stream().filter(RealtimeStock::isZhangTing).map(RealtimeStock::getCode).collect(Collectors.toList());
        List<String> toRemoveCodes = zhangTingStocks.keySet().stream().filter(code -> !codesZhangTing.contains(code)).collect(Collectors.toList());
        for (String code : codesZhangTing) {
            if (!zhangTingStocks.containsKey(code)){
                zhangTingStocks.put(code, new ZhangTingStock(code, StockUtils.getAllStocksMap().get(code), DateTime.now().toString("HH:mm")));
            }
        }

        toRemoveCodes.forEach(code -> zhangTingStocks.remove(code));
    }
}