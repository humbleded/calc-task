package com.hc.calc.task.model;

import java.math.BigDecimal;

public class CalcParm {
    private Long id;

    private Long taskid;

    private String code;

    private BigDecimal defaultval;

    private Long mpointid;
    
    private String datasource;
    
    private String point;

    private String mpointName;

    public String getPoint() {
	return point;
    }

    public void setPoint(String point) {
	this.point = point;
    }

    public String getDatasource() {
	return datasource;
    }

    public void setDatasource(String datasource) {
	this.datasource = datasource;
    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public Long getTaskid() {
	return taskid;
    }

    public void setTaskid(Long taskid) {
	this.taskid = taskid;
    }

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code == null ? null : code.trim();
    }

    public BigDecimal getDefaultval() {
	return defaultval;
    }

    public void setDefaultval(BigDecimal defaultval) {
	this.defaultval = defaultval;
    }

    public Long getMpointid() {
	return mpointid;
    }

    public void setMpointid(Long mpointid) {
	this.mpointid = mpointid;
    }

    public String getMpointName() {
	return mpointName;
    }

    public void setMpointName(String mpointName) {
	this.mpointName = mpointName;
    }

    @Override public String toString() {
	return "CalcParm{" +
			"code='" + code + '\'' +
			", id=" + id +
			", taskid=" + taskid +
			", defaultval=" + defaultval +
			", mpointid=" + mpointid +
			", datasource='" + datasource + '\'' +
			", point='" + point + '\'' +
			", mpointName='" + mpointName + '\'' +
			'}';
    }
}
