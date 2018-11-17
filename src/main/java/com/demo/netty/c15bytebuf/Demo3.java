package com.demo.netty.c15bytebuf;

import java.nio.ByteBuffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

public class Demo3 {

	//mark and reset
	//与ByteBuffer的类似，只是ByteBuf的mark 索引有两个，一个是writeIndex，一个是writeIndex。
	//通过markReadIndex()来标记read mark索引，通过markWriteIndex()来标记write mark索引。
	//通过resetReadIndex()来复位readIndex，通过resetWriteIndex()来复位writeIndex。
	public static void test1() {
		ByteBuf bf = Unpooled.buffer(10);
		for(int i=0; i<9; i++){
			bf.writeByte(i);
		}
		for(int i=0; i<bf.readableBytes(); i++){
			bf.readByte();
			if(i == 3){
				break;
			}
		}
		System.out.println(bf.readerIndex());
		System.out.println(bf.writerIndex());
		
		bf.markReaderIndex();
		bf.markWriterIndex();
		
		for(int i=0; i<5; i++){
			bf.writeByte(i*10);
		}
		bf.readByte();
		System.out.println(bf.readerIndex());
		System.out.println(bf.writerIndex());
		
		bf.resetReaderIndex();
		bf.resetWriterIndex();
		System.out.println(bf.readerIndex());
		System.out.println(bf.writerIndex());
	}

	//在一定的情况下可直接将ByteBuf转为字节数组byte[]或者ByteBuffer。
	//当ByteBuf.hasArray方法返回true时，可直接用ByteBuf.array()获取字节数组；当ByteBuf.nioBufferCount返回的整型值大于0时，可通过ByteBuf.nioBuffer()获取ByteBuffer对象。
	public static void test2() {
		ByteBuffer bb1 = ByteBuffer.wrap("hello".getBytes());
		ByteBuffer bb2 = ByteBuffer.wrap("world".getBytes());
		ByteBuf bf = Unpooled.wrappedBuffer(bb1, bb2);
		
		if(bf.nioBufferCount() > 0){
			ByteBuffer bb3 = bf.nioBuffer();
			while(bb3.hasRemaining()){
				System.out.print((char)bb3.get());
			}
			System.out.println();
		}
		
		if(bf.hasArray()){
			byte[] bytes = bf.array();
			System.out.println(new String(bytes));
		}
	}

	public static void main(String[] args) {
		test1();
		test2();
	}

}
