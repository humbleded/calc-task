package com.hc.calc.task.expression;


import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.hc.calc.task.config.ExecuteStatus;
import com.hc.calc.task.dao.MpointInputDataMapper;
import com.hc.calc.task.model.*;
import com.hc.calc.task.service.DataService;
import com.hc.calc.task.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.*;

/**
 * @author jcf
 * 总运行时长
 */

@Component("durationOfOperation")
public class DurationOfOperationExpression extends  BaseExpression {


    @Autowired
    private DataService dataService;

    @Autowired
    private MpointInputDataMapper mpointInputDataMapper;

    @Autowired
    private RunTimeExpression runTimeExpression;

    private DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public ExecuteRes execute(ComputingUnit unit) throws Exception {
        //获取当前时间前点

        logger.info("总运行时长算法参数:"+unit.toString());
        if (UnitType.ROLL.getType().equals(unit.getType())&& (unit.getShiftsTypeName() == null||"".equals(unit.getShiftsTypeName()))) {
            //重新计算(非班次)是一次性拿全部的数据
            BaseData calcHistoryLog = mpointInputDataMapper.getOneCalcHistoryLogBeforeTime(unit.getMpointId(), unit.getStartDT());
            Double frontPointValue = getFrontPoint(unit,true);
            if (calcHistoryLog ==null){
                return getValueByTimeSolt(unit,null,unit.getInitialValue());
            }else {
                return getValueByTimeSolt(unit,calcHistoryLog,null);
            }
        }else if (unit.getShiftsTypeName() != null && !"".equals(unit.getShiftsTypeName())){
            //班次重新计算或者班次的正常计算，不需要取前点
            BaseData calcHistoryLog = mpointInputDataMapper.getOneCalcHistoryLogBeforeTime(unit.getMpointId(), unit.getStartDT());
            if (calcHistoryLog == null){
                return getValueByTimeSolt(unit,null,unit.getInitialValue());
            }else {
                return getValueByTimeSolt(unit,calcHistoryLog,null);
            }
        }else {
            BaseData calcHistoryLog = mpointInputDataMapper.getOneCalcHistoryLogBeforeTime(unit.getMpointId(), unit.getStartDT());
            //进行正常计算，需要判断是否是第一次计算需要根据taskID获取当前记录
            if (calcHistoryLog == null) {
                //正常计算-第一次计算，小时差值计算
                Double frontPointValue = getFrontPoint(unit,true);
                    return getValueByTimeSolt(unit,null,unit.getInitialValue());
            } else {
                //正常计算-已经计算过了
                Date frontPointEnd = new Date(unit.getEndDT().getTime()-60*60*1000);
                Double frontPointValue = getFrontPoint(unit,true);
                logger.info("当前已经计算过的数据:"+calcHistoryLog.getValue()+" 前点值:"+frontPointValue);
                return getValueByTimeSolt(unit,calcHistoryLog,null);
            }
        }
    }

    private ExecuteRes getValueByTimeSolt(ComputingUnit unit,BaseData lastCalcValue,Integer initValue) throws Exception {
        logger.info("初始值：{}，上次计算数据:{}", initValue, lastCalcValue);
        Map<String, Object> param = new HashMap<>();
        ExecuteRes excuteRes = new ExecuteRes();
        List<BaseData> list = null;
        Double result = 0.0;
        list = dataService.getAllDatas(unit.getCalcParms().get(0).getMpointid(),
                unit.getCalcParms().get(0).getPoint(),
                unit.getCalcParms().get(0).getDatasource(), unit.getStartDT(),
                unit.getEndDT());


        if (list == null || list.size() == 0) {
            return new ExecuteRes(null, null, ExecuteStatus.ERROR,
                    "测点ID为 " + unit.getCalcParms().get(0).getMpointid()
                            + " 在时间范围：" + DateUtil.format(unit.getStartDT())
                            + "至" + DateUtil.format(unit.getEndDT()) + "无数据");
        }
        Long timeSum = 0L;
        CalcParm parmMpoint = unit.getCalcParms().get(0);
        //如果满足条件，则用当前数据时间减前一条数据时间，差值相加，如果为最后一条数据，需要再加上其与endTime之间的时间差
        Long timeTemp = unit.getStartDT().getTime();
        for (int i = 0; i < list.size(); i++) {

            param.put(parmMpoint.getCode(), list.get(i).getValue());
            boolean res;
            synchronized (AviatorEvaluator.class) {
                Expression compile = AviatorEvaluator.compile(unit.getExpression());
                res = (Boolean) compile.execute(param);
            }
            if (res) {
                timeSum += list.get(i).getDataDt().getTime() - timeTemp;
                if (i == list.size() -1) {
                    timeSum += unit.getEndDT().getTime() - list.get(i).getDataDt().getTime();
                }
            }
            timeTemp = list.get(i).getDataDt().getTime();
        }

        logger.info("当前计算的时间累计："+timeSum);
        result = Double.valueOf(df.format((float) (timeSum) / 1000.0 / 60.0/60.0));

        if (lastCalcValue != null){
            result = lastCalcValue.getValue() + result;
        }
        if (initValue != null){
            result += initValue;
        }
        logger.info("任务id:{}计算{}时的数据，在源测点id:{}从{}到{}时间范围内共取到数据{}条,启动时间{}",
                unit.getTaskid(), DateUtil.format(unit.getSaveDT()),
                unit.getCalcParms().get(0).getMpointid(),
                DateUtil.format(unit.getStartDT()),
                DateUtil.format(unit.getEndDT()), list.size(), result);
        excuteRes.setSuccessVal(result);
        return excuteRes;
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
