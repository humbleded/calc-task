package com.hc.calc.task.model;


import com.hc.calc.task.config.ExecuteStatus;

import java.math.BigDecimal;
import java.util.Date;

public class CalcLog {
    private Long taskid;

    private Date datadt;

    private Date finishdt;

    private BigDecimal excuteVal;

    private String status;

    private String error;

    public CalcLog(ExecuteRes res, ComputingUnit unit) {
		this.taskid = unit.getTaskid();
		this.datadt = unit.getSaveDT();
		this.finishdt = new Date();
		if(res.getValue() != null){
		    this.excuteVal = new BigDecimal(res.getValue());
		}
		if(unit.getType().equals(UnitType.ROLL.getType())){
		    this.status = ExecuteStatus.RECALCED.getStatus();
		    if(res.getMsg() != null){
			    this.error = res.getMsg();
		    }
		}else{
		    this.status = res.getStatus();
	
		}
		//异常则记录异常详情
		if(ExecuteStatus.ERROR.getStatus().equals(this.status)){
		    this.error = res.getMsg();
		}
    }

    public Long getTaskid() {
	return taskid;
    }

    public void setTaskid(Long taskid) {
	this.taskid = taskid;
    }

    public Date getDatadt() {
	return datadt;
    }

    public void setDatadt(Date datadt) {
	this.datadt = datadt;
    }

    public Date getFinishdt() {
	return finishdt;
    }

    public void setFinishdt(Date finishdt) {
	this.finishdt = finishdt;
    }

    public BigDecimal getExcuteVal() {
	return excuteVal;
    }

    public void setExcuteVal(BigDecimal excuteVal) {
	this.excuteVal = excuteVal;
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status == null ? null : status.trim();
    }

    @Override
    public String toString() {
        return "CalcLog{" +
                "taskid=" + taskid +
                ", datadt=" + datadt +
                ", finishdt=" + finishdt +
                ", excuteVal=" + excuteVal +
                ", status='" + status + '\'' +
                ", error='" + error + '\'' +
                '}';
    }

    public String getError() {
	return error;
    }

    public void setError(String error) {
	this.error = error == null ? null : error.trim();
    }

}
