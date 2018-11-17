package com.demo.netty.rpc.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//使用RpcResponse封装 RPC 响应，代码如下：
@SuppressWarnings("serial")
@ToString
@Getter
@Setter
public class RpcResponse implements Serializable {

	private String requestId;
    private Exception exception;
    private Object result;

    public boolean hasException() {
        return exception != null;
    }

}
