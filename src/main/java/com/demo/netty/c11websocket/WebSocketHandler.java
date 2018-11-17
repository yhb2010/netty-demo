package com.demo.netty.c11websocket;

import java.time.LocalDateTime;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame>{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        Channel channel = ctx.channel();
        //判断是否是关闭链路的指令
        if(frame instanceof CloseWebSocketFrame){
        	System.out.println("关闭");
        }
        //判断是否是ping的指令
        if(frame instanceof PingWebSocketFrame){
        	System.out.println("ping");
        	ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
        }
        //本例只支持文本消息
        if(frame instanceof TextWebSocketFrame){
        	System.out.println(channel.remoteAddress() + ": " + ((TextWebSocketFrame)frame).text());
        	ctx.channel().writeAndFlush(new TextWebSocketFrame("来自服务端: " + LocalDateTime.now()));
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ChannelId" + ctx.channel().id().asLongText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("用户下线: " + ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
    }
}