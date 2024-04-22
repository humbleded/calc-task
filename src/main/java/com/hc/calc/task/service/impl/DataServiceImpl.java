package com.hc.calc.task.service.impl;

import com.haocang.common.domain.mpoint.AutoMpointRTDataCacheDTO;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hc.calc.task.config.BaseConfig;
import com.hc.calc.task.dao.MpointInputDataMapper;
import com.hc.calc.task.kafka.KafkaProducerConfig;
import com.hc.calc.task.model.*;
import com.hc.calc.task.service.DataService;
import com.hc.calc.task.util.DateUtil;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import org.opentsdb.client.OpenTSDBClient;
import org.opentsdb.client.bean.request.Query;
import org.opentsdb.client.bean.request.SubQuery;
import org.opentsdb.client.bean.response.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Holger
 * @date 2null18/4/26
 */
@Service
public class DataServiceImpl implements DataService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final String CALC_TASK_RECALC = "calcTaskRecalc";
    @Autowired
    private MpointInputDataMapper mapper;
	@Autowired
	private OpenTSDBClient openTSDBClient;
    @Autowired
    private KafkaProducerConfig producerConfig;
    private IMap<String, AutoMpointRTDataCacheDTO> mpointAutoRTdataMap;

	public DataServiceImpl(HazelcastInstance hz ) {
        mpointAutoRTdataMap = hz.getMap("loong-mpoint-auto-rtdata-cache-map");
    }

	public List<QueryResult> queryOpentTSDB(Query query){
		try {
			return openTSDBClient.query(query);
		} catch (Exception e) {
			logger.error("opentsdb查询异常原因:{}",e);
		}
		return new ArrayList<>();
	}


	/**
	 * 获取数据
	 * @param metric
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<BaseData> getDatas(String metric, String startTime, String endTime) {
		String tagValue = "C|I|S|E|U|G";
		// 创建一个filter的集合
		List<SubQuery.Filter> filters = new ArrayList<>();
		List<SubQuery> subQueries = new ArrayList<>();
		SubQuery.Filter filter = SubQuery.Filter.filter(SubQuery.Filter.FilterType.LITERAL_OR, "QoD", tagValue, true).build();
		filters.add(filter);
		// 创建一个SubQuery对象
		SubQuery subQuery1 = SubQuery.metric(metric)
				.aggregator(SubQuery.Aggregator.NONE)
				.filter(filters)
				.build();
		subQueries.add(subQuery1);
		Query query = Query.begin(startTime)
				.end(endTime)
				.sub(subQueries)
				.msResolution()
				.build();
		Long endT;
		if (endTime.contains("/") || endTime.contains(":")) {
			endT = DateUtil.parse("yyyy/MM/dd HH:mm:ss", endTime).getTime();
		} else {
			endT = Long.valueOf(endTime);
		}
		try {
			List<QueryResult> queryResults = this.queryOpentTSDB(query);
			List<BaseData> datas = new ArrayList<>();
			// point -> List<QueryResult>
			Map<String, List<QueryResult>> queryResultByPoint = queryResults.stream().collect(Collectors.groupingBy(QueryResult::getMetric));
			for (Map.Entry<String, List<QueryResult>> queryResultByPointEntry : queryResultByPoint.entrySet()) {
				Map<Long, BaseData> cMap = new HashMap<>();
				Map<Long, BaseData> iMap = new HashMap<>();
				Map<Long, BaseData> sMap = new HashMap<>();
				Map<Long, BaseData> eMap = new HashMap<>();
				Map<Long, BaseData> curveMap = new HashMap<>();

				for (QueryResult queryResult : queryResultByPointEntry.getValue()) {
					String key = queryResult.getTags().get("QoD");
					LinkedHashMap<Long, Number> dps = queryResult.getDps();
					for (Map.Entry<Long, Number> data : dps.entrySet()) {
						BaseData d = new BaseData();
						Long timestamp = Long.valueOf(data.getKey().toString());
						Double value = Double.valueOf(data.getValue().toString());
						if (timestamp <= endT) {
							d.setDataDt(new Date(timestamp));
							d.setValue(value);
							if ("I".equals(key)) {
								iMap.put(timestamp, d);
							} else if ("C".equals(key)) {
								cMap.put(timestamp, d);
							} else if ("S".equals(key)) {
								sMap.put(timestamp, d);
							} else if ("E".equals(key)) {
								eMap.put(timestamp, d);
							} else {
								curveMap.put(timestamp, d);
							}
						}
					}
				}

				for (Map.Entry<Long, BaseData> entry : eMap.entrySet()) {
					curveMap.put(entry.getKey(), entry.getValue());
				}
				for (Map.Entry<Long, BaseData> entry : sMap.entrySet()) {
					curveMap.put(entry.getKey(), entry.getValue());
				}
				for (Map.Entry<Long, BaseData> entry : iMap.entrySet()) {
					curveMap.put(entry.getKey(), entry.getValue());
				}
				for (Map.Entry<Long, BaseData> entry : cMap.entrySet()) {
					curveMap.put(entry.getKey(), entry.getValue());
				}
				for (Map.Entry<Long, BaseData> entry : curveMap.entrySet()) {
					datas.add(entry.getValue());
				}
			}
			return datas;
		} catch (Exception e) {
			logger.error("opentsdb查询结果处理异常:", e.getMessage());
		}
		return new ArrayList<>();
	}


//	// 查询实时值接口
//	public List<DataPoints[]> queryOpentTSDB(TSQuery query) {
//		List<DataPoints[]> results = new ArrayList<>();
//		TSDB tsdb = null;
//		try {
//			tsdb = OpentsdbUtil.getTsdb(BaseConfig.HBASE_ZK_QUORUM);
//		} catch (IOException e2) {
//			logger.error(e2.getMessage());
//		}
//		Query[] tsdbqueries = null;
//		try {
//			tsdbqueries = query.buildQueries(tsdb);
//		} catch (Exception e) {
//			throw new RuntimeException("Unexpected exception", e);
//		}
//		if (tsdbqueries == null || tsdbqueries.length == 0) {
//			return results;
//		}
//		int nqueries = tsdbqueries.length;
//
//		List<Deferred<DataPoints[]>> deferreds = new ArrayList<>(nqueries);
//		for (int i = 0; i < nqueries; ++i) {
//			deferreds.add(tsdbqueries[i].runAsync());
//		}
//		try {
//			class QueriesEB implements Callback<Object, Exception> {
//				QueriesEB() {
//				}
//
//				public Object call(Exception e) throws Exception {
//					logger.error("Queries failed:" + e.getMessage());
//					return null;
//				}
//			}
//			class QueriesCB implements Callback<Object, ArrayList<DataPoints[]>> {
//				QueriesCB() {
//				}
//
//				public Object call(ArrayList<DataPoints[]> queryResults) throws Exception {
//					results.addAll(queryResults);
//					return null;
//				}
//			}
//			Deferred.groupInOrder(deferreds).addCallback(new QueriesCB()).addErrback(new QueriesEB()).join();
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//		}
//		return results;
//	}

//	public List<BaseData> getDatas(String metric, String startTime, String endTime) {
//		String tagValue = "C|I|S|E|U|G|B";
//		TSQuery query = new TSQuery();
//		query.setStart(startTime);
//		query.setEnd(endTime);
//		TSSubQuery subQuery = new TSSubQuery();
//		subQuery.setMetric(metric);
//		List<TagVFilter> filters = new ArrayList<>(1);
//		filters.add((new Builder()).setType("literal_or").setFilter(tagValue).setTagk("QoD").setGroupBy(true).build());
//		subQuery.setFilters(filters);
//		subQuery.setAggregator("none");
//		ArrayList<TSSubQuery> subQueries = new ArrayList<>(1);
//		subQueries.add(subQuery);
//		query.setQueries(subQueries);
//		query.setMsResolution(true);
//		query.validateAndSetQuery();
//		List<DataPoints[]> list = this.queryOpentTSDB(query);
//		//Long endT = DateUtil.parse("yyyy/MM/dd HH:mm:ss", endTime).getTime();
//		Long endT;
//		if (endTime.contains("/") || endTime.contains(":")) {
//			endT = DateUtil.parse("yyyy/MM/dd HH:mm:ss", endTime).getTime();
//		} else {
//			endT = Long.valueOf(endTime);
//		}
//		List<BaseData> datas = new ArrayList<>();
//		Map<Long, BaseData> cMap = new HashMap<>();
//		Map<Long, BaseData> iMap = new HashMap<>();
//		Map<Long, BaseData> sMap = new HashMap<>();
//		Map<Long, BaseData> eMap = new HashMap<>();
//		Map<Long, BaseData> curveMap = new HashMap<>();
//		for (DataPoints[] dataSets : list) {
//			for (DataPoints data : dataSets) {
//				String key = data.getTags().get("QoD");
//				SeekableView view = data.iterator();
//				while (view.hasNext()) {
//					BaseData d = new BaseData();
//					DataPoint dp = view.next();
//					if (dp.timestamp() <= endT) {
//						d.setDataDt(new Date(dp.timestamp()));
//						d.setValue((dp.isInteger() ? dp.longValue() : dp.doubleValue()));
//						if ("I".equals(key)) {
//							iMap.put(dp.timestamp(), d);
//						} else if ("C".equals(key)) {
//							cMap.put(dp.timestamp(), d);
//						} else if ("S".equals(key)) {
//							sMap.put(dp.timestamp(), d);
//						} else if ("E".equals(key)) {
//							eMap.put(dp.timestamp(), d);
//						} else {
//							curveMap.put(dp.timestamp(), d);
//						}
//					}
//				}
//			}
//			for (Map.Entry<Long, BaseData> entry : eMap.entrySet()) {
//				curveMap.put(entry.getKey(), entry.getValue());
//			}
//			for (Map.Entry<Long, BaseData> entry : sMap.entrySet()) {
//				curveMap.put(entry.getKey(), entry.getValue());
//			}
//			for (Map.Entry<Long, BaseData> entry : iMap.entrySet()) {
//				curveMap.put(entry.getKey(), entry.getValue());
//			}
//			for (Map.Entry<Long, BaseData> entry : cMap.entrySet()) {
//				curveMap.put(entry.getKey(), entry.getValue());
//			}
//			for (Map.Entry<Long, BaseData> entry : curveMap.entrySet()) {
//				datas.add(entry.getValue());
//			}
//		}
//		return datas;
//	}

	@Override
	public int addData(ExecuteRes res, ComputingUnit unit) {
		MpointInputData data = new MpointInputData(res, unit);
		int count = mapper.addData(data);
		AutoMpointRTDataCacheDTO rtData = mpointAutoRTdataMap.get(unit.getPoint());
		logger.info("Starting to update realtime cache.....{}/{}", data.getMpointid(), data.getDatadt());
		logger.info("The old realtime cache is.....{}/{}", rtData == null ? null : rtData.toString());
		if (rtData == null || rtData.getDatadt() == null
				|| data.getDatadt().getTime() >= rtData.getDatadt().getTime()) {
			AutoMpointRTDataCacheDTO newRtData = new AutoMpointRTDataCacheDTO();
			newRtData.setDatadt(data.getDatadt());
			newRtData.setValue(data.getValue() + "");
			mpointAutoRTdataMap.put(unit.getPoint(), newRtData);
			logger.info("SuccessFully updating the cache.....{}/{}", data.getMpointid(), data.getDatadt());
		}
		if (UnitType.ROLL.getType().equals(unit.getType())) {
			logger.error("1");
			Producer<String, String> producer = producerConfig.getKafkaProducer();
			List<KeyedMessage<String, String>> messageList = new ArrayList<KeyedMessage<String, String>>();

			messageList.add(new KeyedMessage<String, String>(BaseConfig.recalcDataTopic,
					toJsonMetric(unit.getPoint(), data.getDatadt())));
			logger.error("2");
			producer.send(messageList);
			logger.error("3");
			logger.info("Send recalc data to kafka:{}, {}", data.getMpointid(), data.getDatadt());
		}
		return count;
	}

	private String toJsonMetric(String point, Date time) {
		return " {\"name\" : \"" + point + "\",\"time\" : \"" + DateUtil.format("yyyy-MM-dd'T'HH:mm:ss'Z'", time) + "\""
				+ ",\"source\" : \"" + CALC_TASK_RECALC + "\"}";
	}

	@Override
	public Long count(Long id, String metric, String datasource, Date start, Date end) throws Exception {
		// 判断datasource类型，如不不为自动采集则向关系数据库查询
		if (!datasource.equals(MpointDataSource.AUTO.getDatasource())) {
			return mapper.count(id, start, end);
		} else {
			List<BaseData> datas = this.getDatas(metric, DateUtil.format("yyyy/MM/dd HH:mm:ss", start),
					DateUtil.format("yyyy/MM/dd HH:mm:ss", end));
			datas = checkData(datas, start.getTime(), end.getTime());
			logger.info("开始时间:{},结束时间:{}",DateUtil.format("yyyy/MM/dd HH:mm:ss", start),DateUtil.format("yyyy/MM/dd HH:mm:ss", end));
			if (datas == null || datas.size() == 0) {
				return null;
			} else {
				return new Long(datas.size());
			}
		}
	}


	@Override
	public List<BaseData> getAvgData(Long id, String metric, String downSample, String datasource, Date start, Date end)
			throws Exception {
		// 判断datasource类型，如不不为自动采集则向关系数据库查询
		if (!datasource.equals(MpointDataSource.AUTO.getDatasource())) {
			return mapper.getCycleAvgList(id, start, end);
		} else {
			List<BaseData> datas = this.getDatas(metric, DateUtil.format("yyyy/MM/dd HH:mm:ss", start),
					DateUtil.format("yyyy/MM/dd HH:mm:ss", end));
			datas = checkData(datas, start.getTime(), end.getTime());
			logger.info("开始时间:{},结束时间:{}",DateUtil.format("yyyy/MM/dd HH:mm:ss", start),DateUtil.format("yyyy/MM/dd HH:mm:ss", end));
			if (datas == null) {
				return null;
			} else {
				Collections.sort(datas, new Comparator<BaseData>() {
					@Override
					public int compare(BaseData o1, BaseData o2) {
						return o1.getDataDt().compareTo(o2.getDataDt());
					}
				});
				List<BaseData> returnList = new ArrayList<>();
				for (; start.before(end);) {
					start = DateUtil.addSecond(start, 600);
					Double returnValue = 0.0;
					int j = 0;
					for (BaseData data : datas) {
						if (data.getDataDt().before(start)) {
							returnValue += data.getValue();
							j++;
						}
					}
					returnValue = returnValue / j;
					BaseData b = new BaseData();
					b.setValue(returnValue);
					b.setDataDt(start);
					returnList.add(b);
				}

				for (BaseData data : returnList) {
					data.setValueText(String.valueOf(data.getDataDt().getTime() / 600000L));
				}
				return returnList;
			}
		}
	}

	@Override
	public Double sum(long id, String metric, String datasource, Date start, Date end) throws Exception {
		// 判断datasource类型，如不不为自动采集则向关系数据库查询
		if (!datasource.equals(MpointDataSource.AUTO.getDatasource())) {
			return mapper.sum(id, start, end);
		} else {
			List<BaseData> datas = this.getDatas(metric,  start.getTime()+"",
					(end.getTime()-1)+"");
			if (datas == null || datas.size() == 0) {
				return null;
			} else {
				Double returnValue = 0.0;
				for (BaseData data : datas) {
					returnValue += data.getValue();
				}
				return returnValue;
			}
		}
	}

	@Override
	public Double avg(long id, String metric, String datasource, Date start, Date end) throws Exception {
		// 判断datasource类型，如不不为自动采集则向关系数据库查询
		if (!datasource.equals(MpointDataSource.AUTO.getDatasource())) {
			return mapper.avg(id, start, end);
		} else {
			List<BaseData> datas = this.getDatas(metric, start.getTime()+"",
					(end.getTime()-1)+"");
			if (datas == null || datas.size() == 0) {
				return null;
			} else {
				BigDecimal returnValue = new BigDecimal(0);
				for (BaseData data : datas) {
					returnValue = returnValue.add(BigDecimal.valueOf(data.getValue()));
				}
				return returnValue.divide(BigDecimal.valueOf(datas.size()), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
		}
	}

	@Override
	public Double max(long id, String metric, String datasource, Date start, Date end) throws Exception {
		// 判断datasource类型，如不不为自动采集则向关系数据库查询
		if (!datasource.equals(MpointDataSource.AUTO.getDatasource())) {
			return mapper.max(id, start, end);
		} else {
			List<BaseData> datas = this.getDatas(metric, start.getTime()+"",
					(end.getTime()-1)+"");
			if (datas == null || datas.size() == 0) {
				return null;
			} else {
				Double returnValue = null;
				for (BaseData data : datas) {
					if (returnValue == null) {
						returnValue = data.getValue();
					} else {
						if (returnValue.compareTo(data.getValue()) <= 0) {
							returnValue = data.getValue();
						}
					}
				}
				return returnValue;
			}
		}
	}

	@Override
	public Double min(long id, String metric, String datasource, Date start, Date end) throws Exception {
		// 判断datasource类型，如不不为自动采集则向关系数据库查询
		if (!datasource.equals(MpointDataSource.AUTO.getDatasource())) {
			return mapper.min(id, start, end);
		} else {

			List<BaseData> datas = this.getDatas(metric, start.getTime()+"",
					(end.getTime()-1)+"");
			logger.info("源测点id:{}从{}到{}时间范围内值为{}", id, start, end, datas);
			if (datas == null || datas.size() == 0) {
				return null;
			} else {
				Double returnValue = null;
				for (BaseData data : datas) {
					if (returnValue == null) {
						returnValue = data.getValue();
					} else {
						if (returnValue.compareTo(data.getValue()) >= 0) {
							returnValue = data.getValue();
						}
					}
				}
				logger.info("源测点id:{}从{}到{}时间范围内最小值为{}", id, start, end, returnValue);
				return returnValue;
			}
		}
	}

	@Override
	public Double prev(long id, String metric, String datasource, Date start) throws Exception {
		// 判断datasource类型，如不不为自动采集则向关系数据库查询
		if (!datasource.equals(MpointDataSource.AUTO.getDatasource())) {
			return mapper.prev(id, start);
		} else {
			List<BaseData> datas = this.getDatas(metric,
					DateUtil.getPrevSpan(start).getTime()+"",
					start.getTime()+"");
			if (datas == null || datas.size() == 0) {
				return null;
			} else {
				Collections.sort(datas, new Comparator<BaseData>() {
					@Override
					public int compare(BaseData o1, BaseData o2) {
						return o2.getDataDt().compareTo(o1.getDataDt());
					}
				});
				return datas.get(0).getValue();
			}
		}
	}

	@Override
	public List<BaseData> getData(long id, String metric, String datasource, Date start, Date end) throws Exception {
		// 判断datasource类型，如不不为自动采集则向关系数据库查询
		if (!datasource.equals(MpointDataSource.AUTO.getDatasource())) {
			return mapper.getData(id, start, end);
		} else {
			List<BaseData> datas = this.getDatas(metric, DateUtil.format("yyyy/MM/dd HH:mm:ss", start),
					DateUtil.format("yyyy/MM/dd HH:mm:ss", end));
			return datas;
		}
	}

	@Override
	@Deprecated
	public List<BaseData> getAllDatas(long id, String metric, String datasource, Date start, Date end)
			throws Exception {
		// 判断datasource类型，如不不为自动采集则向关系数据库查询
		if (!datasource.equals(MpointDataSource.AUTO.getDatasource())) {
			return mapper.getData(id, start, end);
		} else {
			List<BaseData> datas = this.getDatas(metric, DateUtil.format("yyyy/MM/dd HH:mm:ss", start),
					DateUtil.format("yyyy/MM/dd HH:mm:ss", end));
			Collections.sort(datas, new Comparator<BaseData>() {
				@Override
				public int compare(BaseData o1, BaseData o2) {
					return o1.getDataDt().compareTo(o2.getDataDt());
				}
			});
			return datas;
		}
	}

	/**
	 * 时间既可以传入毫秒数也可以是时间格式
	 * @param id
	 * @param metric
	 * @param datasource
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<BaseData> getAllDatasNew(long id, String metric, String datasource, Long start, Long end)
			throws Exception {
		// 判断datasource类型，如不不为自动采集则向关系数据库查询
		if (!datasource.equals(MpointDataSource.AUTO.getDatasource())) {
			return mapper.getDataNew(id, new Date(start), new Date(end));
		} else {
			List<BaseData> datas = this.getDatas(metric,start+"", end+"");
			Collections.sort(datas, new Comparator<BaseData>() {
				@Override
				public int compare(BaseData o1, BaseData o2) {
					return o1.getDataDt().compareTo(o2.getDataDt());
				}
			});
			return datas;
		}
	}


	public List<BaseData> checkData(List<BaseData> data, Long start, Long end) {

		List<BaseData> result = new ArrayList<>();
		if (result != null&& !CollectionUtils.isEmpty(data)) {
			data.forEach(x->{
				if (x.getDataDt().getTime() >= start && x.getDataDt().getTime() < end) {
					result.add(x);
				}
			});
		}
		return result;
	}

	@Override
	public List<BaseData> prev(List<Long> notAutoMpointIds, Date startDT) {
		return mapper.prevs(notAutoMpointIds,startDT);
	}

	@Override
	public List<BaseData> getDatas(String startTime, String endTime, ArrayList<SubQuery> queryList, Map<String, String> collect) throws ParseException {
		List<BaseData> result = new ArrayList<>();
		Map<String, List<HData>> map = new HashMap<>();
		Query query = Query.begin(startTime)
				.end(endTime)
				.sub(queryList)
				.msResolution()
				.build();
		List<QueryResult> resultList = this.queryOpentTSDB(query);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Long endT = simpleDateFormat.parse(endTime).getTime();

		// point -> List<QueryResult>
		Map<String, List<QueryResult>> queryResultByPoint = resultList.stream().collect(Collectors.groupingBy(QueryResult::getMetric));
		for (Map.Entry<String, List<QueryResult>> queryResultByPointEntry : queryResultByPoint.entrySet()) {
			List<HData> datas = new ArrayList<>();
			Map<Long, HData> cMap = new HashMap<>();
			Map<Long, HData> iMap = new HashMap<>();
			Map<Long, HData> sMap = new HashMap<>();
			Map<Long, HData> eMap = new HashMap<>();
			Map<Long, HData> curveMap = new HashMap<>();

			for (QueryResult queryResult : queryResultByPointEntry.getValue()) {
				String key = queryResult.getTags().get("QoD");
				LinkedHashMap<Long, Number> dps = queryResult.getDps();
				for (Map.Entry<Long, Number> data : dps.entrySet()) {
					HData d = new HData();
					String value = data.getValue().toString();
					Long timestamp = Long.valueOf(data.getKey().toString());
					if (timestamp <= endT) {
						d.setDataDate(timestamp);
						d.setDataValue(value);
						if ("I".equals(key)) {
							iMap.put(timestamp, d);
						} else if ("C".equals(key)) {
							cMap.put(timestamp, d);
						} else if ("S".equals(key)) {
							sMap.put(timestamp, d);
						} else if ("E".equals(key)) {
							eMap.put(timestamp, d);
						} else {
							curveMap.put(timestamp, d);
						}
					}
				}
			}

			for (Map.Entry<Long, HData> entry : eMap.entrySet()) {
				curveMap.put(entry.getKey(), entry.getValue());
			}
			for (Map.Entry<Long, HData> entry : sMap.entrySet()) {
				curveMap.put(entry.getKey(), entry.getValue());
			}
			for (Map.Entry<Long, HData> entry : iMap.entrySet()) {
				curveMap.put(entry.getKey(), entry.getValue());
			}
			for (Map.Entry<Long, HData> entry : cMap.entrySet()) {
				curveMap.put(entry.getKey(), entry.getValue());
			}
			for (Map.Entry<Long, HData> entry : curveMap.entrySet()) {
				datas.add(entry.getValue());
			}
			Collections.sort(datas);
			map.put(queryResultByPointEntry.getKey(), datas);
		}

		for(Map.Entry<String, List<HData>> dataEntry: map.entrySet()){
			if (dataEntry.getKey().equals("")||dataEntry.getKey() == null){
				continue;
			}
			String mpointId = collect.get(dataEntry.getKey());
			BaseData data = new BaseData();
			List<HData> value = dataEntry.getValue();
			if(CollectionUtils.isEmpty(value)){
				continue;
			}
			// 时间倒序
			value.sort(new Comparator<HData>() {
				@Override
				public int compare(HData o1, HData o2) {
					return o1.getDataDate()>o2.getDataDate()?-1:1;
				}
			});
			data.setMpointIds(Long.valueOf(mpointId));
			if (value.size()<=0){
			}else {
				data.setValue(Double.valueOf(value.get(0).getDataValue()));
			}
			result.add(data);
		}
		return result;
	}

//	@Override
//	public List<BaseData> getDatas(String startTime, String endTime, ArrayList<TSSubQuery> queryList,
//								   Map<String, String> collect) throws ParseException {
//		List<BaseData> result = new ArrayList<>();
//		Map<String, List<HData>> map = new HashMap<>();
//		TSQuery query = new TSQuery();
//		query.setStart(startTime);
//		query.setEnd(endTime);
//		query.setQueries(queryList);
//		query.setMsResolution(true);
//		query.validateAndSetQuery();
//		List<DataPoints[]> list = new ArrayList<>();
//
//		list = this.queryOpentTSDB(query);
//
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//		Long endT = simpleDateFormat.parse(endTime).getTime();
//		for (DataPoints[] dataSets : list) {
//			List<HData> datas = new ArrayList<>();
//			Map<Long, HData> cMap = new HashMap<>();
//			Map<Long, HData> iMap = new HashMap<>();
//			Map<Long, HData> sMap = new HashMap<>();
//			Map<Long, HData> eMap = new HashMap<>();
//			Map<Long, HData> curveMap = new HashMap<>();
//			String point = "";
//			for (DataPoints data : dataSets) {
//				point = data.metricName();
//				String key = data.getTags().get("QoD");
//				SeekableView view = data.iterator();
//				while (view.hasNext()) {
//					HData d = new HData();
//					DataPoint dp = view.next();
//					if (dp.timestamp() <= endT) {
//						d.setDataDate(dp.timestamp());
//						String value = (dp.isInteger() ? dp.longValue() : dp.doubleValue()) + "";
//						d.setDataValue(value);
//						long dt = dp.timestamp();
//						dt = dt-dt%1000;
//						if ("I".equals(key)) {
//							iMap.put(dt, d);
//						} else if ("C".equals(key)) {
//							cMap.put(dt, d);
//						} else if ("S".equals(key)) {
//							sMap.put(dt, d);
//						} else if ("E".equals(key)) {
//							eMap.put(dt, d);
//						} else {
//							curveMap.put(dt, d);
//						}
//					}
//				}
//			}
//			for (Map.Entry<Long, HData> entry : eMap.entrySet()) {
//				curveMap.put(entry.getKey(), entry.getValue());
//			}
//			for (Map.Entry<Long, HData> entry : sMap.entrySet()) {
//				curveMap.put(entry.getKey(), entry.getValue());
//			}
//			for (Map.Entry<Long, HData> entry : iMap.entrySet()) {
//				curveMap.put(entry.getKey(), entry.getValue());
//			}
//			for (Map.Entry<Long, HData> entry : cMap.entrySet()) {
//				curveMap.put(entry.getKey(), entry.getValue());
//			}
//			for (Map.Entry<Long, HData> entry : curveMap.entrySet()) {
//				datas.add(entry.getValue());
//			}
//			Collections.sort(datas);
//			map.put(point, datas);
//		}
//
//		for(Map.Entry<String, List<HData>> dataEntry: map.entrySet()){
//			if (dataEntry.getKey().equals("")||dataEntry.getKey() == null){
//				continue;
//			}
//			String mpointId = collect.get(dataEntry.getKey());
//			BaseData data = new BaseData();
//			List<HData> value = dataEntry.getValue();
//			value.sort(new Comparator<HData>() {
//				@Override
//				public int compare(HData o1, HData o2) {
//					return o1.getDataDate()>o2.getDataDate()?-1:1;
//				}
//			});
//			data.setMpointIds(Long.valueOf(mpointId));
//			if (value == null ||value.size()<=0){
//			}else {
//				data.setValue(Double.valueOf(value.get(0).getDataValue()));
//			}
//			result.add(data);
//		}
//		return result;
//	}




}
