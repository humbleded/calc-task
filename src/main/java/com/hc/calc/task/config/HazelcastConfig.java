package com.hc.calc.task.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientConnectionStrategyConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {

    @Value("${spring.application.name:calcTask}")
    private String instanceName;
    @Value("${application.hazelcast-groupname}")
    private String groupName;
    @Value("${application.hazelcast-instance-tcp-ip}")
    private String instanceTcpIp;

    @Bean
    public HazelcastInstance hazelcastInstance() {
        HazelcastInstance hz = HazelcastClient.getHazelcastClientByName(this.instanceName);
        if (hz != null) {
            return hz;
        } else {
            ClientConfig config = new ClientConfig();
            config.getGroupConfig().setName(this.groupName);
            config.setInstanceName(this.instanceName);
            config.getNetworkConfig().setSmartRouting(false);
            config.getNetworkConfig().addAddress(this.instanceTcpIp.split(","));
            config.getNetworkConfig().setConnectionAttemptLimit(5);
            config.getNetworkConfig().setConnectionAttemptPeriod(5000);
            config.getConnectionStrategyConfig()
                    .setReconnectMode(ClientConnectionStrategyConfig.ReconnectMode.ASYNC);
            config.getNearCacheConfigMap().put("default",initializeDefaultMapConfig());
            hz = HazelcastClient.newHazelcastClient(config);
            return hz;
        }
    }
    private NearCacheConfig initializeDefaultMapConfig() {
        NearCacheConfig mapConfig = new NearCacheConfig();
        mapConfig.getEvictionConfig().setEvictionPolicy(EvictionPolicy.LRU);
        return mapConfig;
    }
}
