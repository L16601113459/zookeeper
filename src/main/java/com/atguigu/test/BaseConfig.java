package com.atguigu.test;

import org.apache.zookeeper.ZooKeeper;

import lombok.Getter;
import lombok.Setter;

public class BaseConfig {
	// 实例常量
	protected static final String CONNECTION_STRING = "192.168.198.128:2181";
	protected static final String PATH = "/atguig1u";
	protected static final int SESSION_TIMEOUT = 20 * 1000;
	// 实例变量
	protected @Setter @Getter ZooKeeper zk = null;

	protected @Setter @Getter String oldValue = "";
	protected @Setter @Getter String newValue = "";
}
