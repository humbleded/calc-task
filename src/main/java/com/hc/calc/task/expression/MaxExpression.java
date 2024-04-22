package com.hc.calc.task.expression;

import com.hc.calc.task.config.ExecuteStatus;
import com.hc.calc.task.model.ComputingUnit;
import com.hc.calc.task.model.ExecuteRes;
import com.hc.calc.task.service.DataService;
import com.hc.calc.task.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 最大值算法
 */
@Component("max")
public class MaxExpression extends BaseExpression {

    @Autowired
    private DataService dataService;

    @Override
    public ExecuteRes execute(ComputingUnit unit) throws Exception {
        logger.info("最大值算法参数:" + unit.toString());
        ExecuteRes excuteRes = new ExecuteRes();
        Double result;
        result = dataService.max(unit.getCalcParms().get(0).getMpointid(),
                unit.getCalcParms().get(0).getPoint(),
                unit.getCalcParms().get(0).getDatasource(), unit.getStartDT(),
                unit.getEndDT());
        logger.info("任务id:{}计算{}时的数据，在源测点id:{}从{}到{}时间范围内最大值为{}",
                unit.getTaskid(), DateUtil.format(unit.getSaveDT()),
                unit.getCalcParms().get(0).getMpointid(),
                DateUtil.format(unit.getStartDT()),
                DateUtil.format(unit.getEndDT()), result);
        if (result == null) {
            return new ExecuteRes(null, null, ExecuteStatus.ERROR, "测点ID为 " + unit.getCalcParms().get(0).getMpointid()
                    + " 在时间范围：" + DateUtil.format(unit.getStartDT()) + "至" + DateUtil.format(unit.getEndDT()) + "无数据");
        } else {
            excuteRes.setSuccessVal(result);
        }
        return excuteRes;

    }

}
