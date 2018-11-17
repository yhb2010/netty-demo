package com.demo.netty.c23可靠性.流量整形;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

//使用GlobalTrafficShapingHandler来实现服务端的“流量整形”，每当有客户端连接至服务端时服务端就会开始往这个客户端发送26M的数据包。
//我们将GlobalTrafficShapingHandler的writeLimit设置为10M/S。并使用了ChunkedWriteHandler来实现大数据包拆分成小数据包发送的功能。
public class NettyServer {

	final static int M = 1024 * 1024;

    public static void main(String[] args) throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new MyServerInitializer());
            ChannelFuture channelFuture = server.bind(14200).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
