package com.meiqinggao.mysql.stock.scheduler;

import com.meiqinggao.mysql.stock.repository.StockRepository;
import com.meiqinggao.mysql.stock.utils.StockUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

@Slf4j
@Service
public class RefreshDailyStockScheduler {
    @Autowired
    private StockRepository stockRepository;

    //刷新每天涨停股票
    @Scheduled(cron = "0 0 8,16 ? * 1-5", zone = "CTT")
    public void refreshDailyStockUpLimit() throws FileNotFoundException, UnsupportedEncodingException {
        StockUtils.refreshStockDate_ZT(stockRepository, 15);
    }
}