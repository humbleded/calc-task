package com.hc.calc.task.kafka;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.stereotype.Component;

import com.hc.calc.task.config.BaseConfig;

/**
 * kafka生产正配置
 * @author zf
 * @date 2020-03-13
 */
@Component
public class KafkaProducerConfig {

	private Producer<String, String> producer;
	
	public KafkaProducerConfig(){
		loadKafkaProducerConfig();
	}
	
	private void loadKafkaProducerConfig(){
		Properties properties = new Properties();
		properties.put("acks", BaseConfig.acks);
		properties.put("bootstrap.servers", BaseConfig.bootstrapServers);
		properties.put("serializer.class", StringEncoder.class.getName());
		properties.put("retries", BaseConfig.retries);	
		properties.put("linger.ms", 0);
		properties.put("batch.size", 1);
		properties.put("metadata.broker.list", BaseConfig.KAFKA_URL);
		producer = new Producer<String, String>(new ProducerConfig(properties));
	}
	
	public Producer<String, String> getKafkaProducer(){
		return this.producer;
	}
}
