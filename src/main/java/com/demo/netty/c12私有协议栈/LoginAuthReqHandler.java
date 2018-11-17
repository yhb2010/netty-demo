package com.demo.netty.c12私有协议栈;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

//握手的发起是在客户端和服务器端TCP链路建立成功通道激活时，握手消息的接入和安全认证在服务端的处理。下面看下具体实现。
//首先开发一个握手认证的客户端ChannelHandle，用于在通道激活时发起握手请求，具体代码实现如下。
public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {

	//当客户端跟服务端tcp三次握手成功后，由客户端构造握手请求消息发送给服务端，由于采用ip白名单认证机制，因此，不需要携带消息体，消息体为空，消息类型为3，握手请求发送之后，按照协议，服务端需要返回握手应答消息。
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush(buildLoginReq());
	}

	//对握手应答消息进行处理，首先判断是否是握手应答消息，如果不是直接透传给后面的handler处理，如果是，则对应答结果进行判断，如果非0说明认证失败，关闭链路，重新发起连接。
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		NettyMessage message = (NettyMessage)msg;
		// 如果是握手应答消息，需要判断是否认证成功
		if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
			byte loginResult = (byte) message.getBody();
			if (loginResult != (byte) 0) {
				//握手失败，关闭连接
				ctx.close();
			} else {
				System.out.println("Login is OK:" + message);
				ctx.fireChannelRead(message);
			}
		} else {
			ctx.fireChannelRead(message);
		}
	}

	private NettyMessage buildLoginReq() {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.LOGIN_REQ.value());
		message.setHeader(header);
		return message;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.fireExceptionCaught(cause);
	}

}
