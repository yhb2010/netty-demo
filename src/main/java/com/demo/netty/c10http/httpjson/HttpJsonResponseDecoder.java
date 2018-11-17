package com.demo.netty.c10http.httpjson;

import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;

public class HttpJsonResponseDecoder extends AbsHttpJsonDecoder<FullHttpResponse> {

	protected HttpJsonResponseDecoder(Class<?> clazz) {
		super(clazz);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, FullHttpResponse response, List<Object> out) throws Exception {
		HttpJsonResponse res = new HttpJsonResponse(response, decode(ctx, response.content()));
		out.add(res);
	}

}