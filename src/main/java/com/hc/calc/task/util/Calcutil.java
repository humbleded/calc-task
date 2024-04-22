package com.hc.calc.task.util;

import com.hc.calc.task.model.BaseData;

import java.util.List;

public class Calcutil {

    public static double getAvgSloop(List<BaseData> list) {
	double sum = 0;
	// 求出斜率和平均值
	for (int i = 1; i < list.size(); i++) {
	    double y = list.get(i).getValue() - list.get(i - 1).getValue();
	    long x = min2ms(Long.valueOf(list.get(i).getValueText()))
		    - min2ms(Long.valueOf(list.get(i - 1).getValueText()));
	    double s = y / x;
	    sum += s;
	}
	double as = sum / (list.size() - 1);
	return as;
    }

    public static long min2ms(long min) {
	return min * 600000;
    }

    public static String ms2min(long ms) {
	return String.valueOf((ms) / 600000);
    }
}
