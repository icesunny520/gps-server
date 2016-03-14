package com.sunny.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

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
public class RpcProtoDecode extends LengthFieldBasedFrameDecoder {
	/**
	 * @param maxFrameLength
	 * @param lengthFieldOffset
	 * @param lengthFieldLength
	 * @param lengthAdjustment
	 * @param initialBytesToStrip
	 */
	public RpcProtoDecode(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
		// TODO Auto-generated constructor stub
	}

	Logger logger = LoggerFactory.getLogger(getClass());

	// public RpcProtoDecode(int frameLength) {
	// super(frameLength);
	// }

	public void printHexString() {
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		ByteBuf revBuf = (ByteBuf) super.decode(ctx, in);// 处理拆包粘包后的完整byteBuf
		if (revBuf == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer();
		while (revBuf.isReadable()) {
			sb.append(bytesToHexString(new byte[] { revBuf.readByte() }) + " ");
		}
		logger.info(sb.toString());
		revBuf.readerIndex(0);

		byte[] headerData = new byte[4];
		revBuf.readBytes(headerData);
		String header = bytesToHexString(headerData);
		// if (logger.isInfoEnabled())
		// logger.info("begin 4 byte " + header);
		if (header.equals("29298000")) {
			GeoModel model = null;
			byte bodyLength = revBuf.readByte();
			int bl = bodyLength & 0xff;
			// if (logger.isInfoEnabled())
			// logger.info("body length " + tt + " total length " + revBuf.readableBytes() + " " + bytesToHexString(new byte[] { bodyLength }));
			// if (bodyLength < 45) {
			byte[] bodyData = new byte[bl];
			revBuf.readBytes(bodyData);
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
				// logger.info(t + "");
				if (t > 128) {
					vData[i] = t - 128;
					bin += "1";
				} else {
					vData[i] = t;
					bin += "0";
				}

			}
			// logger.info("bin = " + bin);
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
			model.setLatitude(getDo(latitudeStr,true));
			// 经度
			byte[] longitudeData = new byte[4];
			body.get(longitudeData);
			String longitudeStr = bcd2string(longitudeData);
			model.setLongitude(getDo(longitudeStr,false));
			// logger.info(id + " " + bytesToHexString(longitudeData) + "," + bytesToHexString(latitudeData));
			// logger.info(id + " " + longitudeStr + "," + latitudeStr);
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

	private String getDo(String str, boolean lat) {
		if (lat) {
			String n1 = str.substring(0, 3);// 截取2位，纬度共2位，最多90度
			String n2 = str.substring(3, 5) + "." + str.substring(5);

			double latresult = Double.parseDouble(n1);
			latresult += Double.parseDouble(n2) / 60.0D;
			String temp = String.valueOf(latresult);
			if (temp.length() > 8) {
				temp = n1 + temp.substring(temp.indexOf("."), 8);
			}
//			logger.info(temp);
			return temp;
		} else {
			String e1 = str.substring(0, 3);// 截取3位数字，经度共3位，最多180度
			// 经度是一伦敦为点作南北两极的线为0度,所有往西和往东各180度
			String e2 = str.substring(3, 5) + "." + str.substring(5);// 需要运算的小数

			double result = Double.parseDouble(e1);
			result += (Double.parseDouble(e2) / 60.0D);
			String temp = String.valueOf(result);
			if (temp.length() > 9) {
				temp = e1 + temp.substring(temp.indexOf("."), 9);
			}
//			logger.info(temp);
			return temp;
		}
	}

	public String bcd2string(byte[] bytes) {
		// StringBuffer sb = new StringBuffer();
		// for (int i = 0; i < b.length; i++) {
		// int h = ((b[i] & 0xff) >> 4) + 48;
		// sb.append((char) h);
		// int l = (b[i] & 0x0f) + 48;
		// sb.append((char) l);
		// }
		// return sb.toString();

		StringBuffer temp = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
			temp.append((byte) (bytes[i] & 0x0f));
		}
		return temp.toString();
	}

	public static int bytes2Int(byte[] byteNum) {
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
