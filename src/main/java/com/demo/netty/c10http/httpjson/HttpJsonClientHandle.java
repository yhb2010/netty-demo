package com.demo.netty.c10http.httpjson;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HttpJsonClientHandle extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		HttpJsonRequest request = new HttpJsonRequest(null, OrderFactory.create(123));
		ctx.writeAndFlush(request);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object o) throws Exception {
		HttpJsonResponse msg = (HttpJsonResponse) o;
		System.out.println("The client receive response of http header is : " + msg.getHttpResponse().headers().names());
		System.out.println("The client receive response of http body is : " + msg.getResult());
		System.out.println("The client receive response of http class is : " + msg.getResult().getClass());
	}

}