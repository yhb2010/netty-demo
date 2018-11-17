package com.demo.netty.c10http.httpfile;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpFileServer {

private static final String DEFAULT_URL = "/src/main/java/com/demo/netty";

    public void run(final int port, final String url)throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                    //将多个消息转换为单一的FullHttpRequest或者FullHttpResponse，原因是http解码器在每个http消息中会生成多个消息对象。
                    ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                    ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                    //支持异步发送大的码流（例如大的文件传输），但不会用过多的内存，防止发生java内存溢出
                    ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                    ch.pipeline().addLast("fileServerHandler", new HttpFileServerHandler());
                }
            });

            ChannelFuture f = b.bind("localhost", port).sync();
            System.out.println("HTTP 文件服务器启动, 地址是： " + "http://localhost:" + port + url);
            f.channel().closeFuture().sync();

        }finally{
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args)throws Exception {
        int port = 8081;
        if(args.length > 0)
        {
            try{
                port = Integer.parseInt(args[0]);
            }catch(NumberFormatException e){
                port = 8080;
            }
        }

        String url = DEFAULT_URL;
        if(args.length > 1)
            url = args[1];
        new HttpFileServer().run(port, url);
    }

}
