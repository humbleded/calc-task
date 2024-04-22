package com.hc.calc.task.zookeeper;

import com.hc.calc.task.config.BaseConfig;
import com.hc.calc.task.excption.ClientIsExistException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * @author Holger
 * @date 2018/4/26
 */
@Component("zkClient")
public class ZookeeperClient implements Watcher {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private CuratorFramework zclient;

    private CountDownLatch latch = new CountDownLatch(1);

    public ZookeeperClient() throws Exception {
	// 第一个时间为超时时间，第二个时间为心跳同步时间
	zclient = CuratorFrameworkFactory.newClient(BaseConfig.ZOOKEEPER_URL,
		30000, 5000, new RetryNTimes(10000, 5000));
	// 连接
	zclient.start();
	//root节点判断
	if (zclient.checkExists().forPath(BaseConfig.ROOT_PATH) == null) {
	    zclient.create().forPath(BaseConfig.ROOT_PATH);
	}
	// 所有的计算节点的父节点
	if (zclient.checkExists().forPath(BaseConfig.CLIENT_PATH) == null) {
	    zclient.create().forPath(BaseConfig.CLIENT_PATH);
	}
	// 创建当前计算节点的临时节点
	String path = BaseConfig.CLIENT_PATH + "/" + "client_"
		+ BaseConfig.CLIENT_ID;
	Stat stat = zclient.checkExists().forPath(path);
	if (stat == null) {
	    zclient.create().withMode(CreateMode.EPHEMERAL).forPath(path,
		    ("client_" + BaseConfig.CLIENT_ID).getBytes());
	} else {
	    throw new ClientIsExistException("The client id client_"
		    + BaseConfig.CLIENT_ID + "is exist");
	}
	zclient.getConnectionStateListenable().addListener(new ClientWatcher());
	// 创建当前节点的节点监听事件
	final NodeCache watcher = new NodeCache(zclient, path);
	watcher.getListenable().addListener(new NodeCacheListener() {
	    @Override
	    public void nodeChanged() throws Exception {
			if (watcher.getCurrentData() == null) {
				logger.info("the id is {} of the client Down line",
						BaseConfig.CLIENT_ID);
				BaseConfig.CLIENT_STATUS = true;
			}
		}
	});
	watcher.start();
    }

    public CuratorFramework getZclient() {
	return zclient;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
	if (watchedEvent.getState() == Event.KeeperState.Disconnected) {
	    System.out.println();
	}
    }
}
