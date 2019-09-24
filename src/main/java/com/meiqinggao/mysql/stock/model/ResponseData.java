package com.meiqinggao.mysql.stock.model;

import lombok.Data;

import java.util.List;

@Data
public class ResponseData {
    private List<String> fields;
    private List<List<Object>> items;
}