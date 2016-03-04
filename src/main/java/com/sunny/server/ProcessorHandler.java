package com.sunny.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunny.database.DBConnectionManager;
import com.sunny.database.Database;

/**
 * 
 * 处理rpc服务端请求
 *
 * Create on Jul 7, 2015 2:10:16 PM
 *
 * @author TonyZhou
 *
 */
public class ProcessorHandler extends ChannelInboundHandlerAdapter {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private Database db;

	public ProcessorHandler() throws FileNotFoundException, IOException, SQLException {
		super();
		Properties conf = new Properties();
		conf.loadFromXML(new FileInputStream("./conf/CMSDB.xml"));
		db = DBConnectionManager.getInstance().getDatabase(conf);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// if (logger.isInfoEnabled()) {
		// logger.info("accept a new connection.");
		// }
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		GeoModel model = (GeoModel) msg;
		if (model == null)
			return;
		db.executeUpdate("REPLACE INTO bus_geo (bus,longitude,latitude,direction,hourSpeed,dateTime,timestamp) values (?,?,?,?,?,?,?)", model.getId(), model.getLongitude(), model.getLatitude(), model.getDirection(), model.getHourSpeed(), model.getDateTime(), model.getTimestamp());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (ctx.channel() != null)
			logger.error(ctx.channel().toString() + " ", cause);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		// logger.info("disconnected...");
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		// logger.info("connected...");
	}
}
