package com.demo.netty.c1nio.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeServerHandle implements Runnable {

	private int port;
	CountDownLatch latch;
	AsynchronousServerSocketChannel asynchronousServerSocketChannel;

	public AsyncTimeServerHandle(int port) {
		this.port = port;
		try{
			asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
			asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
			System.out.println("The time server is start in port: " + port);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		//作用是在完成一组操作之前，允许当前的线程一直阻塞，在本例中，我们让线程在此阻塞，防止服务端执行完成退出，在实际项目中，不需要启动独立的线程来处理AsynchronousServerSocketChannel。
		latch = new CountDownLatch(1);
		//接收客户端连接，由于是异步操作，我们可以传递一个CompletionHandle类型的handler实例接收accept操作成功的通知消息。
		doAccept();
		try{
			latch.await();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void doAccept() {
		asynchronousServerSocketChannel.accept(this, new AcceptCompletionHandle());
	}

}
