package com.demo.netty.c1nio.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;

public class ReadCompletionHandle implements CompletionHandler<Integer, ByteBuffer> {

	private AsynchronousSocketChannel channel;

	public ReadCompletionHandle(AsynchronousSocketChannel channel) {
		if(this.channel == null){
			this.channel = channel;
		}
	}

	@Override
	public void completed(Integer result, ByteBuffer attachment) {
		attachment.flip();
		byte[] body = new byte[attachment.remaining()];
		attachment.get(body);
		try{
			String req = new String(body, "UTF-8");
			System.out.println("The time server receive order: " + req);
			String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(req) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
			doWrite(currentTime);
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	private void doWrite(String currentTime) {
		if(currentTime != null && currentTime.trim().length() > 0){
			byte[] bytes = currentTime.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);
			writeBuffer.flip();
			//CompletionHandler接口的实现类作为操作完成的回调
			channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {

				@Override
				public void completed(Integer result, ByteBuffer buffer) {
					//如果没有发生完成，继续发送
					if(buffer.hasRemaining()){
						channel.write(buffer, buffer, this);
					}
				}

				@Override
				public void failed(Throwable exc, ByteBuffer attachment) {
					try{
						channel.close();
					}catch(Exception e){

					}
				}

			});
		}
	}

	//当异常发生时对异常进行判断：如果是io异常，就关闭链路，释放资源；如果是其它异常，按照业务自己的逻辑进行处理。本例作为简单demo，没有对异常进行分类判断，只要发生了读写异常，就关闭链路，释放资源
	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		try{
			this.channel.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
