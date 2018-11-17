package com.demo.netty.c4粘包拆包.解决问题;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class TimeServer {

	public void bind(int port) throws Exception{
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try{
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024).childHandler(new ChildChannelHandler());
			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		}finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}

	}

	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel arg0) throws Exception {
			//新增加了两个解码器：LineBasedFrameDecoder，StringDecoder。
			//LineBasedFrameDecoder工作原理是它依次遍历ByteBuf中的可读字节，判断是否有\n或\r\n，如果有就以此位置为结束位置，从可读索引到
			//结束位置区间的字节就组成了一行。它是以换行符为结束标志的解码器，支持携带结束符或者不携带结束符两种解码方式，同时支持配置单行的最大长度。
			//如果连续读取到最大长度后仍然没有发现换行符，就抛出异常，同时忽略掉之前读取到的异常码流。
			//StringDecoder将接收到的对象转换为字符串，然后继续调用后面的Handler。
			//LineBasedFrameDecoder+StringDecoder组合就是按行切换的文本解码器，他被设计用来支持粘包和拆包。
			arg0.pipeline().addLast(new LineBasedFrameDecoder(1024));
			arg0.pipeline().addLast(new StringDecoder());
			arg0.pipeline().addLast(new TimeServerHandler());
		}

	}

	public static void main(String[] args) throws Exception {
		int port = 8080;
		new TimeServer().bind(port);
	}

}
