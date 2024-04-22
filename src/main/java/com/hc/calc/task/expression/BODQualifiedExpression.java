package com.hc.calc.task.expression;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hc.calc.task.model.ComputingUnit;
import com.hc.calc.task.model.ExecuteRes;

/**
 * BOD合格率算法
 * @author Holger
 * @date 2018/4/28
 */
@Component("BODQualified")
public class BODQualifiedExpression extends BaseExpression {

    @Autowired
    private CommonExpression expression;


    @Override
    public ExecuteRes execute(ComputingUnit unit) throws Exception {
        return expression.commonQualified(unit, 10, null, logger);
    }
}
