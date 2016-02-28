package com.sunny.server;

/**
 * 
 * 双工服务端都通过该类进行启动
 * 
 * Create on 2013-6-21 上午9:59:59
 * 
 * @author <a href="mailto:zhouyan@malangmedia.com">ZhouYan</a>
 * 
 */
public abstract class TCPDuplexServer {

	/**
	 * 启动服务
	 */
	public abstract void server(int port);

	/**
	 * 是否已经服务
	 * 
	 * @return
	 */
	public abstract boolean isServing();

}
