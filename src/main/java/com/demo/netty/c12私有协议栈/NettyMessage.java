package com.demo.netty.c12私有协议栈;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public final class NettyMessage {

	private Header header;//消息头
	private Object body;//消息体，对于请求消息，它只是方法的参数；对于响应消息，它是返回值

}
