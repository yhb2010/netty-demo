package com.demo.netty.rpc.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//使用RpcRequest封装 RPC 请求，代码如下：
@SuppressWarnings("serial")
@ToString
@Getter
@Setter
public class RpcRequest implements Serializable {

	private String requestId;
    private String interfaceName;
    private String serviceVersion;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

}
