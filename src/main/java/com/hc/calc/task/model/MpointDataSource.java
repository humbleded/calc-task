package com.hc.calc.task.model;

/**
 * @author Holger
 * @date 2018/5/3
 */
public enum MpointDataSource {
    /**
     * 人工录入
     */
    INPUT("INPUT"),
    /**
     * 数据计算
     */
    CALC("CALC"),
    /**
     * 自动采集
     */
    AUTO("AUTO");

    /**
     * 状态量
     */
    public static final String STATE = "State";

    public static final String DIGITAL = "Digtal";

    /**
     * 字符串
     */
    public static final String CHAR = "Char";

    private String datasource;

    MpointDataSource(String datasource) {
	this.datasource = datasource;
    }

    public String getDatasource() {
	return datasource;
    }

    public void setDatasource(String datasource) {
	this.datasource = datasource;
    }
}
