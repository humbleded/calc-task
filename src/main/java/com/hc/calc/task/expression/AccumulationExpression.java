package com.hc.calc.task.expression;

import com.hc.calc.task.config.ExecuteStatus;
import com.hc.calc.task.dao.MpointInputDataMapper;
import com.hc.calc.task.model.*;
import com.hc.calc.task.service.DataService;
import com.hc.calc.task.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 累加计算计算方式
 */

@Component("accumulation")
public class AccumulationExpression extends BaseExpression {

    @Autowired
    private DataService dataService;

    @Autowired
    private MpointInputDataMapper mpointInputDataMapper;

    @Override
    public ExecuteRes execute(ComputingUnit unit) throws Exception {
        logger.info("累加计算算法参数:"+unit.toString());
        //非班次重算计算过程中需要取前点
        if (UnitType.ROLL.getType().equals(unit.getType())&& (unit.getShiftsTypeName() == null||"".equals(unit.getShiftsTypeName()))) {
            //重新计算是一次性拿全部的数据
            Double frontPointValue = getFrontPoint(unit,true);
            return getValueByTimeSolt(unit,unit.getStartDT(),frontPointValue,null);
        }else if (unit.getShiftsTypeName() != null && !"".equals(unit.getShiftsTypeName())){
            //班次重新计算或者班次的正常计算，不需要取前点
            return getValueByTimeSolt(unit,unit.getStartDT(),null,null);
        }else {
            //进行正常计算，需要判断是否是第一次计算需要根据taskID获取当前记录
            BaseData calcHistoryLog = mpointInputDataMapper.getBaseDataByTaskIdAndTime(unit.getMpointId(), unit.getStartDT());
            if (calcHistoryLog == null||unit.getCycle() == 3600) {
                //正常计算-第一次计算，小时差值计算
                Double frontPointValue = getFrontPoint(unit,true);
                return getValueByTimeSolt(unit, unit.getStartDT(), frontPointValue,null);
            } else {
                //正常计算-已经计算过了
                Date frontPointEnd = new Date(unit.getEndDT().getTime()-60*60*1000);
                Double frontPointValue = getFrontPoint(unit,false);
                Double lastCalcValue = calcHistoryLog.getValue();
                logger.info("当前已经计算过的数据:"+lastCalcValue+" 前点值:"+frontPointValue);
                return getValueByTimeSolt(unit,frontPointEnd,frontPointValue,lastCalcValue);
            }
        }
    }

    /**
     * 用于计算当前时间段类累加计算
     * 如果传来了前点，就用第一个值减去前点
     * @return
     * @throws Exception
     */
    // TODO: 2020/8/17 有效值过滤
    private ExecuteRes getValueByTimeSolt(ComputingUnit unit,Date startTime, Double frontPoint,Double lastCalcValue) throws Exception {
        ExecuteRes executeRes = new ExecuteRes();
        Double value = new Double(0);

        logger.info("前点值:"+frontPoint+" 上一次计算的数值:"+lastCalcValue);

        //根据不同的开始时间查询数据
        Long end = (unit.getEndDT().getTime()-1<unit.getStartDT().getTime())?unit.getEndDT().getTime():unit.getEndDT().getTime()-1;
        List<BaseData> list = dataService.getAllDatasNew(unit.getCalcParms().get(0).getMpointid(),
                unit.getCalcParms().get(0).getPoint(),
                unit.getCalcParms().get(0).getDatasource(), startTime.getTime(),
                end);

        //todo 测点数据有效性验证

        if (list == null || list.size() <= 0) {
            //如果当前计算周期类还为结束，使用前值进行替补，如果当前周期内的数据已经计算完但是还是没有数据，就爆出移异常
            if (unit.getCycle() > 60*60 &&  lastCalcValue != null){
                executeRes.setSuccessVal(lastCalcValue);
                return executeRes;
            }else {
                String errorMsg = "测点ID为 " + unit.getMpointId()
                        + " 在时间范围：" + DateUtil.format(unit.getStartDT()) + "至" + DateUtil.format(unit.getEndDT()) + "无数据";
                return new ExecuteRes(null, null, ExecuteStatus.ERROR, errorMsg);
            }
        }else if(list.size()<=1 && frontPoint == null){
            String errorMsg = "测点ID为 " + unit.getMpointId()
                    + " 在时间范围：" + DateUtil.format(unit.getStartDT()) + "至" + DateUtil.format(unit.getEndDT()) + "只有一条数据，无法计算";
            return new ExecuteRes(null, null, ExecuteStatus.ERROR, errorMsg);
        }
        Double midPro;

        //按照时间顺序进行排序
        list.sort(new Comparator<BaseData>() {
            @Override
            public int compare(BaseData o1, BaseData o2) {
                return o1.getDataDt().getTime()>o2.getDataDt().getTime()?1:-1;
            }
        });

        //前点求和
        if (frontPoint != null){
            if (list.get(0).getValue()-frontPoint>0){
                value += (list.get(0).getValue()-frontPoint);
            }

        }

        //前一个值，如果当前值为null，就将前一个之进行相减
        for (int i = 0; i < list.size() - 1; i++) {
            midPro = list.get(i+1).getValue() - list.get(i).getValue();
            if (midPro > 0) {
                value += midPro;
            }
        }

        //加上以前计算过的数值
        if (lastCalcValue != null){
            value += lastCalcValue;
        }

        executeRes.setSuccessVal(value);
        logger.info("任务id:{}计算{}时的数据，在源测点id:{}在开始时间{},结束时间{}的值为{}",
                unit.getTaskid(), DateUtil.format(unit.getSaveDT()),
                unit.getCalcParms().get(0).getMpointid(), DateUtil.format(unit.getStartDT()),
                DateUtil.format(unit.getEndDT()), value);
        return executeRes;
    }

