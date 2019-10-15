package com.meiqinggao.mysql.stock.model;

import lombok.Data;

@Data
public class RealtimeStock {
    private double high;
    private double low;
    private double price;
    private double volume;
    private double change_percent;
    private double pre_close;
    private String code;
    private boolean isZhangTing;
}