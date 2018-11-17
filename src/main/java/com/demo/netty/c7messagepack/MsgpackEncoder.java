package com.demo.netty.c7messagepack;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

//继承MessageToByteEncoder，他负责将Object类型的POJO对象编码为byte数组，然后写入到ByteBuf中
public class MsgpackEncoder extends MessageToByteEncoder<Object> {

	@Override
	protected void encode(ChannelHandlerContext arg0, Object arg1, ByteBuf arg2) throws Exception {
		MessagePack msgpack = new MessagePack();
		try{
			byte[] raw = msgpack.write(arg1);
			arg2.writeBytes(raw);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
