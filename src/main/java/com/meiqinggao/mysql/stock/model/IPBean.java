package com.meiqinggao.mysql.stock.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IPBean {
    private String ip;
    private int port;
    private String type;

    @Override
    public String toString() {
        return "new IPBean(\"" + ip + "\", " + port + ", \"" + type + "\")";
    }
}
