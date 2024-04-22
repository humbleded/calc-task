package com.hc.calc.task.config;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Holger
 * @date 2018/4/19
 */
public class JsonDateValueProcessor implements JsonValueProcessor {
    private String pattern = "yyyy-MM-dd HH:mm:ss";

    @Override
    public Object processArrayValue(Object value, JsonConfig config) {
        return process(value);
    }

    @Override
    public Object processObjectValue(String key, Object value, JsonConfig config) {
        return process(value);
    }

    private Object process(Object value) {
        if (value instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(value);
        }
        return value == null ? "" : value.toString();
    }
}
