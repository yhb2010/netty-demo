package com.demo.netty.c10http.httpjson;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.StringWriter;
import java.nio.charset.Charset;

import com.cdel.util.helper.JacksonUtil;

public abstract class AbsHttpJsonEncoder<T> extends MessageToMessageEncoder<T> {

	StringWriter writer = null;
	final static String CHARSET_NAME = "UTF-8";
	final static Charset UTF_8 = Charset.forName(CHARSET_NAME);

	//body：http消息体
	protected ByteBuf encode(ChannelHandlerContext ctx, Object body) throws Exception {
		// 在此将业务的Order实例序列化为Json字符串。
		String jsonStr = JacksonUtil.ObjecttoJSon(body);
		// 将Json字符串包装成Netty的ByteBuf并返回，实现了HTTP请求消息的Json编码。
		ByteBuf encodeBuf = Unpooled.copiedBuffer(jsonStr, UTF_8);
		return encodeBuf;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// 释放资源
		if (writer != null) {
			writer.close();
			writer = null;
		}
	}

}