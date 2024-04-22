package com.hc.calc.task.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.hc.calc.task.util.DateUtil;
import scala.Int;

/**
 * Created by Holger on 2018/4/18.
 */
public class ComputingUnit {
    private Long taskid;
    private String point;
    private Long cycle;
    private Date startDT;
    private Date endDT;
    private Date saveDT;
    private Long mpointId;
    private String expression;
    private List<CalcParm> calcParms;
    private String type;
    private String ftype;
    private String shiftsTypeName;
    private String shiftName;
    private Integer initialValue;

    public Integer getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(Integer initialValue) {
        this.initialValue = initialValue;
    }

    public String getShiftsTypeName() {
        return shiftsTypeName;
    }

    public void setShiftsTypeName(String shiftsTypeName) {
        this.shiftsTypeName = shiftsTypeName;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public String getFtype() {
	return ftype;
    }

    public void setFtype(String ftype) {
	this.ftype = ftype;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public Long getTaskid() {
	return taskid;
    }

    public void setTaskid(Long taskid) {
	this.taskid = taskid;
    }

    public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

	public List<CalcParm> getCalcParms() {
	return calcParms;
    }

    public void setCalcParms(List<CalcParm> calcParms) {
	this.calcParms = calcParms;
    }

    public Long getCycle() {
	return cycle;
    }

    public void setCycle(Long cycle) {
	this.cycle = cycle;
    }

    public Date getEndDT() {
	return endDT;
    }

    public void setEndDT(Date endDT) {
	this.endDT = endDT;
    }

    public String getExpression() {
	return expression;
    }

    public void setExpression(String expression) {
	this.expression = expression;
    }

    public Long getMpointId() {
	return mpointId;
    }

    public void setMpointId(Long mpointId) {
	this.mpointId = mpointId;
    }

    public Date getStartDT() {
	return startDT;
    }

    public void setStartDT(Date startDT) {
	this.startDT = startDT;
    }

    public Date getSaveDT() {
	return saveDT;
    }

    public void setSaveDT(Date saveDT) {
	this.saveDT = saveDT;
    }

    @Override
    public int hashCode() {
	return Objects.hash(taskid, cycle, startDT, endDT, saveDT, mpointId,
		expression, calcParms);
    }

    @Override
    public String toString() {
        return "ComputingUnit{" +
                "taskid=" + taskid +
                ", point='" + point + '\'' +
                ", cycle=" + cycle +
                ", startDT=" + startDT +
                ", endDT=" + endDT +
                ", saveDT=" + saveDT +
                ", mpointId=" + mpointId +
                ", expression='" + expression + '\'' +
                ", calcParms=" + calcParms +
                ", type='" + type + '\'' +
                ", ftype='" + ftype + '\'' +
                ", shiftsTypeName='" + shiftsTypeName + '\'' +
                ", shiftName='" + shiftName + '\'' +
                '}';
    }
}
