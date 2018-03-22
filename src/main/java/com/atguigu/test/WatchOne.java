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

/**
 * 
 * @Description: 1 初始化ZK的多个操作 1.1 建立ZK的链接 1.2 创建/atguigu节点并赋值 1.3 获得该节点的值
 * 
 *               2 watch 2.1
 *               获得值之后(getZnode方法被调用后)设置一个观察者watcher，如果/atguigu该节点的值发生了变化，(A-->B)
 *               要求通知Client端eclipse，一次性通知
 * @author zzyy
 * @date 2018年3月21日
 */
public class WatchOne extends BaseConfig {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(WatchOne.class);

	/**
	 * @throws IOException @Title: startZK @Description: 获得ZK的连接实例 @param @return
	 * 参数 @return ZooKeeper 返回类型 @throws
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
	 * @Title: createZnode @Description: 创建一个节点并赋值 @param @param zk @param @param
	 * path @param @param data @param @throws KeeperException @param @throws
	 * InterruptedException 参数 @return void 返回类型 @throws
	 */
	public void createZnode(String path, String data) throws KeeperException, InterruptedException {
		zk.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}

	/**
	 * 
	 * @Title: getZnode @Description: 获得节点的值 @param @param zk @param @param
	 * path @param @return @param @throws KeeperException @param @throws
	 * InterruptedException 参数 @return String 返回类型 @throws
	 */
	public String getZnode(String path) throws KeeperException, InterruptedException {
		String result = "";

		byte[] byteArray = zk.getData(path, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				// 业务逻辑，也即出发了/atguig节点的变更后，我需要立刻获得最新值
				// 将本部分的业务逻辑提出来，新变成一个方法
				try {
					triggerValue(path);
				} catch (KeeperException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, new Stat());
		result = new String(byteArray);

		return result;
	}

	public void triggerValue(String path) throws KeeperException, InterruptedException {
		String result = "";

		byte[] byteArray = zk.getData(path, null, new Stat());
		result = new String(byteArray);

		logger.info("*********watcher after triggerValue result : " + result);

	}

	public static void main(String[] args) throws Exception {
		WatchOne one = new WatchOne();
		one.setZk(one.startZK());

		if (one.getZk().exists(PATH, false) == null) {
			one.createZnode(PATH, "AAA");

			String result = one.getZnode(PATH);

			logger.info("*********main init result : " + result);
		} else {
			logger.info("This node is exists......");
		}

		Thread.sleep(Long.MAX_VALUE);

	}
}
