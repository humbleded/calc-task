package com.hc.calc.task.model;

import java.util.Date;

import com.hc.calc.task.util.DateUtil;

/**
 *
 * @author Holger
 * @date 2018/4/26
 */
public class BaseData {
    private Date dataDt;
    private double value;
    private String valueText;
    private Long mpointIds;

    public BaseData() {

    }

    public BaseData(Date dataDt, double value) {
	this.dataDt = dataDt;
	this.value = value;
    }

    public Long getMpointIds() {
        return mpointIds;
    }

    public void setMpointIds(Long mpointIds) {
        this.mpointIds = mpointIds;
    }

    public String getValueText() {
	return valueText;
    }

    public void setValueText(String valueText) {
	this.valueText = valueText;
    }

    public Date getDataDt() {
	return dataDt;
    }

    public void setDataDt(Date dataDt) {
	this.dataDt = dataDt;
    }

    public double getValue() {
	return value;
    }

    public void setValue(double value) {
	this.value = value;
    }

    @Override
    public String toString() {
	return "{" + "dataDt=" + DateUtil.format(dataDt) + ", value=" + value
		+ ", valueText='" + valueText + '\'' + '}';
    }
}
