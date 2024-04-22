package com.hc.calc.task.model;

import com.hc.calc.task.config.ExecuteStatus;


/**
 * Created by Holger on 2018/4/26.
 */
public class ExecuteRes {
    private Double value;

    private String valText;

    private String status;

    private String msg;

    public ExecuteRes(Double value, String valText, ExecuteStatus status, String msg) {
        this.value = value;
        this.valText = valText;
        this.status = status.getStatus();
        this.msg = msg;
    }

    public ExecuteRes() {

    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getValText() {
        return valText;
    }

    public void setValText(String valText) {
        this.valText = valText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setSuccess(double value, String valText) {
	setSuccessVal(value);
        this.valText = valText;
    }

    public void setSuccessVal(Double result) {
	this.status = ExecuteStatus.SUCCESS.getStatus();
	this.value = result;
    }

    public void setFaild(String msg) {
	this.status = ExecuteStatus.ERROR.getStatus();
	this.msg = msg;
	
    }

    @Override
    public String toString() {
	return "ExecuteRes [value=" + value + ", valText=" + valText
		+ ", status=" + status + ", msg=" + msg + "]";
    }
    

}
