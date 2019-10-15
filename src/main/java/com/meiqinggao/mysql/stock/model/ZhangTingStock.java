package com.meiqinggao.mysql.stock.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ZhangTingStock {
    private String code;
    private String name;
    private String time;
}
