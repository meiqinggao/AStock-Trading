package com.meiqinggao.mysql.stock.utils;

import com.meiqinggao.mysql.stock.model.RealtimeStock;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;


public class StockParser {
    public static List<RealtimeStock> parseSourceDataToStocks(String stocksInfo) {
        if (Strings.isBlank(stocksInfo)) {
            return new ArrayList<>();
        }
        String[] stocks = stocksInfo.split(";");
        ArrayList<RealtimeStock> realtimeStocks = new ArrayList<>();
        for (String stockInfo : stocks) {
            String trimStockInfo = Strings.trimToNull(stockInfo);
            if (Strings.isBlank(trimStockInfo)) {
                continue;
            }
            realtimeStocks.add(parseSinaRealtimeDataOneStockInfoToStock(trimStockInfo));
        }

        return realtimeStocks;
    }

    private static RealtimeStock parseSinaRealtimeDataOneStockInfoToStock(String stockInfo) {
        if (Strings.isBlank(stockInfo)) {
            return null;
        }
        String[] splitStockInfo = stockInfo.trim().split("=");
        if (splitStockInfo.length < 1 || splitStockInfo[0].length() <= 11) {
            return null;
        }
        RealtimeStock realtimeStock = new RealtimeStock();
        String code = splitStockInfo[0].substring(13);
        realtimeStock.setCode(code);

        String[] values = splitStockInfo[1].replace("\"", "").split(",");

        double pre_close = Double.parseDouble(values[2]);
        double price = Double.parseDouble(values[3]);
        double high = Double.parseDouble(values[4]);
        double low = Double.parseDouble(values[5]);
        double volume = Double.parseDouble(values[8]);

        double changePct = (price - pre_close) / pre_close * 100.0;

        boolean zhangTingForCode = StockUtils.isZhangTingForCode(code, changePct, price, pre_close);

        realtimeStock.setChange_percent(changePct);
        realtimeStock.setHigh(high);
        realtimeStock.setLow(low);
        realtimeStock.setPrice(price);
        realtimeStock.setVolume(volume);
        realtimeStock.setPre_close(pre_close);
        realtimeStock.setZhangTing(zhangTingForCode);

        return realtimeStock;
    }
}