package com.demo.netty.c10http.httpjson;

import com.cdel.util.helper.JacksonUtil;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public abstract class AbsHttpJsonDecoder<T> extends MessageToMessageDecoder<T> {

	private final static String CHARSET_NAME = "UTF-8";
	private static Charset UTF_8 = Charset.forName(CHARSET_NAME);
	private Class<?> clazz;

	protected AbsHttpJsonDecoder(Class<?> clazz) {
		this.clazz = clazz;
	}

	protected Object decode(ChannelHandlerContext cxt, ByteBuf body) throws Exception {
		String content = body.toString(UTF_8);
		Object result =  null;
		try {
			result = JacksonUtil.jsonToObject(content, clazz);
		} catch (Exception e) {
			throw e;
		}

		return result;
	}

}
