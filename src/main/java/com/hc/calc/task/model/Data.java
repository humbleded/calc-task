package com.hc.calc.task.model;

/**
 * Data class
 * 
 * @author zf
 * @date 2019/08/07
 */
public class Data implements Comparable<Data> {

	public Long dataDate;

	public double dataValue;

	public Long getDataDate() {
		return dataDate;
	}

	public void setDataDate(Long dataDate) {
		this.dataDate = dataDate;
	}

	public double getDataValue() {
		return dataValue;
	}

	public void setDataValue(double dataValue) {
		this.dataValue = dataValue;
	}

	@Override
	public String toString() {
		return "DataDetailDTO [dataDate=" + dataDate + ", dataValue=" + dataValue + "]";
	}

	@Override
	public int compareTo(Data o) {
		return this.dataDate.compareTo(o.getDataDate());
	}
}
