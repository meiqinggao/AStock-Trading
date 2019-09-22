package com.meiqinggao.mysql.stock.utils;


import com.meiqinggao.mysql.stock.constant.ConstantField;
import com.meiqinggao.mysql.stock.constant.StockURL;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;

@Slf4j
public class ConnectionUtils {
    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    public static byte[] getHttpEntityByteArray(String url) {
        int count = 0;
        byte[] responseString = null;
        while (count < 3 && responseString == null) {
            responseString = getHttpEntityByteArrayOnce(url);
            count++;
        }
        return responseString;
    }

    public static String getHttpEntityString(String url) {
        int count = 0;
        String responseString = "";
        while (count < 3 && Strings.isBlank(responseString)) {
            responseString = getHttpEntityStringOnce(url);
            count++;
        }
        return responseString;
    }

    public static String getHttpEntityString(String url, String charset) {
        int count = 0;
        String responseString = "";
        while (count < 3 && Strings.isBlank(responseString)) {
            responseString = getHttpEntityStringOnce(url, charset);
            count++;
        }
        return responseString;
    }



    public static String getPostHttpEntityString(String url, String date) {
        int count = 0;
        String responseString = "";
        while (count < 3 && Strings.isBlank(responseString)) {
            responseString = getPostHttpEntityStringOnce(url, date);
            count++;
        }
        if (Strings.isBlank(responseString)) {
            log.info("Date:" + date + " response is null and fail and count is " + count);
        } else {
            log.info("Date:" + date + " response is successful and count is " + count);
        }
        return responseString;
    }

    private static byte[] getHttpEntityByteArrayOnce(String url) {
        CloseableHttpResponse response = null;
        try {
//发送get请求
            HttpGet request = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(5000).setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000).build();
            request.setConfig(requestConfig);

            int fetch_times = 5;
            while (fetch_times > 0 && (response == null
                    || response.getStatusLine().getStatusCode() != HttpStatus.OK.value())) {
                responseClose(response);
                response = httpClient.execute(request);
                fetch_times--;
            }

            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                return EntityUtilsWrapper.toByteArray(response.getEntity());
            } else {
                log.error("get请求提交失败:" + url);
            }
        } catch (IOException e) {
            log.error("get请求提交失败:" + url, e);
        }finally {
            responseClose(response);
        }
        return null;
    }

    private static String getHttpEntityStringOnce(String url) {
        return getHttpEntityStringOnce(url, Charset.defaultCharset().toString());
    }


    private static String getHttpEntityStringOnce(String url, String charset) {
        CloseableHttpResponse response = null;
        try {
//发送get请求`
            HttpGet request = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(5000).setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .build();
            request.setConfig(requestConfig);

            int fetch_times = 5;
            while (fetch_times > 0 && (response == null
                    || response.getStatusLine().getStatusCode() != HttpStatus.OK.value())) {
                responseClose(response);
                response = httpClient.execute(request);
                fetch_times--;
            }

            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                return EntityUtilsWrapper.toString(response.getEntity(), charset);
            } else {
                log.error("get请求提交失败:" + url);
            }
        } catch (IOException e) {
            log.error("get请求提交失败:" + url, e);
        } finally {
            responseClose(response);
        }
        return null;
    }

    private static String getPostHttpEntityStringOnce(String url, String date) {
        CloseableHttpResponse response = null;
        try {
//发送get请求
            String body = "{\n" +
                    "\t\"api_name\":\"daily\",\n" +
                    "\t\"token\": \"57b24baa835028cffdd9485b3533ea132fcb983b41a6adad631b3afa\",\n" +
                    "\t\"params\": {\"trade_date\":\""
                    + date
                    + "\"},\n" +
                    "\t\"fields\":\"ts_code,open,high,low,close,pre_close,pct_chg\"\n" +
                    "}";
            HttpPost request = new HttpPost("http://api.waditu.com");
            request.addHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(body));
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(5000).setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000).build();
            request.setConfig(requestConfig);

            int fetch_times = 5;
            while (fetch_times > 0 && (response == null
                    || response.getStatusLine().getStatusCode() != HttpStatus.OK.value())) {
                responseClose(response);
                response = httpClient.execute(request);
                fetch_times--;
            }

            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                return EntityUtilsWrapper.toString(response.getEntity());
            } else {
                log.error("get请求提交失败:" + url);
            }
        } catch (IOException e) {
            log.error("get请求提交失败:" + url, e);
        } finally {
            responseClose(response);
        }
        return null;
    }


    public static String getSinaRealTimeUrl(Collection<String> subSymbols) {
        String joinCodes = Strings.join(subSymbols, ',');
        return String.format(StockURL.SINA_REALTIME_URL, joinCodes);
    }

    public static String getCodeDateUrl(String code, String date) {
        return String.format(StockURL.CODE_DATE_URL, date.substring(0, 4), date, addStockCodeDigitPrefix(code));
    }

    public static String getSinaDayPriceUrl(int page) {
        return String.format(StockURL.SINA_DAY_PRICE_URL, page);
    }

    public static String getTushareDayAllUrl(String date) {
        return String.format(StockURL.TUSHARE_DAY_ALL_URL, date.substring(0, 6), date);
    }

    public static String getConceptUrl(String code) {
        return String.format(StockURL.CONCEPT_URL, code);
    }

    public static String addStockCodeDigitPrefix(String code) {
        if (ConstantField.INDEX_LABELS.contains(code)) {
            return ConstantField.INDEX_MAP.get(code);
        }
        if (code.length() != 6) {
            return code;
        }
        if (code.startsWith("6") || code.startsWith("5") || code.startsWith("9")) {
            return "0" + code;
        }
        return "1" + code;
    }

    public static String addStockCodePrefix(String code) {
        if (ConstantField.INDEX_LABELS.contains(code)) {
            return ConstantField.INDEX_MAP.get(code);
        }

        if (code.length() != 6) {
            return code;
        }
// logic from tushare _code_to_symbol
        if (code.startsWith("6") || code.startsWith("5") || code.startsWith("9") || code.startsWith("11") || code.startsWith("13")) {
            return "sh" + code;
        }

        return "sz" + code;
    }

    private static void responseClose(CloseableHttpResponse response) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}