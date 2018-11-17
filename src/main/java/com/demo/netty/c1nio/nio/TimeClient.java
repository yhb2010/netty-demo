package com.demo.netty.c1nio.nio;

public class TimeClient {

	public static void main(String[] args) throws Exception {
		int port = 8080;
		new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient-001").start();
		new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient-002").start();
		new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient-003").start();
		new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient-004").start();
	}

}
