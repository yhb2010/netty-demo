package com.demo.netty.c1nio.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;

public class MultiplexerTimeServer implements Runnable {

	private Selector selector;
	private ServerSocketChannel servChannel;
	private volatile boolean stop;

	/**初始化多路复用器，绑定监听端口
	 * @param port
	 */
	public MultiplexerTimeServer(int port) {
		try {
			selector = Selector.open();
			servChannel = ServerSocketChannel.open();
			servChannel.configureBlocking(false);
			servChannel.socket().bind(new InetSocketAddress(port), 1024);
			servChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("The time server is start in port: " + port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stop(){
		stop = true;
	}

	@Override
	public void run() {
		while(!stop){
			try{
				//休眠时间为1秒，无论是否有读写等事件发生，selector每隔1s都会被唤醒一次。
				//还有一个无参select方法，当有处于就绪状态的Channel时，selector将返回该Channel的SelectionKey集合。
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
			}catch(Throwable t){
				t.printStackTrace();
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
			//处理新接入的请求消息
			//根据SelectionKey的操作位进行判断即可获知网络事件的类型，通过ServerSocketChannel的accept接收客户端的连接请求并创建SocketChannel实例。
			//完成上述操作后，相当于完成了tcp的三次握手，tcp物理链路正式建立。
			//注意，SocketChannel可以设置非阻塞，tcp可以设置接收和发送缓冲区的大小等。
			if(key.isAcceptable()){
				SocketChannel ssc = ((ServerSocketChannel) key.channel()).accept();
				ssc.configureBlocking(false);
				ssc.register(selector, SelectionKey.OP_READ);
			}
			if(key.isReadable()){
				SocketChannel sc = (SocketChannel) key.channel();
				//由于事先并不知道客户端发送的码流大小，先开辟一个1m的缓冲区，然后调用read方法读取请求码流。注意由于把SocketChannel设置为异步非阻塞，因此read是非阻塞的。
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				int readBytes = sc.read(readBuffer);
				//返回值有三种可能：大于0：读到字节，对字节进行编解码；等于0：没读到字节，属于正常情况；小于0：链路已关闭，需要关闭SocketChannel释放资源。
				if(readBytes > 0){
					readBuffer.flip();
					byte[] bytes = new byte[readBuffer.remaining()];
					readBuffer.get(bytes);
					String body = new String(bytes, "utf-8");
					System.out.println("The time server receive order: " + body);
					String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
					doWrite(sc, currentTime);
				}else if(readBytes < 0){
					key.cancel();
					sc.close();
				}else{
					//读到0字节，忽略
				}
			}
		}
	}

	private void doWrite(SocketChannel channel, String response) throws IOException {
		//由于SocketChannel是异步的，并不能保证一次把需要发送的字节数发送完，会出现“写半包”问题，我们注册写操作，不断轮询Selector将没有发送完的ByteBuffer发送完毕，然后可以通过
		//ByteBuffer的hasRemain方法判断消息是否发生成功。
		if(response != null && response.trim().length() > 0){
			byte[] bytes = response.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);
			writeBuffer.flip();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			channel.write(writeBuffer);
		}
	}

}
