package com.hc.calc.task.util;

import com.hc.calc.task.model.MpointDataSource;
import org.opentsdb.client.bean.request.SubQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class OpentsdbUtil {

    private static final Logger log = LoggerFactory.getLogger(OpentsdbUtil.class);


//    private static TSDB tsdb;
//
//    public static TSDB getTsdb(String url) throws IOException {
//        if (tsdb != null) {
//            return tsdb;
//        }
//        try {
//            Config config = new Config(System.getProperty("user.dir")+"/hbase.properties");
//
//            config.overrideConfig("tsd.network.port", "4242");
//            config.overrideConfig("tsd.http.staticroot", "./staticroot");
//            config.overrideConfig("tsd.http.cachedir", "./data/cachedir");
//            config.overrideConfig("tsd.core.auto_create_metrics", "true");
//            config.overrideConfig("tsd.storage.hbase.zk_quorum", url);
//            config.setFixDuplicates(true);
//            tsdb = new TSDB(config);
//        } catch (IOException e) {
//            log.error("get opentsdb error", e);
//            throw new IOException();
//        }
//        return tsdb;
//    }



    /**
     *
     * @param point      测点点位
     * @param tag        标签
     * @param datype     测点数据类型
     * @param downsample 采样器
     * @return
     */
    public static SubQuery getSubQuery(String point, String tag, String datype, String downsample){
        // 创建一个filter的集合
        List<SubQuery.Filter> filters = new ArrayList<>();
        SubQuery.Filter filter = SubQuery.Filter.filter(SubQuery.Filter.FilterType.LITERAL_OR, "QoD", tag, true).build();
        filters.add(filter);
        SubQuery subQuery = SubQuery.metric(point)
                                .aggregator(SubQuery.Aggregator.NONE)
                                .filter(filters)
                                .build();
        if (!MpointDataSource.STATE.equals(datype) && !downsample.equals("none")){
            subQuery.setDownsample(downsample + "ep50r7");
        }
        return subQuery;
    }

//    /**
//     *
//     * @param point       测点点位
//     * @param tag         标签
//     * @param datype      测点数据类型
//     * @param downsample  采样器
//     * @return
//     */
//    public static TSSubQuery getQuery(String point, String tag, String datype, String downsample) {
//        TSSubQuery subQuery = new TSSubQuery();
//        subQuery.setMetric(point);
//        List<TagVFilter> filters = new ArrayList<>(1);
//        filters.add((new TagVFilter.Builder()).setType("literal_or").setFilter(tag).setTagk("QoD").setGroupBy(true).build());
//        subQuery.setFilters(filters);
//        if (MpointDataSource.STATE.equals(datype)) {
//            // 状态量
//            subQuery.setAggregator("none");
//        } else {
//            // 模拟量
//            subQuery.setAggregator("none");
//            if (!"none".equals(downsample)) {
//                subQuery.setDownsample(downsample + "-ep50r7");
//            }
//        }
//        return subQuery;
//    }
}
