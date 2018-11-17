package com.demo.netty.c1nio.bio;

import java.net.ServerSocket;
import java.net.Socket;

//bio的主要问题是每当有一个新的客户端请求接入时，服务端必须创建一个新的线程处理新接入的客户端链路，一个线程只能处理一个客户端连接。
//在高性能服务器应用领域，往往需要面向成千上万的客户端连接，这种模型显然无法满足高性能、高并发的需求。
public class TimeServer {

	public static void main(String[] args) throws Exception {
		int port = 8080;
		ServerSocket server = null;
		try{
			server = new ServerSocket(port);
			System.out.println("The time server is start in port: " + port);
			Socket socket = null;
			//用一个无限循环来监听客户端连接，如果没有客户端连接，主线程阻塞在accept上
			while(true){
				socket = server.accept();
				new Thread(new TimeServerHandle(socket)).start();
			}
		}finally{
			if(server != null){
				System.out.println("The time server close");
				server.close();
				server = null;
			}
		}
	}

}
