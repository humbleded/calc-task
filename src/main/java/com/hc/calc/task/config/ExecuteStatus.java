package com.hc.calc.task.config;

/**
 * @author Holger
 * @date 2018/5/2
 */
public enum ExecuteStatus {
    /**
     * 成功
     */
    SUCCESS("Normal"),
    /**
     * 失败
     */
    RECALCED("Recalced"),
    /**
     * 异常
     */
    ERROR("Error");
    private String status;

    ExecuteStatus(String status) {
	this.status = status;
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }
}
