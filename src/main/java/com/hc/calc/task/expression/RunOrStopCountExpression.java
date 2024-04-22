package com.hc.calc.task.expression;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.hc.calc.task.config.ExecuteStatus;
import com.hc.calc.task.model.BaseData;
import com.hc.calc.task.model.CalcParm;
import com.hc.calc.task.model.ComputingUnit;
import com.hc.calc.task.model.ExecuteRes;
import com.hc.calc.task.service.DataService;
import com.hc.calc.task.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("runOrStopCount")
public class RunOrStopCountExpression extends BaseExpression {

    @Autowired
    private DataService dataService;

    @Override
    public ExecuteRes execute(ComputingUnit unit) throws Exception {
		Map<String, Object> param = new HashMap<>();
    	if (unit.getCalcParms() == null || unit.getCalcParms().size() != 1) {
    		String errorMsg = "计算任务：" + unit.getTaskid() + ", " + DateUtil.format(unit.getStartDT()) + "至" 
    	        + DateUtil.format(unit.getEndDT()) + ", 源测点不存在或者非法";
    		return new ExecuteRes(null, null, ExecuteStatus.ERROR, errorMsg);
    	}
		Long end = (unit.getEndDT().getTime()-1<unit.getStartDT().getTime())?unit.getEndDT().getTime():unit.getEndDT().getTime()-1;
    	CalcParm parmMpoint = unit.getCalcParms().get(0);                //现暂时只支持单测点
    	List<BaseData> datas = dataService.getAllDatasNew(parmMpoint.getMpointid(), parmMpoint.getPoint(), parmMpoint.getDatasource()
    			, unit.getStartDT().getTime(), end);
		
		if (datas == null || datas.size() == 0) {
			String errorMsg = "测点ID为 " + parmMpoint.getMpointid()
				    + " 在时间范围：" + DateUtil.format(unit.getStartDT()) + "至" + DateUtil.format(unit.getEndDT()) + "无数据";
		    return new ExecuteRes(null, null, ExecuteStatus.ERROR, errorMsg);
		}
		int count = 0;
		//如果满足条件，则用当前数据时间减前一条数据时间，差值相加，如果为最后一条数据，需要再加上其与endTime之间的时间差
		int prev = -1;
		for (int i = 0; i < datas.size(); i++) {
			param.put(parmMpoint.getCode(), datas.get(i).getValue());
			boolean res;
			synchronized (AviatorEvaluator.class) {
				Expression compile = AviatorEvaluator.compile(unit.getExpression());
				res = (Boolean) compile.execute(param);
			}
			int current;
			if (res) {
				current = 1;
			} else {
				current = 0;
			}
			if (prev == 0 && current == 1) {
				count ++;
			}
			prev = current;
		}
		
		logger.info("任务id:{}计算{}时的数据，在源测点id:{}从{}到{}时间范围内共取到数据{}条,启动/停机时间{}",
			unit.getTaskid(), DateUtil.format(unit.getSaveDT()), parmMpoint.getMpointid(), DateUtil.format(unit.getStartDT()),
			DateUtil.format(unit.getEndDT()), datas.size(), count);
		return new ExecuteRes(Double.valueOf(count), null,
				ExecuteStatus.SUCCESS, null);
    }
}
