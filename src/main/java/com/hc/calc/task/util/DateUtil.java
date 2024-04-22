package com.hc.calc.task.util;

import com.hc.calc.task.config.BaseConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Holger
 * @date 2018/5/10
 */
public class DateUtil {

    public static Date getPrevSpan(Date date) {
	return new Date(date.getTime() - BaseConfig.PREV_SPAN);
    }


    public static String format(String format, Date date) {
	SimpleDateFormat dt = new SimpleDateFormat(format);
	return dt.format(date);
    }

    public static Date parse(String format, String date) {
	SimpleDateFormat dt = new SimpleDateFormat(format);
	try {
	    return dt.parse(date);
	} catch (ParseException e) {
	    return null;
	}
    }

    public static boolean isMonthFirstDay() {
	Calendar c = Calendar.getInstance();
	int today = c.get(Calendar.DAY_OF_MONTH);
	return today == 1;
    }

    public static boolean isYearFirstDay() {
	Calendar c = Calendar.getInstance();
	int today = c.get(Calendar.DAY_OF_YEAR);
	return today == 1;
    }

    public static Date getNowDate() {
		/* 默认取服务器日期 */
	Calendar c = Calendar.getInstance();
	c.set(Calendar.HOUR_OF_DAY, 0);
	c.set(Calendar.MINUTE, 0);
	c.set(Calendar.SECOND, 0);
	return c.getTime();
    }

    public static Date addDay(Date date, int i) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(date);
	calendar.add(5, i);
	date = calendar.getTime();
	return date;
    }

    public static Date addSecond(Date date, int i) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(date);
	calendar.add(Calendar.SECOND, i);
	date = calendar.getTime();
	return date;
    }

    public static Date addHour(Date date, int i) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(date);
	calendar.add(Calendar.HOUR, i);
	date = calendar.getTime();
	return date;
    }

    public static Date addMonth(Date date, int i) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(date);
	calendar.add(Calendar.MONTH, i);
	date = calendar.getTime();
	return date;
    }

    public static Date addYear(Date date, int i) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(date);
	calendar.add(Calendar.YEAR, i);
	date = calendar.getTime();
	return date;
    }

    public static Date getFirstDayOfMonth(Date date) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(date);
	calendar.set(Calendar.DAY_OF_MONTH, 1);
	calendar.set(Calendar.HOUR_OF_DAY, 0);
	calendar.set(Calendar.MINUTE, 0);
	calendar.set(Calendar.SECOND, 0);
	return calendar.getTime();
    }

    public static Date getFirstDayOfWeek(Date date) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(date);
	calendar.set(Calendar.DAY_OF_WEEK, 2);
	calendar.set(Calendar.HOUR_OF_DAY, 0);
	calendar.set(Calendar.MINUTE, 0);
	calendar.set(Calendar.SECOND, 0);
	return calendar.getTime();
    }

    public static Date getFirstDayOfYear(Date date) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(date);
	calendar.set(Calendar.DAY_OF_YEAR, 1);
	calendar.set(Calendar.HOUR_OF_DAY, 0);
	calendar.set(Calendar.MINUTE, 0);
	calendar.set(Calendar.SECOND, 0);
	return calendar.getTime();
    }

    public static Date getFirstDayOfQuarter(Date date) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(date);
	int currentMonth = calendar.get(Calendar.MONTH) + 1;
	if (currentMonth >= 1 && currentMonth <= 3) {
	    calendar.set(Calendar.MONTH, 0);
	} else if (currentMonth >= 4 && currentMonth <= 6) {
	    calendar.set(Calendar.MONTH, 3);
	} else if (currentMonth >= 7 && currentMonth <= 9) {
	    calendar.set(Calendar.MONTH, 6);
	} else if (currentMonth >= 10 && currentMonth <= 12) {
	    calendar.set(Calendar.MONTH, 9);
	}
	calendar.set(Calendar.DATE, 1);
	calendar.set(Calendar.HOUR_OF_DAY, 0);
	calendar.set(Calendar.MINUTE, 0);
	calendar.set(Calendar.SECOND, 0);
	return calendar.getTime();
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param startDate
     *            时间参数 1 格式：1990-01-01 12:00:00
     * @param nowDate
     *            时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceTime(Date startDate, Date nowDate) {
	long day = 0;
	long hour = 0;
	long min = 0;
	long sec = 0;
	long time1 = startDate.getTime();
	long time2 = nowDate.getTime();
	long diff;
	if (time1 < time2) {
	    diff = time2 - time1;
	} else {
	    diff = time1 - time2;
	}
	day = diff / (24 * 60 * 60 * 1000);
	hour = (diff / (60 * 60 * 1000) - day * 24);
	min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
	sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
	String duration = day + "天" + hour + "时" + min + "分" + sec + "秒";
	return remove0(duration);
    }

    public static String remove0(String str) {
	while (str.startsWith("0")) {
	    str = str.substring(2, str.length());
	    remove0(str);
	}
	return str;
    }

    public static String format(Date date) {
	if(date == null){
	    return null;
	}
	return format("yyyy-MM-dd HH:mm:ss",date);
    }
}
