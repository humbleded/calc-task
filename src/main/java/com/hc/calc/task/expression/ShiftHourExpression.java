package com.hc.calc.task.expression;

import com.hc.calc.task.config.BaseConfig;
import com.hc.calc.task.model.BaseData;
import com.hc.calc.task.model.ComputingUnit;
import com.hc.calc.task.model.ExecuteRes;
import com.hc.calc.task.service.DataService;
import com.hc.calc.task.util.Calcutil;
import com.hc.calc.task.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 小时差值算法
 *
 * @author Holger
 * @date 2018/5/14
 **/
@Component("shiftHour")
public class ShiftHourExpression extends BaseExpression {

    @Autowired
    private DataService dataService;

    @Override
    public ExecuteRes execute(ComputingUnit unit) throws Exception {
        logger.info("小时差值算法的参数:"+unit.toString());
        ExecuteRes eres = new ExecuteRes();
        Date start = new Date(unit.getStartDT().getTime() - 600000);
        logger.info("当前bean:{}其他{}", dataService, unit);
        long count = dataService.count(unit.getCalcParms().get(0).getId(),
                unit.getCalcParms().get(0).getPoint(),
                unit.getCalcParms().get(0).getDatasource(), start,
                unit.getEndDT());
        logger.info("1-任务id:{}计算{}时的数据,共有数据{}条，理论值为{},设定的缺失率为{}", unit.getTaskid(),
                DateUtil.format(unit.getSaveDT()), count,
                BaseConfig.CYCLE / BaseConfig.SAMPLING, BaseConfig.MISSING);
        if ((double) count / (BaseConfig.CYCLE / BaseConfig.SAMPLING) >= 1
                - BaseConfig.MISSING) {
            List<BaseData> list = dataService.getAvgData(
                    unit.getCalcParms().get(0).getId(),
                    unit.getCalcParms().get(0).getPoint(), "10m",
                    unit.getCalcParms().get(0).getDatasource(), start,
                    unit.getEndDT());
            logger.info("2-任务id:{}计算{}时的数据,取到平均值数据为{}", unit.getTaskid(),
                    DateUtil.format(unit.getSaveDT()), list);
            if (list.size() < 7) {
                // 补数据
                double as = Calcutil.getAvgSloop(list);
                // 比对数据，找到缺失的点
                Date find = start;
                for (int i = 0; i < 7; i++) {
                    for (int j = 0; j < list.size(); j++) {
                        long ms = Calcutil.min2ms(
                                Long.valueOf(list.get(j).getValueText()));
                        if (find.getTime() == ms) {
                            list.get(j).setDataDt(find);
                            find = new Date(find.getTime() + 600000);
                            break;
                        }
                        // 非最后一个点补数据
                        if (find.getTime() < ms) {
                            // 表示已经找到了后面的一个数据，表示当前数据缺失
                            // 此时需要根据后面的一个数据补充前面的数据
                            // 公式为Y1 = Y2 - k(X2 - X1)
                            BaseData data = new BaseData(find,
                                    list.get(j).getValue()
                                            - as * (ms - find.getTime()));
                            data.setValueText(Calcutil.ms2min(find.getTime()));
                            list.add(j, data);
                            find = new Date(find.getTime() + 600000);
                            break;
                        }
                        // 如果是最后一个点缺失
                        if (j == list.size() - 1 && ms < find.getTime()) {
                            // 则使用前值补充最后一个点
                            // 公式为Y2 = Y1 + k(X2 - X1)
                            BaseData data = new BaseData(find,
                                    list.get(j).getValue()
                                            + as * (find.getTime() - ms));
                            data.setValueText(Calcutil.ms2min(find.getTime()));
                            list.add(data);
                            find = new Date(find.getTime() + 600000);
                            break;
                        }
                    }
                }
            }
            logger.info("3-任务id:{}计算{}时的数据,最终得到的数据为{}", unit.getTaskid(),
                    DateUtil.format(unit.getSaveDT()), list);
            // 计算
            // 查找中心值
            double[] res = new double[6];
            for (int i = 1; i < list.size(); i++) {
                double temp = list.get(i).getValue()
                        - list.get(i - 1).getValue();
                res[i - 1] = temp;
            }
            logger.info("4-任务id:{}计算{}时的数据,计算的差值的结果为{}", unit.getTaskid(),
                    DateUtil.format(unit.getSaveDT()), Arrays.toString(res));
            Arrays.sort(res);
            logger.info("5-任务id:{}计算{}时的数据,排序后的差值的结果为{}", unit.getTaskid(),
                    DateUtil.format(unit.getSaveDT()), Arrays.toString(res));
            double central = res[2];
            central *= BaseConfig.T10DEVIATION;
            double sum = 0;
            for (int i = 1; i < list.size(); i++) {
                double temp = list.get(i).getValue()
                        - list.get(i - 1).getValue();
                if (temp >= 0 && temp <= central) {
                    sum += temp;
                }
            }
            eres.setSuccessVal(sum);
        } else {
            eres.setSuccessVal(0.0);
        }
        logger.info("6-任务id:{}计算{}时的数据,计算最终的结果为{}", unit.getTaskid(),
                DateUtil.format(unit.getSaveDT()), eres.getValue());
        return eres;
    }

}
