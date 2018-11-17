package com.demo.netty.c2netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TimeServer {

	public void bind(int port) throws Exception{
		//配置服务端的nio线程组
		//NioEventLoopGroup是线程组，包含了一组nio线程，专门用于处理网络事件，实际上他们就是Reactor线程组。、
		//一个用于服务端接收客户端连接，一个用于进行SocketChannel的网络读写。
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try{
			//用于启动nio服务端的辅助启动类，目的是降低服务端的开发复杂度。
			ServerBootstrap b = new ServerBootstrap();
			//将两个线程组传入ServerBootstrap中；设置创建的Channel为NioServerSocketChannel，它的功能对应jdk nio库的ServerSocketChannel；
			//设置tcp参数，将backlog设置为1024；最后绑定io事件的处理类ChildChannelHandler，类似Reactor模式中的Handler类，主要用于处理网络io事件，例如记录日志、对消息进行编解码等。
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024).childHandler(new ChildChannelHandler());
			//绑定端口，同步等待成功
			ChannelFuture f = b.bind(port).sync();
			//等待服务端监听端口关闭
			f.channel().closeFuture().sync();
		}finally{
			//优雅的退出，释放线程池资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}

	}

	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel arg0) throws Exception {
			arg0.pipeline().addLast(new TimeServerHandler());
		}

	}

	public static void main(String[] args) throws Exception {
		int port = 8080;
		new TimeServer().bind(port);
	}

}
