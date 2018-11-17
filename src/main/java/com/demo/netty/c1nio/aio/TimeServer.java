package com.demo.netty.c1nio.aio;

import java.net.ServerSocket;
import java.net.Socket;

//aio
public class TimeServer {

	public static void main(String[] args) throws Exception {
		int port = 8080;
		AsyncTimeServerHandle timeServer = new AsyncTimeServerHandle(port);
		new Thread(timeServer, "Aio-AsynctimeServerHandle-001").start();
	}

}
