package com.sunny.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

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
	private int check = 300;
	private int timeout = 1000 * 60 * 60 * 3;
	private AtomicInteger count = new AtomicInteger();

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
		if (count.incrementAndGet() % check == 0) {
			int r = db.executeUpdate("delete from bus_geo where " + System.currentTimeMillis() + " - bus_geo.`timestamp` > " + timeout);
			logger.info("delete " + r + " number of rows");
		}
		GeoModel model = (GeoModel) msg;
		if (model == null || Float.valueOf(model.getLongitude()) == 0 || Float.valueOf(model.getLatitude()) == 0)
			return;

		double[] rs = new double[2];
		GpsCorrect.transform(Double.valueOf(model.getLatitude()), Double.valueOf(model.getLongitude()), rs);
		if (rs[0] != 0 && rs[1] != 0) {
			model.setLatitude(rs[0] + "");
			model.setLongitude(rs[1] + "");
		}
		db.executeUpdate("REPLACE INTO bus_geo (bus,longitude,latitude,direction,hourSpeed,dateTime,timestamp) values (?,?,?,?,?,?,?)", model.getId(), model.getLongitude(), model.getLatitude(), model.getDirection(), model.getHourSpeed(), model.getDateTime(), model.getTimestamp());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// if (ctx.channel() != null)
		// logger.error(ctx.channel().toString() + " ", cause);
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
