package com.meiqinggao.mysql.stock.utils;

import com.meiqinggao.mysql.stock.model.RealtimeStock;
import com.meiqinggao.mysql.stock.model.ZhangTingConcepts;
import com.meiqinggao.mysql.stock.model.ZhangTingStock;
import com.meiqinggao.mysql.stock.model.ZhangTingStocks;
import com.meiqinggao.mysql.stock.repository.StockConceptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RealtimeDataParser {
    private List<String> excludeConcepts = Arrays.asList("标普道琼斯A股", "富时罗素概念股", "机构重仓", "参股新三板");
    private final ZhangTingStocks zhangTingStocks;
    private final ZhangTingConcepts zhangTingConcepts;
    private final StockConceptRepository stockConceptRepository;

    public void retrieveAndParseStockData() {
	log.info("Start retrive real time stock data...");	
        String retrievedStockData = StockDataRetriever.retrieveAllSinaRealtimeStocksData(StockUtils.getAllSymbols());
        List<RealtimeStock> realtimeStocks = StockParser.parseSourceDataToStocks(retrievedStockData);
        List<String> codesZhangTing = realtimeStocks.stream().filter(RealtimeStock::isZhangTing).map(RealtimeStock::getCode).collect(Collectors.toList());
        List<String> toRemoveCodes = zhangTingStocks.keySet().stream().filter(code -> !codesZhangTing.contains(code)).collect(Collectors.toList());
        for (String code : codesZhangTing) {
            if (!zhangTingStocks.containsKey(code)){
                int zhangTingCount = 1;
                if (StockUtils.getConsecutiveZhangTings().containsKey(code)) {
                    zhangTingCount = StockUtils.getConsecutiveZhangTings().get(code) + 1;
                }
                zhangTingStocks.put(code, new ZhangTingStock(code, StockUtils.getAllStocksMap().get(code), DateTime.now().toString("HH:mm"), zhangTingCount));

                List<String> concepts = stockConceptRepository.findStockConceptsByStock_code(code);
                for (String concept : concepts) {
                    if (excludeConcepts.contains(concept.trim())) {
                        continue;
                    }

                    if (zhangTingConcepts.containsKey(concept)) {
                        zhangTingConcepts.get(concept).add(code);
                    } else {
                        HashSet<String> codeSets = new HashSet<>();
                        codeSets.add(code);
                        zhangTingConcepts.put(concept, codeSets);
                    }
                }
            }
        }

        for (String toRemoveCode : toRemoveCodes) {
            zhangTingStocks.remove(toRemoveCode);
            zhangTingConcepts.values().forEach(codes -> codes.remove(toRemoveCode));
        }
    	log.info("Finish retrieving real time data.");
    }
}
