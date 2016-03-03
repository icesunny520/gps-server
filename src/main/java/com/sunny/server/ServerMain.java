/**
 * 
 */
package com.sunny.server;

import org.apache.log4j.PropertyConfigurator;

/**
 * 
 *
 * Create on Jan 29, 2016 9:40:54 AM
 *
 * @author TonyZhou
 * 
 */
public class ServerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configure("conf/log4j.properties");
		NIOTCPDuplexServer server = new NIOTCPDuplexServer();
		server.server(8890);
		
//		byte a = (byte)-118;
//		byte[] t = new byte[1];
//		t[0]=a;
//		System.out.println(RpcProtoDecode.bytesToHexString(t));
////		int i = a;    
//		int i = a & 0xff;
//		System.out.println(i);

	}

}
