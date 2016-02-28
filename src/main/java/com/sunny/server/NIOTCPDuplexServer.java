/**
 * 
 */
package com.sunny.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * NIO模式双工客户端
 * 
 * Create on 2013-8-29 上午10:01:20
 * 
 * @author <a href="mailto:xiaxiayoyo@gmail.com">ZhouYan</a>.
 * 
 */
public class NIOTCPDuplexServer extends TCPDuplexServer {

	private Logger logger = LoggerFactory.getLogger(NIOTCPDuplexServer.class);
	/**
	 * 连接通道
	 */
	private Channel channel;
	/**
	 * io线程池
	 */
	private EventLoopGroup bossGroup;
	/**
	 * 工作线程池
	 */
	private EventLoopGroup workerGroup;

	/**
	 * 存储NettyRpcDuplexServer的的key
	 */
	public static final AttributeKey<NIOTCPDuplexServer> SERVER = AttributeKey.valueOf("NIORpcDuplexServer");

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sunny.rpc.tcp.server.duplex.RpcDuplexServer#server(int)
	 */
	@Override
	public void server(int port) {
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup(200);
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.childAttr(SERVER, this);
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline p = ch.pipeline();
					// 编码
					// p.addLast("RequsetEncode", new RpcProtoEncode());
					// 处理拆包，粘包，并解码
					p.addLast("ResponseDecode", new RpcProtoDecode(45));
					// 指定时间内没有发生任何读操作（包括心跳），则判定连接超时，关闭连接
					p.addLast("ReadTimeoutHandler", new ReadTimeoutHandler(600, TimeUnit.SECONDS));
					p.addLast("ServerHandler", new ProcessorHandler());
				}
			});

			b.option(ChannelOption.TCP_NODELAY, true);
			b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
			b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

			// Bind and start to accept incoming connections.
			channel = b.bind(port).sync().channel();
			logger.info("started server on " + port);
		} catch (InterruptedException e) {
			logger.error("", e);
		} finally {
			Thread t = new Thread(new Runnable() {

				public void run() {
					try {
						channel.closeFuture().sync();
					} catch (InterruptedException e) {

					} finally {
						logger.info("close server channel.");
						bossGroup.shutdownGracefully();
						workerGroup.shutdownGracefully();
					}
				}
			});
			t.setName("ServerFutureCloseListenerThread");
			t.start();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sunny.rpc.tcp.server.duplex.RpcDuplexServer#isServing()
	 */
	@Override
	public boolean isServing() {
		if (channel != null)
			return channel.isActive();
		else
			return false;
	}

}
