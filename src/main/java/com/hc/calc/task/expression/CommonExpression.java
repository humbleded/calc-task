package com.hc.calc.task.expression;

import com.hc.calc.task.config.ExecuteStatus;
import com.hc.calc.task.excption.RangeIsNullException;
import com.hc.calc.task.model.BaseData;
import com.hc.calc.task.model.ComputingUnit;
import com.hc.calc.task.model.ExecuteRes;
import com.hc.calc.task.service.DataService;
import com.hc.calc.task.util.DateUtil;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Holger
 * @date 2018/4/28
 */
@Component
public class CommonExpression {

    @Autowired
    private DataService service;

    /**
     * 计算合格率，所有的合格率都是按照隔一小时取值来计算合格率
     *
     * @param unit   计算单元
     * @param max    合格率最大值
     * @param min    合格率最小猪
     * @param logger
     * @return 计算结果
     */
    public ExecuteRes commonQualified(ComputingUnit unit, Number max,
                                      Number min, Logger logger) throws Exception {
        logger.info("合格率算法参数:" + unit.toString());
        Long end = (unit.getEndDT().getTime() - 1 < unit.getStartDT().getTime()) ? unit.getEndDT().getTime() : unit.getEndDT().getTime() - 1;
        List<BaseData> list = service.getAllDatasNew(
                unit.getCalcParms().get(0).getMpointid(),
                unit.getCalcParms().get(0).getPoint(),
                unit.getCalcParms().get(0).getDatasource(), unit.getStartDT().getTime(),
                end);
        if (list == null || list.size() == 0) {
            return new ExecuteRes(null, null, ExecuteStatus.ERROR,
                    "测点ID为 " + unit.getCalcParms().get(0).getMpointid()
                            + " 在时间范围：" + DateUtil.format(unit.getStartDT())
                            + "至" + DateUtil.format(unit.getEndDT()) + "无数据");
        }
        int count = 0;
        for (BaseData data : list) {
            if (min != null && max == null) {
                if (data.getValue() > min.doubleValue()) {
                    count++;
                }

            } else if (min == null && max != null) {
                if (data.getValue() < max.doubleValue()) {
                    count++;
                }
            } else if (min != null && max != null) {
                if (data.getValue() < max.doubleValue()
                        && data.getValue() > min.doubleValue()) {
                    count++;
                }

            } else {
                throw new RangeIsNullException("");
            }
        }
        logger.info("任务id:{}计算{}时的数据,在测点:{}从{}到{}时间范围内数据总量{},合格次数{},不合格次数{}",
                unit.getTaskid(), DateUtil.format(unit.getSaveDT()),
                unit.getCalcParms().get(0).getMpointid(),
                DateUtil.format(unit.getStartDT()),
                DateUtil.format(unit.getEndDT()), list.size(), count,
                list.size() - count);
        if (list.size() == 0) {
            return new ExecuteRes(null, null, ExecuteStatus.ERROR,
                    "测点 " + unit.getCalcParms().get(0).getMpointid() + "在："
                            + DateUtil.format(unit.getStartDT()) + "无数据");
        }
        double res = count / (Double.valueOf(String.valueOf(list.size())));
        return new ExecuteRes(res, null, ExecuteStatus.SUCCESS, null);
    }

}
