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

@EnableJpaRepositories
@SpringBootApplication
@Slf4j
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
//		List<IPBean> ipBeans = HtmlParser.parseHtmlFileIpBeans("file/ip1.html");
//		List<IPBean> validIpBeans = ipBeans.stream().filter(IpUtils::isValid).collect(Collectors.toList());
//		IPBean ipBean = new IPBean("120.83.123.169", 9999, "HTTP");
//		IPBean ipBean = new IPBean("1.197.204.49", 9999, "HTTP");
//
//		boolean isvalid = IpUtils.isValid(ipBean);

//		StockUtils.saveAllStockCodeAndName(stockRepository, stockDataRetriever);
//		StockUtils.saveAllStockConcept(stockRepository, stockConceptRepository);
//		StockUtils.saveAllStockField(stockRepository, stockConceptRepository);
		StockUtils.saveStockDate_ZT(stockRepository, 15);

		System.out.println();
	}


}