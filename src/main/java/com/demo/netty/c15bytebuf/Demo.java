package com.demo.netty.c15bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Demo {

	public static void test1() {
		ByteBuf heapBuffer = Unpooled.buffer(8);
		System.out.println("初始化：" + heapBuffer);
		heapBuffer.writeBytes("测试测试测试".getBytes());
		// cap初始化8，增加到64
		System.out.println("写入测试测试测试：" + heapBuffer);
	}

	public static void test2() {
		// 1、创建缓冲区
		ByteBuf heapBuffer = Unpooled.buffer(8);
		// 2、写入缓冲区内容
		heapBuffer.writeBytes("测试测试测试".getBytes());
		// 3、创建字节数组
		byte[] b = new byte[heapBuffer.readableBytes()];
		System.out.println(b[11]);
		// 4、复制内容到字节数组b
		heapBuffer.readBytes(b);
		System.out.println(b[11]);
		// 5、字节数组转字符串
		String str = new String(b);
		System.out.println(str);
	}
	
	//包装缓冲区（wrapper buffer），这种buffer只是一个基于现存的字节数组或者ByteBuffer对象或者字符串的视图，对数组或者ByteBuffer的任何修改都会反映到包装的ByteBuf中。包装创建方式的方法名都为wrappedBuffer。
	public static void test3(){
		byte[] bytes = "hello world".getBytes();
		ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
		bytes[1] = 'E';
		for (int i = 0; i < byteBuf.readableBytes(); i++) {
			System.out.print((char)byteBuf.getByte(i));
		}
		System.out.println();
	}
	
	//拷贝缓冲区（copy buffer），这种buffer是一种深度拷贝的方式，拷贝现有的字节数组byte[]， ByteBuffer对象或者字符串，并重新分配一块内存区域来存储数据拷贝数据，因此它们并不会分享同一数据。通常用这种方式来合并多个Buffer，拷贝创建方式的方法名都为copiedBuffer。
	public static void test4(){
		byte[] bytes = "hello world".getBytes();
		byte[] bytes2 = "welcome".getBytes();
		ByteBuf byteBuf = Unpooled.copiedBuffer(bytes, bytes2);
		bytes[1] = 'E';
		bytes2[0] = 'B';
		for (int i = 0; i < byteBuf.readableBytes(); i++) {
			System.out.print((char)byteBuf.getByte(i));
		}
		System.out.println();
		
	}

	public static void main(String[] args) {
		test1();
		test2();
		test3();
		test4();
	}

}
