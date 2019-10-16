package com.meiqinggao.mysql.stock.scheduler;

import com.meiqinggao.mysql.stock.model.ZhangTingStocks;
import com.meiqinggao.mysql.stock.utils.RealtimeDataParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RealtimeDataRetrieveScheduler {
    @Autowired
    private ZhangTingStocks zhangTingStocks;
    @Autowired
    private RealtimeDataParser realtimeDataParser;

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
        realtimeDataParser.retrieveAndParseStockData();
    }
}