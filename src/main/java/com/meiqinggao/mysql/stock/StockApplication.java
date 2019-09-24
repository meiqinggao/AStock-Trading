package com.meiqinggao.mysql.stock;

import com.meiqinggao.mysql.stock.model.Response;
import com.meiqinggao.mysql.stock.model.Stock;
import com.meiqinggao.mysql.stock.repository.StockConceptRepository;
import com.meiqinggao.mysql.stock.repository.StockRepository;
import com.meiqinggao.mysql.stock.utils.SinaPreviousDayPriceParser;
import com.meiqinggao.mysql.stock.utils.StockDataRetriever;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;

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
//	String url = ConnectionUtils.getConceptUrl(”300236“);
//	String response = ConnectionUtils.getHttpEntityString(url, ”GBK“);
//	Document document = Jsoup.parse(response);
//	Elements elements = document.getElementsByClass(”J_popLink“);
//	List<String> concepts = elements.stream().map(element -> element.text()).collect(Collectors.toList());
//	//	System.out.println();
//
//	Map<String, String> stringStringMap = HtmlParser.parseHtmlField(new File(”file/300475_field.html“));
		Stock stock = new Stock();
		DateTime now = DateTime.now();
		stock.setCode("000001");
		stock.setStock_name("聚隆科技");
		stock.setRecent_zt(now.toDate());
		stockRepository.save(stock);

//	int PAGE_NUM = 50;
//	List<String> excludeStocks = stockRepository.findAll().stream().map(Stock::getCode).collect(Collectors.toList());
//
//	for (int i = 0; i < PAGE_NUM; i++) {
//	log.info(”Start received stock page: “ + i);
//	String stocksInfoForPage = stockDataRetriever.getPreviousDayStocksInfoForPage(i);
//	List<Stock> subStocks = SinaPreviousDayPriceParser.parseStockCodeAndName(stocksInfoForPage, excludeStocks);
//
//	if (subStocks.size() > 0) {
//	stockRepository.saveAll(subStocks);
//	}
//	}

//	List<String> allStockCodes = stockRepository.findAll().stream().map(Stock::getCode).collect(Collectors.toList());
//
//	List<StockConcept> stocks = new ArrayList<>();
//	int count = 0;
//	for (String code : allStockCodes) {
//	log.info(”Processed Count: “ + count);
//	String url = ConnectionUtils.getConceptUrl(code);
//	String response = ConnectionUtils.getHttpEntityString(url, ”GBK“);
//	Document document = Jsoup.parse(response);
//	Elements elements = document.getElementsByClass(”J_popLink“);
//	List<String> concepts = elements.stream().map(Element::text).collect(Collectors.toList());
//
//	for (String concept : concepts) {
//	StockConcept stockConcept = new StockConcept();
//	stockConcept.setConcept(concept);
//	stockConcept.setStock_code(code);
// stockConceptRepository.save(stockConcept);
////	stocks.add(stockConcept);
//	}
//	count++;
//	}


//	List<String> allStockCodes = stockRepository.findAll().stream().map(Stock::getCode).collect(Collectors.toList());
//
//	List<StockConcept> stocks = new ArrayList<>();
//	int count = 0;
//	for (String code : allStockCodes) {
//	log.info("Processed Count: " + count);
//	String url = ConnectionUtils.getFieldUrl(code);
//	String response = ConnectionUtils.getHttpEntityString(url, "GBK");
//	Map<String, String> fieldMap = HtmlParser.parseHtmlField(response);
//
//	StockConcept stockConcept = new StockConcept();
// stockConcept.setConcept(fieldMap.get("second"));
//	stockConcept.setStock_code(code);
// stockConceptRepository.save(stockConcept);
//
//
//
////	for (Map.Entry<String, String> fieldentry : fieldMap.entrySet()) {
////	StockConcept stockConcept = new StockConcept();
////	stockConcept.setConcept();
////	stockConcept.setStock_code(code);
//// stockConceptRepository.save(stockConcept);
////	}
//
////	for (String concept : concepts) {
////	StockConcept stockConcept = new StockConcept();
////	stockConcept.setConcept(concept);
////	stockConcept.setStock_code(code);
//// stockConceptRepository.save(stockConcept);
//////	stocks.add(stockConcept);
////	}
//	count++;
//	}

//	List<String> allStockCodes = stockRepository.findAll().stream().map(Stock::getCode).collect(Collectors.toList());
//
//	String date = "20190920";
//	DateTime now = DateTime.now();
//	Date dateDate = new Date(Integer.valueOf(date.substring(0,4)), Integer.valueOf(date.substring(4,6)), Integer.valueOf(date.substring(6,8)));
//	Response response = ConnectionUtils.getDateResponse(date);
//	if (response.getData().getItems().size() > 0){
//	parseJsonFile(allStockCodes, response, now);
//	}


//
//	stockConceptRepository.saveAll(stocks);

//	List<StockConcept> all = stockConceptRepository.findAll();
		System.out.println();
	}

	public void parseJsonFile(List<String> codes, Response response, DateTime dateTime) throws FileNotFoundException, UnsupportedEncodingException {
		List<String> fields = response.getData().getFields();
		int ts_codeIndex = fields.indexOf("ts_code");
		int pct_chgIndex = fields.indexOf("pct_chg");
		List<List<Object>> items = response.getData().getItems();
		for (List<Object> item : items) {
			double pct_chg = (double)item.get(pct_chgIndex);
			String code = item.get(ts_codeIndex).toString().substring(0, 6);
			if (pct_chg < 10.15 && pct_chg > 9.85 && codes.contains(code)) {

				Stock stock = stockRepository.findByCode(code);
				stock.setRecent_zt(dateTime.toDate());
				stockRepository.save(stock);
			}
		}
	}
}