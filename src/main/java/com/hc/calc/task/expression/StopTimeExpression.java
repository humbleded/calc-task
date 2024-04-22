package com.hc.calc.task.expression;

import com.hc.calc.task.config.ExecuteStatus;
import com.hc.calc.task.model.BaseData;
import com.hc.calc.task.model.ComputingUnit;
import com.hc.calc.task.model.ExecuteRes;
import com.hc.calc.task.service.DataService;
import com.hc.calc.task.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component("stopTime")
public class StopTimeExpression extends BaseExpression {

    @Autowired
    private DataService dataService;

    @Override
    public ExecuteRes execute(ComputingUnit unit) throws Exception {
		ExecuteRes excuteRes = new ExecuteRes();
		List<BaseData> list = null;
		Double result = 0.0;
		int thisValue = -1;
		Date lastDataDt = unit.getStartDT();
		Long end = (unit.getEndDT().getTime()-1<unit.getStartDT().getTime())?unit.getEndDT().getTime():unit.getEndDT().getTime()-1;
		list = dataService.getAllDatasNew(unit.getCalcParms().get(0).getMpointid(),
			unit.getCalcParms().get(0).getPoint(),
			unit.getCalcParms().get(0).getDatasource(), unit.getStartDT().getTime(),
			end);
		if (list == null || list.size() == 0) {
		    return new ExecuteRes(null, null, ExecuteStatus.ERROR,
			    "测点ID为 " + unit.getCalcParms().get(0).getMpointid()
				    + " 在时间范围：" + DateUtil.format(unit.getStartDT())
				    + "至" + DateUtil.format(unit.getEndDT()) + "无数据");
		}
		for (int i = 0; i < list.size(); i++) {
		    thisValue = (int) list.get(i).getValue();
		    if (0 == thisValue) {
			    result = result + (list.get(i).getDataDt().getTime() - lastDataDt.getTime());
		    }
		    lastDataDt = list.get(i).getDataDt();
		}
	
		if (0 == thisValue) {
		    result = result + (unit.getEndDT().getTime() - lastDataDt.getTime());
		}
		result = result / 1000.0 / 60.0;
		logger.info("任务id:{}计算{}时的数据，在源测点id:{}从{}到{}时间范围内共取到数据{}条,关机时间{}",
			unit.getTaskid(), DateUtil.format(unit.getSaveDT()),
			unit.getCalcParms().get(0).getMpointid(),
			DateUtil.format(unit.getStartDT()),
			DateUtil.format(unit.getEndDT()), list.size(), result);
		excuteRes.setSuccessVal(result);
		return excuteRes;
    }
}
