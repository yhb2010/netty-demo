package com.demo.netty.c23可靠性.流量整形;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

//客户端比较简单了，使用ChannelTrafficShapingHandler来实现“流量整形”，并将readLimit设置为1M/S。
public class NettyClient {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap client = new Bootstrap();
            client.group(group).channel(NioSocketChannel.class).handler(new MyClientInitializer());

            ChannelFuture channelFuture = client.connect("127.0.0.1", 14200).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
	}

}