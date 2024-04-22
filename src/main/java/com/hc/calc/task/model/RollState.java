package com.hc.calc.task.model;

/**
 * @author Holger
 * @date 2018/6/11
 */
public enum RollState {
    /**
     * 年
     */
    WAITTING("Waitting"),
    /**
     * 月
     */
    NORMAL("Normal"),
    /**
     * 日
     */
    ERROR("Error");

    private String state;

    RollState(String state) {
	this.state = state;
    }

    public String getState() {
	return state;
    }
}
