package com.demo.netty.c1nio.aio;

import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeClientHandler implements CompletionHandler<Void, AsyncTimeClientHandler>, Runnable {

	private AsynchronousSocketChannel client;
	private String host;
	private int port;
	private CountDownLatch latch;

	public AsyncTimeClientHandler(String host, int port) {
		this.host = host;
		this.port = port;
		try{
			client = AsynchronousSocketChannel.open();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		//创建CountDownLatch进行等待，防止异步操作还没有完成就退出。
		latch = new CountDownLatch(1);
		//this：AsynchronousSocketChannel的附件，用于回调通知时作为入参被传递，调用者可以自定义
		//this：CompletionHandler异步操作回调通知接口，由调用者实现。
		client.connect(new InetSocketAddress(host, port), this, this);
		try{
			latch.await();
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			client.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void completed(Void result, AsyncTimeClientHandler attachment) {
		byte[] req = "QUERY TIME ORDER".getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
		writeBuffer.put(req);
		writeBuffer.flip();
		//创建请求消息体，对其进行编码，然后复制到发送缓冲区writeBuffer中，调用write方法进行异步写。与服务器端类似，可以实现CompletionHandler接口用于写操作完成后的回调
		client.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {

			@Override
			public void completed(Integer result, ByteBuffer buffer) {
				//如果发送缓冲区仍有字节尚未发送完，继续异步发送，如果已经完成发送，则执行异步读取操作。
				if(buffer.hasRemaining()){
					client.write(buffer, buffer, this);
				}else{
					ByteBuffer readBuffer = ByteBuffer.allocate(1024);
					client.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>() {

						@Override
						public void completed(Integer result, ByteBuffer buffer) {
							buffer.flip();
							byte[] bytes = new byte[buffer.remaining()];
							buffer.get(bytes);
							String body;
							try{
								body = new String(bytes, "UTF-8");
								System.out.println("Now is: " + body);
								latch.countDown();
							}catch(Exception e){
								e.printStackTrace();
							}
						}

						@Override
						public void failed(Throwable exc, ByteBuffer attachment) {
							try{
								client.close();
								latch.countDown();
							}catch(Exception e){

							}
						}

					});
				}
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				try{
					client.close();
					latch.countDown();
				}catch(Exception e){

				}
			}

		});
	}

	@Override
	public void failed(Throwable exc, AsyncTimeClientHandler attachment) {
		try{
			client.close();
			latch.countDown();
		}catch(Exception e){

		}
	}

}
