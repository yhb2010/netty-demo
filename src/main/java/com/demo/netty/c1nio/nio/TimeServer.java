package com.demo.netty.c1nio.nio;

public class TimeServer {

	public static void main(String[] args) throws Exception {
		int port = 8080;
		//多路复用类，是一个独立的线程，负责轮询多路复用器Selector，可以处理多个客户端的并发接入。
		MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
		new Thread(timeServer, "NIO-MultiplexerTimeServer-001").start();
	}

}
