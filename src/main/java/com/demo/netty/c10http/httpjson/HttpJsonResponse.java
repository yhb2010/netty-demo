package com.demo.netty.c10http.httpjson;

import io.netty.handler.codec.http.FullHttpResponse;

//它包含两个成员变量：FullHttpResponse和Object，Object就是业务需要发送的应答POJO对象。
public class HttpJsonResponse {

	private FullHttpResponse httpResponse;
	private Object result;

	public HttpJsonResponse(FullHttpResponse httpResponse, Object result) {
		this.httpResponse = httpResponse;
		this.result = result;
	}

	public final FullHttpResponse getHttpResponse() {
		return httpResponse;
	}

	public final void setHttpResponse(FullHttpResponse httpResponse) {
		this.httpResponse = httpResponse;
	}

	public final Object getResult() {
		return result;
	}

	public final void setResult(Object result) {
		this.result = result;
	}

}