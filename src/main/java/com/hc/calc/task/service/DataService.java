package com.hc.calc.task.service;

import com.hc.calc.task.model.BaseData;
import com.hc.calc.task.model.ComputingUnit;
import com.hc.calc.task.model.ExecuteRes;
import org.opentsdb.client.bean.request.SubQuery;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Holger
 * @date 2018/4/26
 */
public interface DataService {

    /**
     * 求和
     *
     * @param id         测点id
     * @param metric     OpenTSDB 测点标志
     * @param datasource 测点数据类型
     * @param start      测点开始时间
     * @param end        测点结束时间
     * @return double
     * @throws Exception
     */
    Double sum(long id, String metric, String datasource, Date start, Date end) throws Exception;

    /**
     * 平均值
     *
     * @param id         测点id
     * @param metric     OpenTSDB 测点标志
     * @param datasource 测点数据类型
     * @param start      测点开始时间
     * @param end        测点结束时间
     * @return double
     * @throws Exception
     */
    Double avg(long id, String metric, String datasource, Date start, Date end) throws Exception;

    /**
     * 最大值
     *
     * @param id         测点id
     * @param metric     OpenTSDB 测点标志
     * @param datasource 测点数据类型
     * @param start      测点开始时间
     * @param end        测点结束时间
     * @return double
     * @throws Exception
     */
    Double max(long id, String metric, String datasource, Date start, Date end) throws Exception;

    /**
     * 最小值
     *
     * @param id         测点id
     * @param metric     OpenTSDB 测点标志
     * @param datasource 测点数据类型
     * @param start      测点开始时间
     * @param end        测点结束时间
     * @return double
     * @throws Exception
     */
    Double min(long id, String metric, String datasource, Date start, Date end) throws Exception;

    /**
     * 取前值
     *
     * @param id         测点id
     * @param metric     OpenTSDB 测点标志
     * @param datasource 测点数据类型
     * @param start      测点开始时间
     * @return double
     * @throws Exception
     */
    Double prev(long id, String metric, String datasource, Date start) throws Exception;

    /**
     * 获取数据列表
     *
     * @param id         测点id
     * @param metric     OpenTSDB 测点标志
     * @param datasource 测点数据类型
     * @param start      测点开始时间
     * @param end        测点结束时间
     * @return 数据list
     * @throws Exception
     */
    List<BaseData> getData(long id, String metric, String datasource, Date start, Date end) throws Exception;

    /**
     * 写入数据
     *
     * @param res  执行结果
     * @param unit 计算单元
     * @return 更新行数
     */
    int addData(ExecuteRes res, ComputingUnit unit);

    /**
     * 取count
     *
     * @param id         测点id
     * @param metric     OpenTSDB 测点标志
     * @param datasource 测点数据类型
     * @param start      测点开始时间
     * @param end        测点结束时间
     * @return double
     * @throws Exception
     */
    Long count(Long id, String metric, String datasource, Date start, Date end) throws Exception;

    /**
     * 获取平均数据
     * 
     * @param id         测点id
     * @param point      OpenTSDB 测点标志
     * @param downSample 数据分组
     * @param datasource 测点数据类型
     * @param start      测点开始时间
     * @param end      测点结束时间
     * @return
     * @throws Exception
     */
    List<BaseData> getAvgData(Long id, String point, String downSample, String datasource, Date start, Date end)
		    throws Exception;
    /**
     * 获取给定时间范围内的所有数据
     *
     * @param id         测点id
     * @param metric     OpenTSDB 测点标志
     * @param datasource 测点数据类型
     * @param start      测点开始时间
     * @param end        测点结束时间
     * @return 数据list
     * @throws Exception
     */
    List<BaseData> getAllDatas(long id, String metric, String datasource, Date start, Date end) throws Exception;

    public List<BaseData> prev(List<Long> notAutoMpointIds, Date startDT);

    public List<BaseData> getDatas(String startTime, String endTime, ArrayList<SubQuery> queryList,
                                   Map<String, String> collect) throws ParseException;

    List<BaseData> getAllDatasNew(long id, String metric, String datasource, Long start, Long end) throws Exception;


}
