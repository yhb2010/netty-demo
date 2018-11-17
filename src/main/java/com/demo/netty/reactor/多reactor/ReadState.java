package com.demo.netty.reactor.多reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;
 
public class ReadState implements HandlerState{
 
	private SelectionKey sk;
	
	public ReadState() {
	}
	
	@Override
	public void changeState(TCPHandler h) {
		h.setState(new WorkState());
	}
 
	@Override
	public void handle(TCPHandler h, SelectionKey sk, SocketChannel sc, ThreadPoolExecutor pool) throws IOException { // read()
		this.sk = sk;
		// non-blocking下不可用Readers，因為Readers不支援non-blocking
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		int numBytes = sc.read(buf); // 讀取字符串
		if(numBytes == -1)
		{
			System.out.println(System.currentTimeMillis() + " [Warning!] A client has been closed.");
			h.closeChannel();
			return;
		}
		buf.flip();
		byte[] arr = new byte[buf.remaining()];
		buf.get(arr);
		String str = new String(arr); // 將讀取到的byte內容轉為字符串型態
		if ((str != null) && !str.equals(" ")) {
			h.setState(new WorkState()); // 改變狀態(READING->WORKING)
			pool.execute(new WorkerThread(h, str)); // do process in worker thread
			System.out.println(System.currentTimeMillis() + " " + sc.socket().getRemoteSocketAddress().toString() + " " + str);
		}
		
	}
	
	/*
	 * 執行邏輯處理之函數
	 */
	synchronized void process(TCPHandler h, String str) {
		// do process(decode, logically process, encode)..
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		h.setState(new WriteState()); // 改變狀態(WORKING->SENDING)
		this.sk.interestOps(SelectionKey.OP_WRITE); // 通過key改變通道註冊的事件
		this.sk.selector().wakeup(); // 使一個阻塞住的selector操作立即返回
	}
 
	/*
	 * 工作者線程
	 */
	class WorkerThread implements Runnable {
 
		TCPHandler h;
		String str;
 
		public WorkerThread(TCPHandler h, String str) {
			this.h = h;
			this.str=str;
		}
 
		@Override
		public void run() {
			process(h, str);
		}
 
	}

}
