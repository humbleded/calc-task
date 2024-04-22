package com.hc.calc.task.util;

import com.hc.calc.task.model.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author Holger
 * @date 2018/5/11
 */
public class CoreUtil {
    /**
     * 
     * 将OpenTSDB查询返回的String数据转换成数据List
     * 
     * @param data
     *            OpenTSDB查询返回的String数据
     * @return List
     */
    public static List<BaseData> ots2Data(String data) {
	if (data == null || data.isEmpty()) {
	    return null;
	}
	JSONArray json = JSONArray.fromObject(data);
	if(json == null || json.size() == 0){
	    return null;
	}
	JSONArray js = JSONArray
		.fromObject("[" + json.getJSONObject(0).get("dps") + "]");
	List<BaseData> res = new ArrayList<BaseData>();
	for (int i = 0; i < js.size(); i++) {
	    JSONObject jobj = js.getJSONObject(i);
	    Iterator<String> it = jobj.keySet().iterator();
	    while (it.hasNext()) {
		String key = it.next();
		BaseData bd = new BaseData();
		Date datetime = new Date(Long.parseLong(key) * 1000);
		bd.setDataDt(datetime);
		bd.setValue(jobj.getDouble(key));
		res.add(bd);
	    }
	}
	return res;
    }

    public static boolean isCustom(String expression) {
	if (expression.indexOf("+") != -1) {
	    return true;
	}
	if (expression.indexOf("-") != -1) {
	    return true;
	}
	if (expression.indexOf("*") != -1) {
	    return true;
	}
	if (expression.indexOf("/") != -1) {
	    return true;
	}
	return false;
    }

    /**
     * 
     * @param unit 计算单元
     * @param log 单元的计算日志
     * @return 补算记录
     */
    public static CalcRoll log2Roll(ComputingUnit unit, CalcLog log) {
	CalcRoll roll = new CalcRoll();
	roll.setTaskid(unit.getTaskid());
	roll.setDatadt(unit.getSaveDT());
	roll.setFinishdt(new Date());
	if(log != null &&(log.getError() == null || log.getError().isEmpty())){
	    roll.setStatus(RollState.NORMAL.getState());
	    roll.setExcuteVal(log.getExcuteVal());
		System.out.println("算出结果了");
	}else{
		roll.setError(log.getError());
	    roll.setStatus(RollState.ERROR.getState());
		System.out.println("结果存在问题");
	}
		System.out.println(log.toString());
		System.out.println(roll.toString());
	return roll;
    }
}
