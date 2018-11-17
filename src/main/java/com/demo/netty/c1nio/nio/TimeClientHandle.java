package com.demo.netty.c1nio.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class TimeClientHandle implements Runnable {

	private String host;
	private int port;
	private Selector selector;
	private SocketChannel socketChannel;
	private volatile boolean stop;

	public TimeClientHandle(String host, int port) {
		this.host = host == null ? "127.0.0.1" : host;
		this.port = port;
		try{
			selector = Selector.open();
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try{
			doConnect();
		}catch(IOException e){
			e.printStackTrace();
		}
		while(!stop){
			try{
				selector.select(1000);
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				SelectionKey key = null;
				while(it.hasNext()){
					key = it.next();
					it.remove();
					try{
						handleInput(key);
					}catch(Exception e){
						if(key != null){
							key.cancel();
							if(key.channel() != null){
								key.channel().close();
							}
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		//多路复用器关闭后，所有注册在上面的Channel和Pipe等资源都会被自动去注册并关闭，所以不需要重复释放资源
		if(selector != null){
			try{
				selector.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}

	private void handleInput(SelectionKey key) throws IOException {
		if (key.isValid()){
			//判断连接是否成功
			SocketChannel sc = (SocketChannel)key.channel();
			//如果处于连接状态，说明服务器已经返回了ACK应答消息。这时我们需要对连接结果进行判断。
			//调用finishConnect，如果返回true，说明客户端连接成功，如果返回false或直接抛出异常，说明连接失败。
			//本例中，如果连接成功，将SocketChannel注册到多路复用器上，注册SelectionKey.OP_READ操作位，监听网络读操作，然后发送请求消息给服务端。
			if(key.isConnectable()){
				if(sc.finishConnect()){
					sc.register(selector, SelectionKey.OP_READ);
					doWrite(sc);
				}else{
					System.exit(1);//连接失败，进程退出
				}
			}
			if(key.isReadable()){
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				int readBytes = sc.read(readBuffer);
				if(readBytes > 0){
					readBuffer.flip();
					byte[] bytes = new byte[readBuffer.remaining()];
					readBuffer.get(bytes);
					String body = new String(bytes, "utf-8");
					System.out.println("Now is: " + body);
					stop = true;
				}else if(readBytes < 0){
					key.cancel();
					sc.close();
				}else{
					//读到0字节，忽略
				}
			}
		}
	}

	//连接操作，作为实例，连接是成功的，因此不需要多次重连操作，将其放到循环之前。
	//如果连接成功，将socketChannel注册到多路复用器selector上，注册OP_READ，如果没有连接成功，说明服务端没有没有返回tcp握手应答消息，但并不代表连接失败。
	//我们将socketChannel注册到多路复用器selector上，注册OP_CONNECT，当服务端返回tcp syn-ack消息后，selector就能够轮询到这个socketChannel处于连接就绪状态。
	private void doConnect() throws IOException {
		if(socketChannel.connect(new InetSocketAddress(host, port))){
			socketChannel.register(selector, SelectionKey.OP_READ);
			doWrite(socketChannel);
		}else{
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
		}
	}

	private void doWrite(SocketChannel sc) throws IOException {
		byte[] req = "QUERY TIME ORDER".getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
		writeBuffer.put(req);
		writeBuffer.flip();
		sc.write(writeBuffer);
		//异步会有写半包，最后通过hasRemaining对发送结果进行判断，如果缓冲区的消息全部发送完成，打印内容
		if(!writeBuffer.hasRemaining()){
			System.out.println(Thread.currentThread().getName() + ": Send order 2 server succeed.");
		}
	}

}
