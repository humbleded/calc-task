package com.hc.calc.task.expression;

import com.hc.calc.task.model.ComputingUnit;
import com.hc.calc.task.model.ExecuteRes;
import com.hc.calc.task.service.DataService;
import com.hc.calc.task.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 取前点值算法
 */
@Component("prev")
public class PrevExpression extends BaseExpression {

    @Autowired
    private DataService dataService;

    @Override
    public ExecuteRes execute(ComputingUnit unit) throws Exception {
	    logger.info("取前点值算法参数:"+unit.toString());
        ExecuteRes excuteRes = new ExecuteRes();
        Double result;
        Long end = unit.getStartDT().getTime() - 1;
        result = dataService.prev(unit.getCalcParms().get(0).getMpointid(),
                unit.getCalcParms().get(0).getPoint(),
                unit.getCalcParms().get(0).getDatasource(), new Date(end));
        logger.info("任务id:{}计算{}时的数据，在源测点id:{}从{}到{}时间范围内前点值为{}",
                unit.getTaskid(), DateUtil.format(unit.getSaveDT()),
                unit.getCalcParms().get(0).getMpointid(),
                DateUtil.format(unit.getStartDT()),
                DateUtil.format(unit.getEndDT()), result);
        if (result == null) {
            excuteRes.setFaild("测点:" + unit.getCalcParms().get(0).getMpointid()
                    + "在时间:" + DateUtil.format(unit.getStartDT()) + "未取到值");
        } else {
            excuteRes.setSuccessVal(result);
        }
        return excuteRes;

    }

}
