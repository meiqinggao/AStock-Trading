package com.meiqinggao.mysql.stock.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
public class EntityUtilsWrapper {
    public static String toString(HttpEntity entity) {
        return toString(entity, Charset.defaultCharset().toString());
    }

    public static String toString(HttpEntity entity, String charset) {
        if (entity == null) {
            return null;
        }
        int parse_times = 5;
        while (parse_times > 0) {
            try {
                return EntityUtils.toString(entity, charset);
            } catch (IOException e) {
                e.printStackTrace();
                log.info("Failed to use EntityUtils to parse entity, parse_times: " + parse_times);
            }
            parse_times--;
        }
        return null;
    }

    public static byte[] toByteArray(HttpEntity entity) {
        if (entity == null) {
            return null;
        }
        int parse_times = 5;
        while (parse_times > 0) {
            try {
                return EntityUtils.toByteArray(entity);
            } catch (IOException e) {
                e.printStackTrace();
                log.info("Failed to use EntityUtils to parse entity, parse_times: " + parse_times);
            }
            parse_times--;
        }
        return null;
    }
}
