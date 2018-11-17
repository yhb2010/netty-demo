package com.demo.netty.c10http.httpjson;

import java.net.InetAddress;
import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

public class HttpJsonRequestEncoder extends AbsHttpJsonEncoder<HttpJsonRequest> {

    @Override
    protected void encode(ChannelHandlerContext ctx, HttpJsonRequest message, List<Object> out) throws Exception {
        ByteBuf body = encode(ctx, message.getBody());
        FullHttpRequest request = message.getRequest();
        //构造默认的http消息头，由于通常http通信双方更关注消息体本身，所以这里采用了硬编码的方式，如果要产品化，可以做成配置的
        if (request == null) {
            request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/do", body);
            HttpHeaders headers = request.headers();
            headers.set(HttpHeaders.Names.HOST, InetAddress.getLocalHost().getHostAddress());
            headers.set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            headers.set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP.toString() + "," + HttpHeaders.Values.DEFLATE.toString());
        }
        //由于消息体不为空，也没有使用chunk方式，所以在http消息头中设置消息体的长度ContentLength，完成消息体的json序列化后将重新构造的http请求消息加入到out中，由后续netty的http请求编码器继续对http请求消息进行编码
        HttpHeaders.setContentLength(request, body.readableBytes());
        out.add(request);
    }

}
