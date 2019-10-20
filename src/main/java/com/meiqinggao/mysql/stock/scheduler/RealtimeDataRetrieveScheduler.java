package com.meiqinggao.mysql.stock.scheduler;

import com.meiqinggao.mysql.stock.model.ZhangTingConcepts;
import com.meiqinggao.mysql.stock.model.ZhangTingStocks;
import com.meiqinggao.mysql.stock.repository.StockRepository;
import com.meiqinggao.mysql.stock.utils.RealtimeDataParser;
import com.meiqinggao.mysql.stock.utils.StockUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

@Slf4j
@Service
public class RealtimeDataRetrieveScheduler {
    @Autowired
    private ZhangTingStocks zhangTingStocks;
    @Autowired
    private ZhangTingConcepts zhangTingConcepts;
    @Autowired
    private RealtimeDataParser realtimeDataParser;
    @Autowired
    private StockRepository stockRepository;

    @Scheduled(cron = "0 24 9 ? * 1-5", zone = "CTT")
    public void resetOnMorning() throws FileNotFoundException, UnsupportedEncodingException {
        zhangTingStocks.clear();
        zhangTingConcepts.clear();
        StockUtils.initConsecutiveZhangTings(stockRepository);
    }


    @Scheduled(cron = "*/30 25-59 9 ? * 1-5", zone = "CTT")
    @Scheduled(cron = "*/30 0-31 11 ? * 1-5", zone = "CTT")
    @Scheduled(cron = "*/30 * 10,13,14 ? * 1-5", zone = "CTT")
    @Scheduled(cron = "30 0 15 ? * 1-5", zone = "CTT")
    public void getAllZhangTingStocks() {
        realtimeDataParser.retrieveAndParseStockData();
    }
}
