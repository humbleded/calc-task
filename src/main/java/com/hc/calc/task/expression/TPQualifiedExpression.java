package com.hc.calc.task.expression;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hc.calc.task.model.ComputingUnit;
import com.hc.calc.task.model.ExecuteRes;

/**
 * @author Holger
 * @date 2018/4/28
 */
@Component("TPQualified")
public class TPQualifiedExpression extends BaseExpression {

    @Autowired
    private CommonExpression expression;

    @Override
    public ExecuteRes execute(ComputingUnit unit) throws Exception {
        return expression.commonQualified(unit, 0.5, null, logger);
    }
}
