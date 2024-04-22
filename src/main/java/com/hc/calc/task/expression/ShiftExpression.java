package com.hc.calc.task.expression;

import com.hc.calc.task.model.ComputingUnit;
import com.hc.calc.task.model.ExecuteRes;
import com.hc.calc.task.service.DataService;
import com.hc.calc.task.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 差值算法
 */
@Component("shift")
public class ShiftExpression extends BaseExpression {

    @Autowired
    private DataService dataService;

    @Override
    public ExecuteRes execute(ComputingUnit unit) throws Exception {
        logger.info("差值算法的参数:"+unit.toString());
        ExecuteRes excuteRes = new ExecuteRes();
        Double result1, result2;
        result1 = dataService.prev(unit.getCalcParms().get(0).getMpointid(),
                unit.getCalcParms().get(0).getPoint(),
                unit.getCalcParms().get(0).getDatasource(), unit.getStartDT());
        result2 = dataService.prev(unit.getCalcParms().get(0).getMpointid(),
                unit.getCalcParms().get(0).getPoint(),
                unit.getCalcParms().get(0).getDatasource(), unit.getEndDT());
        logger.info("任务id:{}计算{}时的数据，在源测点id:{}在开始时间{}的值为{},结束时间{}的值为{}",
                unit.getTaskid(), DateUtil.format(unit.getSaveDT()),
                unit.getCalcParms().get(0).getMpointid(), DateUtil.format(unit.getStartDT()),
                result1, DateUtil.format(unit.getEndDT()), result2);
        if (result1 == null) {
            excuteRes.setFaild("测点:" + unit.getCalcParms().get(0).getMpointid()
                    + "在时间:" + DateUtil.format(unit.getStartDT()) + "未取到值");
        } else if (result2 == null) {
            excuteRes.setFaild("测点:" + unit.getCalcParms().get(0).getMpointid()
                    + "在时间:" + DateUtil.format(unit.getEndDT()) + "未取到值");
        } else {
            excuteRes.setSuccessVal(result2 - result1);
        }
        return excuteRes;

    }

}
