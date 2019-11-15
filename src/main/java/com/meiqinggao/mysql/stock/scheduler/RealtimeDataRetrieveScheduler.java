package com.meiqinggao.mysql.stock.scheduler;

import com.meiqinggao.mysql.stock.model.ZhangTingConcepts;
import com.meiqinggao.mysql.stock.model.ZhangTingStocks;
import com.meiqinggao.mysql.stock.repository.StockRepository;
import com.meiqinggao.mysql.stock.utils.RealtimeDataParser;
import com.meiqinggao.mysql.stock.utils.StockUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

@Slf4j
@RequiredArgsConstructor
@Service
public class RealtimeDataRetrieveScheduler {
    private final ZhangTingStocks zhangTingStocks;
    private final ZhangTingConcepts zhangTingConcepts;
    private final RealtimeDataParser realtimeDataParser;
    private final StockRepository stockRepository;

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
