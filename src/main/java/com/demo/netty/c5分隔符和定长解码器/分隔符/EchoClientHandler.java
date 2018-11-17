package com.demo.netty.c5分隔符和定长解码器.分隔符;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;

public class EchoClientHandler implements ChannelInboundHandler {

	private int counter;
	private static final String ECHO_STR = "Hi, Lilinfeng. Welcome to Netty.$_";

	public EchoClientHandler() {
	}

	@Override
	public void handlerAdded(ChannelHandlerContext arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void handlerRemoved(ChannelHandlerContext arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	//客户端和服务器端建立连接后，netty的nio线程会调用channelActive按方法，发送查询时间的指令给服务端，调用writeAndFlush方法将请求消息发送给服务端
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		for(int i=0; i<10; i++){
			ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_STR.getBytes()));
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	//当服务端应答消息的时候，该方法被调用
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("This is " + ++counter + " times receive server: [" + msg + "]");
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext arg0)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void channelRegistered(ChannelHandlerContext arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void channelUnregistered(ChannelHandlerContext arg0)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void channelWritabilityChanged(ChannelHandlerContext arg0)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		//释放资源
		ctx.close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext arg0, Object arg1)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
