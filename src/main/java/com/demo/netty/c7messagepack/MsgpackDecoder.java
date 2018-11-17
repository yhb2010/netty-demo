package com.demo.netty.c7messagepack;

import java.util.List;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

//首先从数据报arg1中获取需要解码的byte数组，然后调用MessagePack的read方法将其反序列化为Object对象，将解码后的对象加入到解码列表arg2中，这样就完成了MessagePack的解码操作
public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {

	@Override
	protected void decode(ChannelHandlerContext arg0, ByteBuf arg1, List<Object> arg2) throws Exception {
		final byte[] array;
		final int length = arg1.readableBytes();
		array = new byte[length];
		arg1.getBytes(arg1.readerIndex(), array, 0, length);
		MessagePack msgpack = new MessagePack();
		arg2.add(msgpack.read(array));
	}

}
