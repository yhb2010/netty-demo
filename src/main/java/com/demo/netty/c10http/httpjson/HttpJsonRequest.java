package com.demo.netty.c10http.httpjson;

import lombok.Data;
import io.netty.handler.codec.http.FullHttpRequest;

@Data
public class HttpJsonRequest {

	//它包含两个成员变量FullHttpRequest和编码对象Object，用于实现和协议栈之间的解耦。
    private FullHttpRequest request;
    private Object body;

    public HttpJsonRequest(FullHttpRequest request, Object body) {
        this.body = body;
        this.request = request;
    }

}