    /**
     * 取前点
     * @param unit
     * @Param endDT 当前数据周期的结束时间
     * @Param isLastCycle 当前计算任务周期
     * @return
     * @throws Exception
     */
    private Double getFrontPoint(ComputingUnit unit,boolean isLastCycle) throws Exception {

        boolean limitFlag = false;
        Date frontPointEnd ;
        long cycleTime ;
        //获取前点的值，以及前点的时间，现在限制是一天的数据
        if (isLastCycle){
            //如果是第一次计算当前时间，就需要取前一天的最新值
            frontPointEnd = new Date(unit.getStartDT().getTime()-1);
            cycleTime = unit.getStartDT().getTime() - unit.getCycle()*1000;
        }else {
            //如果当前是非第一次计算
            frontPointEnd = new Date(unit.getEndDT().getTime()-60*60*1000-1);
            cycleTime = unit.getStartDT().getTime();
        }
        //结束时间一天前的数据
        Date frontPointStart = new Date(frontPointEnd.getTime()-24*60*60*1000);

        logger.info("取前点的开始时间:"+frontPointStart+" 取前点的结束时间:"+frontPointEnd+" 取前点的界限时间:"+cycleTime);

        //如果开始时间与结束时间相差一个周期，调整开始时间
        if(frontPointStart.getTime()<cycleTime){
            frontPointStart = new Date(cycleTime);
            limitFlag = true;
        }
        //先按照天进行取值
        List<BaseData> midPro = dataService.getAllDatas(unit.getCalcParms().get(0).getMpointid(),
                unit.getCalcParms().get(0).getPoint(),
                unit.getCalcParms().get(0).getDatasource(), frontPointStart,
                frontPointEnd);


        //按照周进行取值
        if ((midPro == null || midPro.size() <= 0)&& !limitFlag ){
            frontPointStart = new Date(frontPointEnd.getTime()-24*60*60*1000*7);
            if(frontPointStart.getTime()<cycleTime){
                frontPointStart = new Date(cycleTime);
                limitFlag = true;
            }
            midPro = dataService.getAllDatas(unit.getCalcParms().get(0).getMpointid(),
                    unit.getCalcParms().get(0).getPoint(),
                    unit.getCalcParms().get(0).getDatasource(), frontPointStart,
                    frontPointEnd);
            //按照月进行取值
            if ((midPro == null || midPro.size() <= 0)&&!limitFlag){
                Calendar instance = Calendar.getInstance();
                instance.setTime(frontPointEnd);
                instance.set(Calendar.MONTH,-1);
                frontPointStart = new Date(instance.getTimeInMillis());
                if(frontPointStart.getTime()<cycleTime){
                    frontPointStart = new Date(cycleTime);
                }

                midPro = dataService.getAllDatas(unit.getCalcParms().get(0).getMpointid(),
                        unit.getCalcParms().get(0).getPoint(),
                        unit.getCalcParms().get(0).getDatasource(), frontPointStart,
                        frontPointEnd);

            }
        }

        if (midPro == null || midPro.size()<=0){
            logger.info("在"+frontPointStart+"到"+frontPointEnd+":前点没有数据");
            return null;
        }else {

            midPro.sort(new Comparator<BaseData>() {
                @Override
                public int compare(BaseData o1, BaseData o2) {
                    return o1.getDataDt().getTime()>o2.getDataDt().getTime()?-1:1;
                }
            });

            logger.info("在"+frontPointStart+"到"+frontPointEnd+":前点的值为:"+midPro.get(0).getValue());
            return midPro.get(0).getValue();
        }
    }
}
