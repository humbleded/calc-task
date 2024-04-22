package com.hc.calc.task.excption;

/**
 * @author Holger
 * @date 2018/5/2
 */
public class DataIsNullException extends CalcBaseException {

    /**
     *无参数的构造器
     */
    public DataIsNullException() {
    }

    /**
     * 带一个字符串参数的构造器
     */
    public DataIsNullException(String msg) {
        super(msg);
    }
}
