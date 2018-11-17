package com.demo.netty.c10http.httpjson;

import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class HttpJsonResponseEncoder extends AbsHttpJsonEncoder<HttpJsonResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, HttpJsonResponse message, List<Object> out) throws Exception {
        ByteBuf body = encode(ctx, message.getResult());
        FullHttpResponse response = message.getHttpResponse();
        //如果业务侧已经构造了http应答消息，则利用业务已有应答消息重新复制一个新的http应答消息。无法重用业务侧自定义http应答消息的主要原因，是netty的DefaultFullHttpResponse没有提供动态设置消息体content的接口，只能在第一次构造的时候设置内容。
        if (response == null) {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, body);
        } else {
            response = new DefaultFullHttpResponse(message.getHttpResponse().getProtocolVersion(), message.getHttpResponse().getStatus(), body);
        }
        //设置消息体内容格式为text/html，然后在消息头中设置消息体的长度。
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html;charset=UTF-8");
        HttpHeaders.setContentLength(response, body.readableBytes());
        out.add(response);
    }

}
