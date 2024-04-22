package com.hc.calc.task.model;

/**
 * @author Holger
 * @date 2018/6/12
 */
public enum UnitType {
    /**
     * 任务类型为补算类型
     */
    ROLL("roll"),
    /**
     * 任务类型为计算任务类型 
     */
    TASK("task");
    private String type;

    UnitType(String type) {
	this.type = type;
    }

    public String getType() {
	return type;
    }
}
