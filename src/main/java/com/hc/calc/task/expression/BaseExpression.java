package com.hc.calc.task.expression;


import com.hc.calc.task.model.ComputingUnit;
import com.hc.calc.task.model.ExecuteRes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Holger
 * @date 2018/4/26
 */
public abstract class BaseExpression {

    protected final Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * 执行表达式计算
     *
     * @param unit 计算单元相关类
     * @return 计算结果
     */
    abstract public ExecuteRes execute(ComputingUnit unit) throws Exception;
}
