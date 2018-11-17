package com.demo.netty.reactor.精典;

public class Client {

	public static void main(String[] args) throws Exception {
		int port = 8080;
		new Thread(new ClientHandle("127.0.0.1", port), "TimeClient-001").start();
	}

}
