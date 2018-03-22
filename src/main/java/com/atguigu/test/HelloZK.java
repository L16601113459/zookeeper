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
 * eclipse此处为Client端， CentOS为zookeeper的Server端 1.通过Java程序 ， 新建连接ZK 2.新建一个znode节点
 * /atguigu并设置为hello1018 3.获取当前节点的最新值 4.关闭连接
 *
 */
public class HelloZK extends BaseConfig {
	private static final Logger logger = Logger.getLogger(HelloZK.class);

	/**
	 * 通过Java程序新建一个链接
	 * 
	 * @return
	 * @throws IOException
	 */
	public ZooKeeper startZK() throws IOException {
		return new ZooKeeper(CONNECTION_STRING,SESSION_TIMEOUT, new Watcher() {
			public void process(WatchedEvent event) {
			}
		});
	}

	/**
	 * 新建一个Znode节点
	 * 
	 * @param zk
	 * @param path
	 * @param date
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void createZnode(String path, String date) throws KeeperException, InterruptedException {
		zk.create(path, date.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}

	/**
	 * 获取当前节点的最新值
	 * 
	 * @param zk
	 * @param path
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public String getZnode(String path) throws KeeperException, InterruptedException {
		String result = "";
		byte[] byteArray = zk.getData(path, false, new Stat());
		result = new String(byteArray);

		return result;
	}

	/**
	 * 关闭连接
	 * 
	 * @param zk
	 * @throws InterruptedException
	 */
	public void stopZK() throws InterruptedException {
		if (zk != null)
			zk.close();
	}

	public static void main(String[] args) throws Exception {
		HelloZK hello = new HelloZK();
		ZooKeeper zk = hello.startZK();

		if (zk.exists(PATH, false) == null) {
			hello.createZnode(PATH, "helloZnode");
			String result = hello.getZnode(PATH);

			if (logger.isInfoEnabled()) {
				logger.info("main(String[]) ------- String result=" + result);
			}
		} else {
			logger.info("This node is exists......");
		}

		hello.stopZK();
	}
}
