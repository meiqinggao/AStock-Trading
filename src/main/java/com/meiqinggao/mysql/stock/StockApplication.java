package com.meiqinggao.mysql.stock;

import com.meiqinggao.mysql.stock.repository.StockConceptRepository;
import com.meiqinggao.mysql.stock.repository.StockRepository;
import com.meiqinggao.mysql.stock.utils.SinaPreviousDayPriceParser;
import com.meiqinggao.mysql.stock.utils.StockDataRetriever;
import com.meiqinggao.mysql.stock.utils.StockUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@EnableJpaRepositories
@SpringBootApplication
public class StockApplication implements ApplicationRunner {
	@Autowired
	private StockConceptRepository stockConceptRepository;
	@Autowired
	private StockRepository stockRepository;
	@Autowired
	private StockDataRetriever stockDataRetriever;
	@Autowired
	private SinaPreviousDayPriceParser parser;

	public static void main(String[] args) {
		SpringApplication.run(StockApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		StockUtils.init(stockRepository, stockConceptRepository);

//		StockUtils.refreshAllStockCodeAndName(stockRepository, stockDataRetriever);
		StockUtils.refreshAllStockConcept(stockRepository, stockConceptRepository, 100);
		StockUtils.refreshAllStockField(stockRepository, stockConceptRepository, 100);
//		StockUtils.refreshStockDate_ZT(stockRepository, 20);
	}


}