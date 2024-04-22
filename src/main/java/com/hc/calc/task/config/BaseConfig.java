package com.hc.calc.task.config;


/**
 * @author Holger
 * @date 2018/4/19
 */
public class BaseConfig {
    /**
     * zookeeper root节点地址
     */
    public static final String ROOT_PATH = "/calc";

    public static final String TASK_PATH = ROOT_PATH + "/task";

    /**
     * zookeeper 计算节点父节点
     */

    public static final String CLIENT_PATH = ROOT_PATH + "/client";

    /**
     * 任务存储的kafka队列
     */
    public static String TASK_TOPIC;

    /**
     * 存储Task topic 的分区数,默认只有一个分区
     */
    public static int TASK_PART = 1;

    /**
     * kafka服务地址
     */
    public static String KAFKA_URL;

    /**
     * zookeeper服务地址
     */
    public static String ZOOKEEPER_URL;

    /**
     * 数据延迟
     */
    public static long DELAY = 30 * 60 * 1000;

    /**
     * zookeeper 当前轮的任务数据剩余量
     */
    public static final String TASK_NUM_PATH = ROOT_PATH + "/tnum";

    /**
     * zookeeper 当前轮的任务数据剩余量
     */
    public static final String ROLL_NUM_PATH = ROOT_PATH + "/rnum";

    /**
     * 计算节点的编号
     */
    public static int CLIENT_ID;
    /**
     * 客户端分组
     */
    public static String GROUP_ID;

    /**
     * 当前client的状态，true为正常启动，false为失败，则关闭退出节点
     */

    public static boolean CLIENT_STATUS = true;

    /**
     * 当前节点的线程数量
     */
    public static int THREAD_NUM = 1;

    /**
     * 取前点跨度时间
     */
    public static int PREV_SPAN = 30 * 60 * 1000;

    /**
     * 默认只查询一条的配置
     */
    public final static String DOWNSAMPLING = "0all";

    public static double MISSING =0.5;

    public static int SAMPLING = 5;

    public static int CYCLE = 70;
    /**
     * 计算任务差值误差
     */
    public static double T10DEVIATION = 5;
    
    public static String HBASE_ZK_QUORUM;
    
    public static String acks;
    public static String bootstrapServers;
    public static String keySerializer;
    public static String valueSerializer;
    public static String recalcDataTopic;
    public static String retries;
    public static String linger;
}
