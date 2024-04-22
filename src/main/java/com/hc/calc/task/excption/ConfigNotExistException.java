package com.hc.calc.task.excption;

/**
 * @author Holger
 * @date 2018/4/26
 */
public class ConfigNotExistException extends CalcBaseException {
    /**
     *无参数的构造器
     */
    public ConfigNotExistException() {
        super();
    }

    /**
     * 带一个字符串参数的构造器
     */
    public ConfigNotExistException(String msg) {
        super(msg);
    }

}

