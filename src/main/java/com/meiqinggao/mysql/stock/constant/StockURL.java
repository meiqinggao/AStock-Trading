package com.meiqinggao.mysql.stock.constant;

public interface StockURL {
    String SINA_REALTIME_URL = "http://hq.sinajs.cn/list=%s";
    String CODE_DATE_URL ="http://quotes.money.163.com/cjmx/%s/%s/%s.xls";
    //SINA_DAY_PRICE_URL 也能获得实时信息，但每次最多只能拿到100条数据，对于实时性比较高的情况不适用
    String SINA_DAY_PRICE_URL = "http://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeData?num=80&sort=code&asc=0&node=hs_a&symbol=&_s_r_a=page&page=%s";
    String TUSHARE_DAY_ALL_URL = "http://file.tushare.org/tsdata/h/%s/%s.csv";
    String TUSHARE_PRO_Daily_URL = "http://api.waditu.com";
    String CONCEPT_URL = "http://basic.10jqka.com.cn/%s/concept.html";
    String FIELD_URL = "http://basic.10jqka.com.cn/%s/field.html";
    String TUSHARE_PRO_Daily_BODY = "{\n" +
            "\t\"api_name\":\"daily\",\n" +
            "\t\"token\": \"57b24baa835028cffdd9485b3533ea132fcb983b41a6adad631b3afa\",\n" +
            "\t\"params\": {\"trade_date\":\""
            + "%s"
            + "\"},\n" +
            "\t\"fields\":\"ts_code,open,high,low,close,pre_close,pct_chg,vol\"\n" +
            "}";

    String CONCEPT_RIZE = "q.10jqka.com.cn/gn/";
    String INDUSTRY_RISE_PAGE1 = "http://q.10jqka.com.cn/thshy/index/field/199112/order/desc/page/1/ajax/1/%20HTTP/1.1";
    String INDUSTRY_RISE_PAGE2 = "http://q.10jqka.com.cn/thshy/index/field/199112/order/desc/page/2/ajax/1/%20HTTP/1.1";

}