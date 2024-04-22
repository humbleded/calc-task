package com.hc.calc.task.expression;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.hc.calc.task.config.ExecuteStatus;
import com.hc.calc.task.model.*;
import com.hc.calc.task.service.DataService;
import com.hc.calc.task.service.MpointService;
import com.hc.calc.task.util.DateUtil;

import com.hc.calc.task.util.OpentsdbUtil;
import org.opentsdb.client.bean.request.SubQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Holger
 * @date 2018/4/28
 */
@Component("arith4Operator")
public class Arith4OperatorExpression extends BaseExpression {

	@Autowired
	private DataService service;

	@Autowired
	MpointService mpointService;

	/**
	 * 需要区分当前测点类型
	 * 		自动采集测点去OpenTEDB进行查询
	 * 		其他测点在mysql中进行查询
	 *
	 * 	对于自动采集的测点：
	 * 		首先需要进行测点point获取生成TSSubQuery对象，然后查询出来
	 *
	 * 	查询出来的数据需要进行判断当前测点是否存在数据，如果存在数据，报错，如果全部都存在数据，进行计算部分的操作
	 */

	@Override
	public ExecuteRes execute(ComputingUnit unit) throws Exception {
		logger.info("四则运算算法参数:"+unit.toString());
		Map<String, Object> param = new HashMap<>();
		Map<String, Integer> numMap = new HashMap<>();
		ArrayList<SubQuery> queryList = new ArrayList<>();
		List<BaseData> mpointDatas = new ArrayList();

		List<String> mpointIds = unit.getCalcParms().stream().map(x->{return  String.valueOf(x.getMpointid());}).collect(Collectors.toList());

		List<Mpoint> mpointsByIds = mpointService.getMpointsByIds(mpointIds);

		//根据不同的测点类型对应相应的查询方式
		List<Mpoint> autoMpoints = mpointsByIds.stream().filter(a -> a.getDatasource().equals(MpointDataSource.AUTO.getDatasource())).collect(Collectors.toList());
		Map<String, String> collect = autoMpoints.stream().collect(Collectors.toMap(Mpoint::getPoint, x->{return  String.valueOf(x.getId());},(k1,k2)->k1));

		List<Mpoint> notAutoMpoints = mpointsByIds.stream().filter(a -> !a.getDatasource().equals(MpointDataSource.AUTO.getDatasource())).collect(Collectors.toList());

		List<Long> notAutoMpointIds = notAutoMpoints.stream().map(Mpoint::getId).collect(Collectors.toList());
		if (notAutoMpoints != null && notAutoMpoints.size() > 0){
			List<BaseData> notAutoBaseDatas = service.prev(notAutoMpointIds,unit.getStartDT());
			if (notAutoBaseDatas != null && notAutoBaseDatas.size()>0){
				mpointDatas.addAll(notAutoBaseDatas);
			}
		}

		if (autoMpoints != null && autoMpoints.size() > 0){
			autoMpoints.forEach(e -> {
				numMap.put(e.getPoint(), e.getNumtail() == null ? 0 : e.getNumtail().intValue());

				if (MpointDataSource.AUTO.getDatasource().equals(e.getDatasource()) && !MpointDataSource.CHAR.equals(e.getDatype())) {
					queryList.add(OpentsdbUtil.getSubQuery(e.getPoint(), "C|I|S|E|U|G|Q",
							e.getDatype(), "none"));
				}
			});

			List<BaseData> datas = service.getDatas(DateUtil.format("yyyy/MM/dd HH:mm:ss", DateUtil.getPrevSpan(unit.getStartDT())),
					DateUtil.format("yyyy/MM/dd HH:mm:ss", unit.getStartDT()), queryList, collect);

			if (datas != null && datas.size() > 0){
				mpointDatas.addAll(datas);
			}
		}

		Map<Long, Double> collect1 = mpointDatas.stream().collect(Collectors.toMap(BaseData::getMpointIds, BaseData::getValue, (k1, k2) -> k2));

		for (CalcParm parm : unit.getCalcParms()) {
			Double val = collect1.get(parm.getMpointid());
			logger.info("任务id:{}计算{}时的数据，在源测点id:{},参数代码为{}从{}到{}时间范围取得值为{}",
					unit.getTaskid(), DateUtil.format(unit.getSaveDT()),
					parm.getMpointid(), parm.getCode(),
					DateUtil.format(unit.getStartDT()),
					DateUtil.format(unit.getEndDT()), val);
			if (parm.getDefaultval() !=null && val == null){
				val = parm.getDefaultval().doubleValue();
			}
			if (val == null) {
				return new ExecuteRes(null, null, ExecuteStatus.ERROR,
						"测点 " + parm.getMpointid() + "在："
								+ DateUtil.format(unit.getStartDT()) + "无数据");
			}
			param.put(parm.getCode(), val);
		}
		Object res;
		synchronized (AviatorEvaluator.class) {
			Expression compile = AviatorEvaluator.compile(unit.getExpression());
			res = compile.execute(param);
		}
		logger.info("任务id:{}计算{}时的数据，计算{}结果为{}", unit.getTaskid(),
				unit.getExpression(), DateUtil.format(unit.getSaveDT()), res);
		if (res == null || ("NaN").equals(res.toString())
				|| res.toString().endsWith("Infinity")) {
			return new ExecuteRes(null, null, ExecuteStatus.ERROR, "/ by zero");
		}

		return new ExecuteRes(Double.valueOf(String.valueOf(res)), null,
				ExecuteStatus.SUCCESS, null);
	}
}
