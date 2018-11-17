package com.demo.netty.c5分隔符和定长解码器.分隔符;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class EchoServer {

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
			//以$_作为分隔符
			ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
			//1024是指单条消息的最大长度，当达到最大长度后仍然没有查找到分隔符，就抛出TooLongFrameException，防止由于异常码流缺失分隔符导致的内存溢出，这是netty解码器的可靠性保护
			arg0.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
			arg0.pipeline().addLast(new StringDecoder());
			arg0.pipeline().addLast(new EchoServerHandler());
		}

	}

	public static void main(String[] args) throws Exception {
		int port = 8080;
		new EchoServer().bind(port);
	}

}
