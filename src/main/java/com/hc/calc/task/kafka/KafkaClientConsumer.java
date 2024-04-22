package com.hc.calc.task.kafka;

import com.hc.calc.task.config.BaseConfig;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ConsumerConnector;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @author Holger
 * @date 2018/4/26
 */
@Component
public class KafkaClientConsumer {

    private ConsumerConnector consumer;

    public KafkaClientConsumer() {
        Properties properties = new Properties();
        properties.put("zookeeper.connect", BaseConfig.ZOOKEEPER_URL);
        properties.put("zookeeper.session.timeout.ms", "50000");
        properties.put("zookeeper.connection.timeout.ms", "100000");
        properties.put("metadata.broker.list", BaseConfig.KAFKA_URL);
        properties.put("rebalance.backoff.ms", "2000");
        properties.put("rebalance.max.retries", "10");
        properties.put("group.id", "client_" + BaseConfig.GROUP_ID);
        properties.put("auto.offset.reset", "largest");
        properties.put("enable.auto.commit", "false");
        properties.put("max.poll.records", 1);
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));
    }

    public ConsumerConnector getConsumer() {
        return this.consumer;
    }

}
