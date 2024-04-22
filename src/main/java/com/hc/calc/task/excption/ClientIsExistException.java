package com.hc.calc.task.excption;

/**
 * @author Holger
 * @date 2018/4/26
 */
public class ClientIsExistException extends CalcBaseException {
    /**
     * 无参数的构造器
     */
    public ClientIsExistException() {
    }

    /**
     * 带一个字符串参数的构造器
     */
    public ClientIsExistException(String msg) {
        super(msg);
    }
}
