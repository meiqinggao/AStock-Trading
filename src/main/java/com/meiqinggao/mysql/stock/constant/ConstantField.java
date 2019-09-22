package com.meiqinggao.mysql.stock.constant;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ConstantField {
    String OPEN = "open"; //开盘价
    String PRE_CLOSE = "pre_close"; //昨日收盘价
    String PRICE = "price"; //当前价格
    String HIGH = "high"; //今日最高价
    String LOW = "low";
    String VOLUME = "volume"; //成交量
    String AMOUNT = "amount"; //成交额
    String CHANGE_PERCENT = "chgPct"; //今日涨幅
    String AVERAGE_PRICE = "averPrice"; //均价
    String AVERAGE_PRICE_PERCENT = "averPricePct"; //均价涨幅
    String MA5 = "MA5";
    String MA10 = "MA10";
    String MA15 = "MA15";
    String MA20 = "MA20";
    String MA30 = "MA30";
    String MA60 = "MA60";
    String MA90 = "MA90";

    String SYMBOL = "symbol";
    String CODE = "code";
    String TRADE = "trade";
    String NAME = "name";
    String TURN_OVER_RATIO = "turnoverratio";

    //最低换手率
    double TURN_OVER_RATIO_THRESHHOLD = 2.0;
    //最低股价
    double LOW_STOCK_PRICE_THRESHHOLD = 4.0;
    //均价在基础均价的0.998 - 1.005之间振荡
    double AVERAGE_PRICE_INCREASE_PERCENT = 1.5;
    double AVERAGE_PRICE_DECREASE_PRECENT = -0.2;

    // 区间均价要求平稳 最大的均价和最小均价之间的差 小于 AVERAGE_PRICE_PCT_MAX_OVER_MIN_LIMIT （0.5%）
    double AVERAGE_PRICE_PCT_MAX_OVER_MIN_LIMIT = 1;

    //均价的涨幅在AVERAGE_PRICE_TOP_PCT 和 AVERAGE_PRICE_BOTTOM_PCT 之间
    double AVERAGE_PRICE_TOP_PCT = 3;
    double AVERAGE_PRICE_BOTTOM_PCT = -5;

    double PRICE_TOP_PCT = 3;
    //价格在一个点一下 假的 小幅震荡太多了
    double PRICE_BOTTOM_PCT = -5;
// double PRICE_BOTTOM_PCT = -2;

    //在离现价最近的30min内，最高价比最低价相差最多2%
// double PRICE_PCT_MAX_OVER_MIN_LIMIT = 2;
    double PRICE_PCT_MAX_OVER_MIN_LIMIT = 0.5;

    //当天股价的最高价达到了6个点，或者最低价低于-5，那么可以删除股票了 下次不用搜索了
    double HIGH_LIMIT_PCT = 4;
    double LOW_LIMIT_PCT = -10;

    double OPEN_HIGH_LIMIT_PCT = 3;
    double OPEN_LOW_LIMIT_PCT = -8;

    //min (head)时刻price最多比max (tail)时候高多少， default:-1.5%
//然后K线条分时图向下的趋势太明显了 需要排除
    double DROP_MAX_RANGE = -0.5;
    //min (head) 不能比 max (tail)时候低太多
    double RISE_MAX_RANGE = 2;

    //现价不能比均价高太多，设置最多高1%
// double PRICE_OVER_AVER_PRICE_LIMIT = 1.5;
    double PRICE_OVER_AVER_PRICE_LIMIT = 2;

    //最高价不能比现价高很多，不然就是高开低走的形态
    double HIGH_OVER_PRICE_LIMIT = 2;

    //股价低于５元时　低价股
    double LOW_PRICE = 5;

    //默认的需要计算的interval区间大小 60min
    int INTERVAL = 60;
    //开盘时候的INTERVAL大小，如果设置成INTERVAL 60太大了，意味着开盘一个小时内不可能有股票选出来
    int INTERVAL_AT_START = 40;
    //靠近现价的区间大小，用于计算最近一段时间内的价格平稳程度
    int INTERVAL_RECENT = 30;


    // 离现在的价格最近的一段时间（30min）,前一半的的平均价格不能比后一半的价格高很多，最好处后一半高
// 所以能往下，但是最多往下一点点，最好趋势向上
    double FIRST_HALF_AVER_PCT_OVER_SECOND_LIMIT = 0.2;

    // double PRICE_OVER_AVER_PRICE_CONFIDENCE_VALUE = 0.95;
    double PRICE_OVER_AVER_PRICE_CONFIDENCE_VALUE = 0.5;

    double PRICE_PCT_OUTSIDE_AVERAGE_SIZE = 0.25;

    //指数
    List<String> INDEX_LABELS = Arrays.asList("sh","sz", "hs300", "sz50", "cyb", "zxb", "zx300", "zh500");
    Map<String, String> INDEX_MAP = Stream.of(new String[][] {
            { "sh", "sh000001" },
            { "sz", "sz399001" },
            { "hs300", "sh000300" },
            { "sz50", "sh000016" },
            { "zxb", "sz399005" },
            { "cyb", "sz399006" },
            { "zx300", "sz399008" },
            { "zh500", "sh000905" },
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

}