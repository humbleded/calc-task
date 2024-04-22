package com.hc.calc.task.config;

import com.hc.calc.task.excption.ConfigNotExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * @author Holger
 * @date 2018/4/19
 */
@Configuration
@PropertySource("classpath:application.properties")
public class LoadCalcConfig {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Environment env;

    public LoadCalcConfig(Environment env) {
		try {
		    this.env = env;
		    logger.info("Initialize the read configuration");
		    loadConfig();
	
		} catch (Exception e) {
		    logger.error(e.getMessage(), e);
		    System.exit(0);
		}
		logger.debug("read properties success!");
    }

    public Environment getEnv(){
    	return env;
    }
    
    public boolean loadConfig() throws ConfigNotExistException {
		logger.debug("start read configuration");
		BaseConfig.TASK_TOPIC = env.getProperty("task.topic");
		if (BaseConfig.TASK_TOPIC == null || BaseConfig.TASK_TOPIC.isEmpty()) {
		    throw new ConfigNotExistException("task topic is not null");
		} else {
		    logger.debug("Task queue read successfully, value is {}",
			    BaseConfig.TASK_TOPIC);
		}
		logger.debug("Task queue partition value is {}", BaseConfig.TASK_PART);
		BaseConfig.KAFKA_URL = env.getProperty("kafka.url");
		if (BaseConfig.KAFKA_URL == null || BaseConfig.KAFKA_URL.isEmpty()) {
		    throw new ConfigNotExistException("kafka url cannot be null");
		} else {
		    logger.debug("kafka url read successfully, value is {}",
			    BaseConfig.KAFKA_URL);
		}
		BaseConfig.ZOOKEEPER_URL = env.getProperty("zookeeper.url");
		if (BaseConfig.ZOOKEEPER_URL == null
			|| BaseConfig.ZOOKEEPER_URL.isEmpty()) {
		    throw new ConfigNotExistException("zookeeper url cannot be null");
		} else {
		    logger.debug("zookeeper url read successfully, value is {}",
			    BaseConfig.ZOOKEEPER_URL);
		}
		if (env.getProperty("delay", Long.class) == null) {
		    logger.warn(
			    "No task delay configuration, use the default configuration");
		} else {
		    BaseConfig.DELAY = env.getProperty("delay", Long.class) * 60 * 1000;
		}
		if (env.getProperty("client.id", Integer.class) == null) {
		    throw new ConfigNotExistException("client.id cannot be null");
		} else {
		    BaseConfig.CLIENT_ID = env.getProperty("client.id", Integer.class);
		}
		if (env.getProperty("group.id", String.class) == null) {
		    throw new ConfigNotExistException("group.id cannot be null");
		} else {
		    BaseConfig.GROUP_ID = env.getProperty("group.id", String.class);
		}
		if (env.getProperty("thread.num", Integer.class) != null) {
		    BaseConfig.THREAD_NUM = env.getProperty("thread.num", Integer.class);
		}
		BaseConfig.HBASE_ZK_QUORUM = env.getProperty("hbase.zk.quorum");
		if (BaseConfig.HBASE_ZK_QUORUM == null || BaseConfig.HBASE_ZK_QUORUM.isEmpty()) {
		    throw new ConfigNotExistException("hbase url cannot be null");
		}
		BaseConfig.acks = env.getProperty("acks");
		BaseConfig.bootstrapServers = env.getProperty("bootstrap.servers");
		BaseConfig.keySerializer = env.getProperty("key.serializer");
		BaseConfig.valueSerializer = env.getProperty("value.serializer");
		BaseConfig.recalcDataTopic = env.getProperty("recalcDataTopic");
		BaseConfig.retries = env.getProperty("retries");
		BaseConfig.linger = env.getProperty("linger");
		return true;
    }
}
