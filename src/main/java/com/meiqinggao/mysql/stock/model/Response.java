package com.meiqinggao.mysql.stock.model;

import lombok.Data;

@Data
public class Response {
    private String request_id;
    private int code;
    private String msg;
    private ResponseData data;
}