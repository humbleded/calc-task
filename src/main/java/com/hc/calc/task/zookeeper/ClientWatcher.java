package com.hc.calc.task.zookeeper;

import com.hc.calc.task.config.BaseConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;

/**
 * @author Holger
 * @date 2018/4/28
 */
public class ClientWatcher implements ConnectionStateListener {

    @Override
    public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
        if (connectionState == ConnectionState.LOST) {
            BaseConfig.CLIENT_STATUS = true;
        }
    }
}
