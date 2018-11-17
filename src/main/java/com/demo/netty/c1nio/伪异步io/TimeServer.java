package com.demo.netty.c1nio.伪异步io;

import java.net.ServerSocket;
import java.net.Socket;

//为了改进模型，后来出现了一种通过线程池或者消息队列实现1个或者多个线程处理n个客户端的模型，由于底层通信依然使用同步阻塞io，所以称为伪异步。
public class TimeServer {

	public static void main(String[] args) throws Exception {
		int port = 8080;
		ServerSocket server = null;
		try{
			server = new ServerSocket(port);
			System.out.println("The time server is start in port: " + port);
			Socket socket = null;
			TimeServerHandleExecutePool singleExecutor = new TimeServerHandleExecutePool(50, 10000);//创建io任务线程池
			//用一个无限循环来监听客户端连接，如果没有客户端连接，主线程阻塞在accept上
			while(true){
				socket = server.accept();
				singleExecutor.execute(new TimeServerHandle(socket));
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
