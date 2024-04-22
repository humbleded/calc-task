package com.hc.calc.task.expression;

import com.hc.calc.task.model.ComputingUnit;
import com.hc.calc.task.model.ExecuteRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * NH3N合格率算法
 * @author Holger
 * @date 2018/4/28
 */
@Component("NH3NQualified")
public class NH3NQualifiedExpression extends BaseExpression {

    @Autowired
    private CommonExpression expression;

    @Override
    public ExecuteRes execute(ComputingUnit unit) throws Exception {
    	return expression.commonQualified(unit, 5, null, logger);
    }
}
