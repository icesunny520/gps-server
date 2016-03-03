package com.sunny.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.FixedLengthFrameDecoder;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 解码消息包
 *
 * Create on Jul 7, 2015 12:11:21 PM
 *
 * @author TonyZhou
 *
 */
public class RpcProtoDecode extends FixedLengthFrameDecoder {
	Logger logger = LoggerFactory.getLogger(getClass());

	public RpcProtoDecode(int frameLength) {
		super(frameLength);
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		ByteBuf revBuf = (ByteBuf) super.decode(ctx, in);// 处理拆包粘包后的完整byteBuf
		if (revBuf == null) {
			return null;
		}

		byte[] headerData = new byte[4];
		revBuf.readBytes(headerData);
		String header = bytesToHexString(headerData);
		if (logger.isInfoEnabled())
			logger.info("begin 4 byte " + header);
		if (header.equals("29298000")) {
			GeoModel model = null;
			byte bodyLength = revBuf.readByte();
			// if (logger.isInfoEnabled())
			// logger.info("body length " + bodyLength);
			bodyLength = 40;
			// if (bodyLength < 45) {
			byte[] bodyData = new byte[bodyLength];
			revBuf.readBytes(bodyData);
			int v = bodyData[bodyLength - 1] & 0xFF;
			String hv = Integer.toHexString(v);
			// if (logger.isInfoEnabled())
			// logger.info("end " + hv);
			// if (hv.equals("d")) {
			model = new GeoModel();
			ByteBuffer body = ByteBuffer.wrap(bodyData);
			// id,无符号
			byte[] idData = new byte[4];
			int[] vData = new int[4];
			body.get(idData);
			String bin = "";
			String numHeader = "";
			for (int i = 0; i < 4; i++) {
				int t = idData[i] & 0xff;
				logger.info(t + "");
				if (t > 128) {
					vData[i] = t - 128;
					bin += "1";
				} else {
					vData[i] = t;
					bin += "0";
				}

			}
			logger.info("bin = " + bin);
			if (bin.equals("1100")) {
				numHeader = "158";
			} else if (bin.equals("1101"))
				numHeader = "159";
			else {
				BigInteger src1 = new BigInteger(bin, 2);
				int hn = Integer.valueOf(src1.toString()) + 30;
				numHeader = "1" + hn;
			}
			String id = numHeader + (vData[0] == 0 ? "00" : vData[0]) + (vData[1] == 0 ? "00" : vData[1]) + (vData[2] == 0 ? "00" : vData[2]) + (vData[3] == 0 ? "00" : vData[3]);

			if (logger.isInfoEnabled()) {
				logger.info("id " + id);
			}
			model.setId(Long.valueOf(id));
			// 年月日时分秒
			byte[] timeData = new byte[6];
			body.get(timeData);
			String dateStr = bcd2string(timeData);
			Calendar calendar = Calendar.getInstance();
			calendar.set(Integer.valueOf("20" + dateStr.substring(0, 2)), Integer.valueOf(dateStr.substring(2, 4)) - 1, Integer.valueOf(dateStr.substring(4, 6)), Integer.valueOf(dateStr.substring(6, 8)), Integer.valueOf(dateStr.substring(8, 10)),
					Integer.valueOf(dateStr.substring(10, 12)));
			model.setTimestamp(calendar.getTimeInMillis());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
			model.setDateTime(format.format(calendar.getTimeInMillis()));
			// 纬度
			byte[] latitudeData = new byte[4];
			body.get(latitudeData);
			String latitudeStr = bcd2string(latitudeData);
			model.setLatitude(Float.valueOf(latitudeStr.substring(0, 3) + "." + latitudeStr.substring(3)));
			// 经度
			byte[] longitudeData = new byte[4];
			body.get(longitudeData);
			String longitudeStr = bcd2string(longitudeData);
			model.setLongitude(Float.valueOf(longitudeStr.substring(0, 3) + "." + longitudeStr.substring(3)));
			// 速度
			byte[] speed = new byte[2];
			body.get(speed);
			model.setHourSpeed(Integer.valueOf(bcd2string(speed)));
			// 方向
			byte[] direction = new byte[2];
			body.get(direction);
			model.setDirection(Integer.valueOf(bcd2string(direction)));
			// }
			// }
			return model;
		}
		return null;

	}

	public String bcd2string(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			int h = ((b[i] & 0xff) >> 4) + 48;
			sb.append((char) h);
			int l = (b[i] & 0x0f) + 48;
			sb.append((char) l);
		}
		return sb.toString();
	}

	public int bytes2Int(byte[] byteNum) {
		int num = 0;
		for (int ix = 0; ix < 4; ++ix) {
			num <<= 8;
			num |= (byteNum[ix] & 0xff);
		}
		return num;
	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

}
