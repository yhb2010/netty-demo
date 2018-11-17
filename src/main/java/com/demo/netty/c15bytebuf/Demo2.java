package com.demo.netty.c15bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

public class Demo2 {

	public static void test1() {
		ByteBuf byteBuf = Unpooled.buffer(10);
		for (int i = 0; i < 8; i++) {
			byteBuf.writeByte(i);
		}
		int result = byteBuf.readByte();//将数据从byteBuf读出，readIndex+1
		System.out.println(byteBuf.readerIndex());
		
		ByteBuf dest = Unpooled.buffer(10);
		byteBuf.readBytes(dest, 4);//将数据从byteBuf读到dest，此时byteBuf的readIndex+4，dest的writeIndex+4
		System.out.println(byteBuf.readerIndex());
		System.out.println(dest.writerIndex());
	}

	public static void test2() {
		ByteBuf readAbleBuffer = Unpooled.wrappedBuffer("java".getBytes());
		ByteBuf byteBuf = Unpooled.buffer(10);
		byteBuf.writeBytes("hello world ".getBytes());//byteBuf的writeIndex+12
		byteBuf.writeBytes(readAbleBuffer);//将readAbleBuffer数据写入byteBuf，因此readAbleBuffer的readIndex+4，byteBuf的writeIndex+4
		System.out.println(byteBuf.writerIndex());
		System.out.println(byteBuf.capacity());
		System.out.println(readAbleBuffer.readerIndex());
		while(byteBuf.isReadable()){
			System.out.print((char)byteBuf.readByte());
		}
		System.out.println();
	}
	
	//discardable bytes
	//可丢弃的字节区域，表示的是已经被读取过的数据。
	//这块区域随着buffer的读取而逐渐变大，当buffer无法再进行读取时，也就是readIndex == writeIndex时，此时达到最大。
	//通过discardReadBytes()方法可丢弃这些字节（不等于将底层清空）并重新回收其所占的空间（可重新被写入数据），其实就是将Readable bytes区域往前移动，直至readIndex=0。

	//ByteBuf类型
	//ByteBuf主要有三种类型：heap buffer（堆缓冲）、direct buffer（直接缓冲/堆外缓冲）、composite buffer（虚拟缓冲/复合缓冲）
	//heap buffer：数据以字节数组的方式存储在jvm的堆空间中。
	//direct buffer：数据存储在堆外内存，由操作系统直接管理。
	//composite buffer：将多个已存在的buffer对象组合成一个buffer对象。下面来看个composite buffer的例子：
	public static void test3(){
		ByteBuf byteBuf = Unpooled.buffer();
		byteBuf.writeBytes("hello".getBytes());
		ByteBuf byteBuf2 = Unpooled.buffer();
		byteBuf2.writeBytes(" world".getBytes());
		//将一个buffer内容为content的ByteBuf对象和一个buffer内容为world的ByteBuf对象添加到一个composite ByteBuf对象，添加的时候，第一个参数若设置为true，就可以直接操作虚拟buffer的read操作，将两个BytBuf对象的内容都获取到。若参数设置为false，只能通过虚拟buffer的component方法分别获取两个ByteBuf对象，再获取对应的数据。
		CompositeByteBuf cb = Unpooled.compositeBuffer();
		cb.addComponent(true, byteBuf);
		cb.addComponent(true, byteBuf2);
		System.out.println(cb.writerIndex());
		System.out.println(cb.readerIndex());
		
		ByteBuf byteBuf3 = cb.component(0);
		while(byteBuf3.isReadable()){
			System.out.print((char)byteBuf3.readByte());
		}
		System.out.println();
		
		ByteBuf byteBuf4 = cb.component(1);
		while(byteBuf4.isReadable()){
			System.out.print((char)byteBuf4.readByte());
		}
		System.out.println();
		
		while(cb.isReadable()){
			System.out.print((char)cb.readByte());
		}
		System.out.println();
	}

	public static void main(String[] args) {
		test1();
		test2();
		test3();
	}

}
