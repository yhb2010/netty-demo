package com.demo.netty.c12私有协议栈;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//由于心跳消息、握手请求和握手应答消息都可以统一由NettyMessage承载，所以不需要为这几个内控制消息做单独的数据结构定义。
@Getter
@Setter
@ToString
public class Header {

	private int crcCode=0xabef0101;//Netty消息校验码：0xabef固定值，01主版本号，01次版本号
	private int length;//消息长度
	private long sessionID;//会话ID
	private byte type;//消息类型：
	//0:业务请求消息
	//1：业务响应消息
	//2：业务one way消息
	//3：握手请求消息
	//4：握手应答消息
	//5：心跳请求消息
	//6：心跳应答消息
	private byte priority;//消息优先级：0~255
	private Map<String,Object> attachment=new HashMap<String, Object>();//附件，可选字段，由于扩展消息头

}
