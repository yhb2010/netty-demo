package com.demo.netty.reactor.多reactor;

public class Client {

	public static void main(String[] args) throws Exception {
		int port = 8081;
		new Thread(new ClientHandle("127.0.0.1", port), "TimeClient-001").start();
		new Thread(new ClientHandle("127.0.0.1", port), "TimeClient-002").start();
		new Thread(new ClientHandle("127.0.0.1", port), "TimeClient-003").start();
		new Thread(new ClientHandle("127.0.0.1", port), "TimeClient-004").start();
		new Thread(new ClientHandle("127.0.0.1", port), "TimeClient-005").start();
		new Thread(new ClientHandle("127.0.0.1", port), "TimeClient-006").start();
		new Thread(new ClientHandle("127.0.0.1", port), "TimeClient-007").start();
		new Thread(new ClientHandle("127.0.0.1", port), "TimeClient-008").start();
		new Thread(new ClientHandle("127.0.0.1", port), "TimeClient-009").start();
		new Thread(new ClientHandle("127.0.0.1", port), "TimeClient-010").start();
		new Thread(new ClientHandle("127.0.0.1", port), "TimeClient-011").start();
		new Thread(new ClientHandle("127.0.0.1", port), "TimeClient-012").start();
		new Thread(new ClientHandle("127.0.0.1", port), "TimeClient-013").start();
		new Thread(new ClientHandle("127.0.0.1", port), "TimeClient-014").start();
		new Thread(new ClientHandle("127.0.0.1", port), "TimeClient-015").start();
		new Thread(new ClientHandle("127.0.0.1", port), "TimeClient-016").start();
	}

}
