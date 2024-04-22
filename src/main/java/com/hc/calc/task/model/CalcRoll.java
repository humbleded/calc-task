package com.hc.calc.task.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class CalcRoll {
    private Long taskid;

    private Date datadt;

    private Date finishdt;

    private BigDecimal excuteVal;

    private String status;

    private String error;
    
    private List<CalcRoll> nextRolls;

    public List<CalcRoll> getNextRolls() {
	return nextRolls;
    }
    
    public void addNextRoll(CalcRoll next){
	nextRolls.add(next);
    }

    public void setNextRolls(List<CalcRoll> nextRolls) {
	this.nextRolls = nextRolls;
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

    public String getError() {
	return error;
    }

    public void setError(String error) {
	this.error = error == null ? null : error.trim();
    }

    @Override 
    public boolean equals(Object o) {
	if (this == o)
	    return true;
	if (o == null || getClass() != o.getClass())
	    return false;

	CalcRoll calcRoll = (CalcRoll) o;

	if (taskid != null ? !taskid.equals(calcRoll.taskid) : calcRoll.taskid != null)
	    return false;
	return datadt != null ? datadt.equals(calcRoll.datadt) : calcRoll.datadt == null;

    }

    @Override 
    public int hashCode() {
	int result = taskid != null ? taskid.hashCode() : 0;
	result = 31 * result + (datadt != null ? datadt.hashCode() : 0);
	return result;
    }

    @Override
    public String toString() {
        return "CalcRoll{" +
                "taskid=" + taskid +
                ", datadt=" + datadt +
                ", finishdt=" + finishdt +
                ", excuteVal=" + excuteVal +
                ", status='" + status + '\'' +
                ", error='" + error + '\'' +
                ", nextRolls=" + nextRolls +
                '}';
    }
}
