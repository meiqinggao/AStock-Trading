package com.meiqinggao.mysql.stock.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Slf4j
@Component
public class StockDataRetriever {
    private static final int BATCH_SIZE = 500;

    public static String retrieveAllSinaRealtimeStocksData(List<String> symbols) {
        //初始化设定 StringBuilder大小，提高性能。每一个item 大约250个字符
        StringBuilder stringBuilder = new StringBuilder(250*symbols.size());
        int startIndex = 0;
        int codesSize = symbols.size();
        log.info("Size need to retrive is " + codesSize);
        while (startIndex < codesSize) {
            int endIndex = (startIndex + BATCH_SIZE) < codesSize ? startIndex + BATCH_SIZE : codesSize;
            log.info("retrieve real time size: " + (endIndex - startIndex));
            List<String> subSymbols = symbols.subList(startIndex, endIndex);
            log.info("start to retrive subsinaRealtime");
            String subStocksInfo = retrieveSubSinaRealtimeStocksData(subSymbols);
            log.info("finish to retrive subsinaRealtime");
            stringBuilder.append(subStocksInfo);
            startIndex = startIndex + BATCH_SIZE;
        }
        log.info("the size of stringBuilder of realTime stocks Data is " + (double)stringBuilder.toString().length());
        return stringBuilder.toString();
    }

    public static String retrieveSubSinaRealtimeStocksData(Collection<String> subSymbols) {
        String realtimeUrl = ConnectionUtils.getSinaRealTimeUrl(subSymbols);
        return ConnectionUtils.getHttpEntityString(realtimeUrl);
    }

    public static String getPreviousDayStocksInfoForPage(int page) {
        String url = ConnectionUtils.getSinaDayPriceUrl(page);
        return ConnectionUtils.getHttpEntityString(url);
    }
}