package com.meiqinggao.mysql.stock.utils;

import com.meiqinggao.mysql.stock.model.IPBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class IpUtils {
    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    //    private static List<IPBean> validIpBeans = crawl().stream().filter(IpUtils::isValid).collect(Collectors.toList());;
    private static List<IPBean> validIpBeans;
    public static HttpHost getHttpHost() {
        if (validIpBeans == null || validIpBeans.size() == 0) {
            throw new RuntimeException("No Proxy IP can be used!");
        }
        return new HttpHost(validIpBeans.get(0).getIp(), validIpBeans.get(0).getPort());
    }

    public static List<IPBean> crawl() {
//        HttpGet request = new HttpGet("https://www.xicidaili.com");
//        RequestConfig requestConfig = RequestConfig.custom()
//                .setSocketTimeout(5000).setConnectTimeout(5000)
//                .setConnectionRequestTimeout(5000).build();
//        request.setConfig(requestConfig);
//
//        CloseableHttpResponse response = null;
//        try {
//            response = httpClient.execute(request);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        String responseString;
//        if (response == null || response.getStatusLine() == null) {
//            throw new RuntimeException("Response is null!");
//        } else if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
//            responseString = EntityUtilsWrapper.toString(response.getEntity());
//        } else {
//            throw new RuntimeException("response is not OK");
//        }

        String html = "";
        Document document = Jsoup.parse(html);
        Elements eles = document.selectFirst("table").select("tr");
        ArrayList<IPBean> ipList = new ArrayList<>();
        for (int i = 0; i < eles.size(); i++){
            if (i == 0) continue;
            Element ele = eles.get(i);
            String ip = ele.children().get(1).text();
            int port = Integer.parseInt(ele.children().get(2).text().trim());
            String typeStr = ele.children().get(5).text().trim();

            if ("HTTP".equalsIgnoreCase(typeStr)){
                ipList.add(new IPBean(ip, port, "HTTP"));
            } else {
                ipList.add(new IPBean(ip, port, "HTTPS"));
            }
        }
        return ipList;
    }

    public static boolean isValid(IPBean ipBean) {
        HttpGet request = new HttpGet("http://basic.10jqka.com.cn/002419/concept.html");
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(5000).setConnectTimeout(5000)
                .setProxy(new HttpHost(ipBean.getIp(), ipBean.getPort()))
                .setConnectionRequestTimeout(5000).build();
        request.setConfig(requestConfig);

        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            log.info(ipBean + " is NOT valid with IOException");
            return false;
        }

        if (response == null || response.getStatusLine() == null) {
            log.info(ipBean + " is NOT valid with null response");
            return false;
        }
        boolean isValid = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
        if (isValid) {
            log.info(ipBean + " is VALID");
        } else {
            log.info(ipBean + " is NOT valid with status " + response.getStatusLine().getStatusCode());
        }
        return isValid;
    }
}
