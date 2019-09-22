package com.meiqinggao.mysql.stock;

import com.meiqinggao.mysql.stock.utils.ConnectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.stream.Collectors;

@EnableJpaRepositories
@SpringBootApplication
public class StockApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(StockApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		String url = ConnectionUtils.getConceptUrl("000669");
		String response = ConnectionUtils.getHttpEntityString(url, "GBK");
		Document document = Jsoup.parse(response);
		Elements elements = document.getElementsByClass("J_popLink");
		List<String> concepts = elements.stream().map(element -> element.text()).collect(Collectors.toList());
		System.out.println();
	}
}
