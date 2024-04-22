package com.hc.calc.task.model;

import java.util.Date;

public class MpointInputData {
    private Long mpointid;

    private Date datadt;

    private Double value;

    private String valuetext;

    public MpointInputData(ExecuteRes res, ComputingUnit unit) {
	this.mpointid = unit.getMpointId();
	this.datadt = unit.getSaveDT();
	this.value = res.getValue();
    }

    public Long getMpointid() {
        return mpointid;
    }

    public void setMpointid(Long mpointid) {
        this.mpointid = mpointid;
    }

    public Date getDatadt() {
        return datadt;
    }

    public void setDatadt(Date datadt) {
        this.datadt = datadt;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getValuetext() {
        return valuetext;
    }

    public void setValuetext(String valuetext) {
        this.valuetext = valuetext == null ? null : valuetext.trim();
    }
}