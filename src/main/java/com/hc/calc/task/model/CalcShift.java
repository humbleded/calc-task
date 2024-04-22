package com.hc.calc.task.model;

import java.util.Date;

public class CalcShift {


    public CalcShift(){

    }

    public CalcShift(Date dataDT, Long mpointId, String shiftsType, String shift) {
        this.dataDT = dataDT;
        this.mpointId = mpointId;
        this.shiftsType = shiftsType;
        this.shift = shift;
    }

    public CalcShift(Date dataDT, Long mpointId, String shiftsType, String shift, Date startDT, Date endDT) {
        this.dataDT = dataDT;
        this.mpointId = mpointId;
        this.shiftsType = shiftsType;
        this.shift = shift;
        this.startDT = startDT;
        this.endDT = endDT;
    }

    private Date dataDT;

    private Long mpointId;

    private String shiftsType;

    private String shift;

    private Date startDT;

    private Date endDT;

    public Date getStartDT() {
        return startDT;
    }

    public void setStartDT(Date startDT) {
        this.startDT = startDT;
    }

    public Date getEndDT() {
        return endDT;
    }

    public void setEndDT(Date endDT) {
        this.endDT = endDT;
    }


    public Date getDataDT() {
        return dataDT;
    }

    public void setDataDT(Date dataDT) {
        this.dataDT = dataDT;
    }

    public Long getMpointId() {
        return mpointId;
    }

    public void setMpointId(Long mpointId) {
        this.mpointId = mpointId;
    }

    public String getShiftsType() {
        return shiftsType;
    }

    public void setShiftsType(String shiftsType) {
        this.shiftsType = shiftsType;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
}
