package com.demo.netty.c1nio.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandle implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandle> {

	@Override
	public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandle attachment) {
		//既然已经接收客户端成功了，为什么还要再次调用accept方法？因为：调用asynchronousServerSocketChannel的accept方法后，如果有新的客户端连接进来，系统将回调我们传入的
		//CompletionHandler实例completed方法，表示新的客户端已经接入成功。因为一个asynchronousServerSocketChannel可以接收成千上万的客户端，
		//所以需要继续调用它的accept方法，接收其它的客户端连接，最终形成一个循环。每当接收一个客户端连接成功后，再异步接收新的客户端连接。
		attachment.asynchronousServerSocketChannel.accept(attachment, this);
		//链路建立后，服务端需要接收客户端的请求消息，先预分配1m的缓冲区。
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		//read参数：接收缓冲区，用于从异步Channel中读取数据包
		//异步Channel携带的附件，通知回调的时候作为入参使用
		//CompletionHandle：接收通知回调的业务Handler，在本例中为ReadCompletionHandle
		result.read(buffer, buffer, new ReadCompletionHandle(result));
	}

	@Override
	public void failed(Throwable exc, AsyncTimeServerHandle attachment) {
		exc.printStackTrace();
		attachment.latch.countDown();
	}

}
