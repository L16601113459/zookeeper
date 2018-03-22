package com.atguigu.test;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Description: 1 初始化ZK的多个操作 1.1 建立ZK的链接 1.2 创建/atguigu节点并赋值 1.3 获得该节点的值
 * 
 *               2 watchmore 2.1
 *               获得值之后设置一个观察者watcher，如果/atguigu该节点的值发生了变化，要求通知Client端，一次性通知
 * 
 *               3 watchMore 3.1
 *               获得值之后设置一个观察者watcher，如果/atguigu该节点的值发生了变化，要求通知Client端,继续观察 3.2
 *               又再次获得新的值的同时再新设置一个观察者，继续观察并获得值 3.3
 *               又再次获得新的值的同时再新设置一个观察者，继续观察并获得值.。。。。。重复上述过程
 * @author zzyy
 * @date 2018年3月21日
 */
public class WatchMore extends BaseConfig {
	private static final Logger logger = Logger.getLogger(WatchMore.class);

	/**
	 * 
	 * 狗富贵, 互相旺 For 1018
	 * 
	 * @Title: startZK @Description:
	 * 获得ZK的session连接对象实例 @param @return @param @throws IOException 参数 @return
	 * ZooKeeper 返回类型 @throws
	 */
	public ZooKeeper startZK() throws IOException {
		return new ZooKeeper(CONNECTION_STRING, SESSION_TIMEOUT, new Watcher() {
			@Override
			public void process(WatchedEvent event) {

			}
		});
	}

	/**
	 * 
	 * @Title: createZnode @Description: 再给定的路径下创建znode节点并赋值 @param @param
	 * zk @param @param path @param @param data @param @throws
	 * KeeperException @param @throws InterruptedException 参数 @return void
	 * 返回类型 @throws
	 */
	public void createZnode(String path, String data) throws KeeperException, InterruptedException {
		zk.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}

	/**
	 * 
	 * @Title: getZnode @Description: 获取我们对应节点的值 @param @param zk @param @param
	 * path @param @return @param @throws KeeperException @param @throws
	 * InterruptedException 参数 @return String 返回类型 @throws
	 */
	public String getZnode(String path) throws KeeperException, InterruptedException {
		String result = "";

		byte[] byteArray = zk.getData(path, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				try {
					triggerValue(path);
				} catch (KeeperException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, new Stat());
		result = new String(byteArray);
		oldValue = result;

		return result;
	}

	public boolean triggerValue(String path) throws KeeperException, InterruptedException {
		String result = "";

		byte[] byteArray = zk.getData(path, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				try {
					triggerValue(path);
				} catch (KeeperException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, new Stat());
		result = new String(byteArray);
		newValue = result;

		if (newValue.equals(oldValue)) {
			logger.info("*********The value no changes*******");
			return false;
		} else {
			logger.info("********oldValue: " + oldValue + "\t newValue: " + newValue);
			oldValue = newValue;
			return true;
		}
	}

	public static void main(String[] args) throws Exception {
		WatchMore watchOne = new WatchMore();
		watchOne.setZk(watchOne.startZK());

		if (watchOne.getZk().exists(PATH, false) == null) {
			watchOne.createZnode(PATH, "AAA");

			String result = watchOne.getZnode(PATH);// AAA

			if (logger.isInfoEnabled()) {
				logger.info("main(String[]) --------init String result=" + result);
			}
		} else {
			logger.info("this node has already exists!!!!");
		}

		Thread.sleep(Long.MAX_VALUE);
	}

}
