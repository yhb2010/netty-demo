package com.demo.netty.reactor.精典;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

//精典Reactor模式
//在Reactor模式中，包含如下角色
//Reactor 将I/O事件发派给对应的Handler
//Acceptor 处理客户端连接请求
//Handlers 执行非阻塞读/写
//最简单的Reactor模式实现代码如下所示。
public class NIOServer {

	//为了方便阅读，上示代码将Reactor模式中的所有角色放在了一个类中。
	//从上示代码中可以看到，多个Channel可以注册到同一个Selector对象上，实现了一个线程同时监控多个请求状态（Channel）。同时注册时需要指定它所关注的事件，例如上示代码中socketServerChannel对象只注册了OP_ACCEPT事件，而socketChannel对象只注册了OP_READ事件。
	//selector.select()是阻塞的，当有至少一个通道可用时该方法返回可用通道个数。同时该方法只捕获Channel注册时指定的所关注的事件。
	public static void main(String[] args) throws IOException {
		Selector selector = Selector.open();
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.bind(new InetSocketAddress(8080));
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

		while (selector.select() > 0) {
			Set<SelectionKey> keys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = keys.iterator();
			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
				iterator.remove();
				if (key.isAcceptable()) {
					ServerSocketChannel acceptServerSocketChannel = (ServerSocketChannel) key.channel();
					SocketChannel socketChannel = acceptServerSocketChannel.accept();
					socketChannel.configureBlocking(false);
					System.out.println("Accept request from " + socketChannel.getRemoteAddress());
					socketChannel.register(selector, SelectionKey.OP_READ);
				} else if (key.isReadable()) {
					SocketChannel socketChannel = (SocketChannel) key.channel();
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					int count = socketChannel.read(buffer);
					if(count > 0){
						buffer.flip();
						byte[] bytes = new byte[buffer.remaining()];
						buffer.get(bytes);
						String body = new String(bytes, "utf-8");
						System.out.println("The time server receive order: " + body);
					}else if(count < 0){
						key.cancel();
						socketChannel.close();
					}else{
						//读到0字节，忽略
					}
				}
				keys.remove(key);
			}
		}
	}

}
