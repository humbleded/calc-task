package com.hc.calc.task.thread;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hc.calc.task.config.BaseConfig;
import com.hc.calc.task.config.Constants;
import com.hc.calc.task.config.ExecuteStatus;
import com.hc.calc.task.config.JsonDateValueProcessor;
import com.hc.calc.task.excption.CalcBaseException;
import com.hc.calc.task.expression.BaseExpression;
import com.hc.calc.task.expression.ShiftHourExpression;
import com.hc.calc.task.model.*;
import com.hc.calc.task.service.CalcLogService;
import com.hc.calc.task.service.CalcShiftService;
import com.hc.calc.task.service.DataService;
import com.hc.calc.task.util.CoreUtil;
import com.hc.calc.task.zookeeper.ZookeeperClient;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.JSONUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.concurrent.TimeUnit;

/**
 * @author Holger
 * @date 2018/4/27
 */
public class TaskThread implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private KafkaStream<byte[], byte[]> stream;

	private DataService dataService;

	private BeanFactory factory;

	private ConsumerConnector consumer;

	private CalcLogService calcLogService;

	private CalcShiftService calcShiftService;

	public TaskThread(KafkaStream<byte[], byte[]> stream, DataService dataService, BeanFactory factory,
					  ConsumerConnector consumer, CalcShiftService shiftService) {
		this.factory = factory;
		this.dataService = dataService;
		this.stream = stream;
		this.consumer = consumer;
		this.calcLogService = factory.getBean("calcLogService", CalcLogService.class);
		this.calcShiftService = shiftService;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void run() {
		ConsumerIterator<byte[], byte[]> it = stream.iterator();
		int count = 0;
		while (it.hasNext()) {
			count++;
			CalcLog log = null;
			// 每次消费之前判断当前节点是否退出
			if (!BaseConfig.CLIENT_STATUS) {
				System.out.println(Thread.currentThread().getName() + " exit!");
				return;
			}
			ComputingUnit unit = null;
			ExecuteRes res = null;
			JSONObject obj = null;
			Long end = null;
			try {
				MessageAndMetadata<byte[], byte[]> mam = it.next();
				JsonConfig config = new JsonConfig();
				JsonDateValueProcessor jsonValueProcessor = new JsonDateValueProcessor();
				config.registerJsonValueProcessor(Date.class, jsonValueProcessor);
				JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(new String[] { "yyyy-MM-dd HH:mm:ss" }));
				obj = JSONObject.fromObject(new String(mam.message()), config);
				// 转换成unit对象
				unit = (ComputingUnit) JSONObject.toBean(obj, ComputingUnit.class);
				// list转换
				unit.setCalcParms((List) JSONArray.toCollection(obj.getJSONArray("calcParms"), CalcParm.class));
				BaseExpression exp;
				// 判断计算方法是否是四则算法或者
				// TODO: 2018/7/3 此处是兼容写法，为了保证刚开始的设计的根据算法名获取类能正常工作
				//if (Constants.FOUR_ARITHMETIC_OPERATOR.equals(unit.getFtype()) || CoreUtil.isCustom(unit.getExpression())) {


				logger.info(unit.toString());

				if (Constants.FOUR_ARITHMETIC_OPERATOR.equals(unit.getFtype())) {
					exp = factory.getBean(Constants.FOUR_ARITHMETIC_OPERATOR, BaseExpression.class);
				} else if (Constants.EQU_RUN_TIME.equals(unit.getFtype()) || Constants.EQU_STOP_TIME.equals(unit.getFtype())){
				    exp = factory.getBean(Constants.RUN_OR_STOP_TIME, BaseExpression.class);
				} else if (Constants.EQU_RUN_COUNT.equals(unit.getFtype()) || Constants.EQU_STOP_COUNT.equals(unit.getFtype())){
				    exp = factory.getBean(Constants.RUN_OR_STOP_COUNT, BaseExpression.class);
				} else if (Constants.DURATION_OF_OPERATION.equals(unit.getFtype()) || Constants.DURATION_OF_OPERATION.equals(unit.getFtype())){
					exp = factory.getBean(Constants.DURATION_OF_OPERATION, BaseExpression.class);
				}else {
					// 获取计算算法的bean
					exp = factory.getBean(unit.getExpression(), BaseExpression.class);
				}
				// 获取计算结果
				res = exp.execute(unit);
				// 更新数据
				if (res.getStatus().equals(ExecuteStatus.SUCCESS.getStatus())) {
					dataService.addData(res, unit);
				}

				log = new CalcLog(res, unit);
			} catch (CalcBaseException e) {
				log = new CalcLog(new ExecuteRes(null, null, ExecuteStatus.ERROR, e.getMessage()), unit);
			} catch (Exception e) {
				logger.error(obj + "");
				logger.error(e.getMessage());
				logger.error("未知异常", e);
				log = new CalcLog(new ExecuteRes(null, null, ExecuteStatus.ERROR, "未知异常导致计算("+e.getMessage()+")，请联系管理员！"), unit);
			} finally {
				try {
					if (unit.getType().equals(UnitType.ROLL.getType())) {
						CalcRoll roll = CoreUtil.log2Roll(unit, log);
						calcLogService.updateRoll(roll);
						//并且删除当前计算任务之前算的结果
						calcLogService.deleteOldLog(roll.getDatadt(),roll.getTaskid());
					}
					if (unit.getCycle().equals(Constants.CYCLE_SHIFT)&&!unit.getType().equals(UnitType.ROLL.getType())){
						calcShiftService.deleteOldLog(unit.getMpointId(),unit.getSaveDT());

						CalcShift calcShift = new CalcShift(unit.getStartDT(),unit.getMpointId(),unit.getShiftsTypeName(),unit.getShiftName(),unit.getStartDT(),unit.getEndDT());
						calcShiftService.save(calcShift);
					}
					calcLogService.saveLog(log);


				} catch (Exception e2) {
					e2.printStackTrace();
				}
				try {
					String node = BaseConfig.TASK_NUM_PATH;
					if (unit.getType().equals(UnitType.ROLL.getType())) {
						node = BaseConfig.ROLL_NUM_PATH;
					}
					ZookeeperClient zclient = factory.getBean("zkClient", ZookeeperClient.class);
					CuratorFramework client = zclient.getZclient();
					InterProcessSemaphoreMutex lock = new InterProcessSemaphoreMutex(client, node);
					// InterProcessMutex locks = new InterProcessMutex(client,
					// node);
					logger.info("{} Start to acquire the lock.......", count);
					if (lock.acquire(10, TimeUnit.SECONDS)) {
						String data = new String(client.getData().forPath(node));
						String wdata = new String(new Long(data).longValue() - 1 + "");
						client.setData().forPath(node, wdata.getBytes());
						logger.info("{} Acquired the lock: {}, {}", count, data, wdata);
						lock.release();
					} else {
						logger.info("{} Fail to acquired the lock......", count);
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.info("{} Fail to build the lock!", count, e);
				}
				consumer.commitOffsets();
			}
		}
	}


/*	public static void main(String[] args) throws Exception {
		JsonConfig config = new JsonConfig();
		JsonDateValueProcessor jsonValueProcessor = new JsonDateValueProcessor();
		config.registerJsonValueProcessor(Date.class, jsonValueProcessor);
		JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(new String[] { "yyyy-MM-dd HH:mm:ss" }));
		JSONObject jsonObject = JSONObject.fromObject(new String("{\"mpointId\":2907,\"expression\":\"shiftHour\",\"saveDT\":\"2021-09-26 13:00:00\",\"endDT\":\"2021-09-26 14:00:00\",\"type\":\"task\",\"cycle\":3600,\"point\":\"CALC-2907\",\"shiftName\":\"\",\"ftype\":\"aggregation\",\"shiftsTypeName\":\"\",\"startDT\":\"2021-09-26 13:00:00\",\"calcParms\":[{\"mpointid\":2104,\"code\":\"mpoint\",\"defaultval\":0,\"datasource\":\"AUTO\",\"id\":2,\"mpointName\":\"出水累计流量\",\"point\":\"1000000000-CSFLOW_LJ\",\"taskid\":2}],\"initialValue\":0,\"taskid\":2}"), config);
		// 转换成unit对象
		ComputingUnit unit = (ComputingUnit) JSONObject.toBean(jsonObject, ComputingUnit.class);
		System.out.println(unit);

		ShiftHourExpression shiftHourExpression = new ShiftHourExpression();
		unit.setCalcParms((List) JSONArray.toCollection(jsonObject.getJSONArray("calcParms"), CalcParm.class));
		shiftHourExpression.execute(unit);
	}*/

}
