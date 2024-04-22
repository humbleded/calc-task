package com.hc.calc.task.thread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.hc.calc.task.service.CalcShiftService;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.hc.calc.task.config.BaseConfig;
import com.hc.calc.task.kafka.KafkaClientConsumer;
import com.hc.calc.task.service.DataService;

/**
 * @author Holger
 * @date 2018/4/27
 */
@Component
public class ThreadPool {

    @Autowired
    private KafkaClientConsumer clientConsumer;

    @Autowired
	CalcShiftService shiftService;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 启动线程池
     * 
     * @param dataService
     * @param factory
     */
    public void startPool(DataService dataService, BeanFactory factory) {
		int numThread = BaseConfig.THREAD_NUM;
		String topic = BaseConfig.TASK_TOPIC;
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>(16);
		topicCountMap.put(topic, numThread);
		logger.info("***********************The consumer thread count is: {}, {}", numThread, topicCountMap.get(topic));
		ConsumerConnector consumer = clientConsumer.getConsumer();
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
		ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("task-thread-%d").build();
		// Common Thread Pool
		ExecutorService executor = new ThreadPoolExecutor(numThread, numThread, 10L, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>(), namedThreadFactory,
			new ThreadPoolExecutor.AbortPolicy());
		for (final KafkaStream<byte[], byte[]> stream : streams) {
		    if (!BaseConfig.CLIENT_STATUS) {
				System.out.println("节点已经被强制退出");
				return;
		    }
		    executor.submit(new TaskThread(stream, dataService, factory, consumer,shiftService));
		}
    }
}
