package com.demo.netty.reactor.多reactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

//Sub Reactor在实作上有个重点要注意，
//当一个监听中而阻塞住的selector由于Acceptor需要注册新的IO事件到该selector上时，
//Acceptor会调用selector的wakeup()函数唤醒阻塞住的selector，以注册新IO事件后再继续监听。
//但Sub Reactor中循环调用selector.select()的线程回圈可能会因为循环太快，导致selector被唤醒后再度于IO事件成功注册前被调用selector.select()而阻塞住，
//因此我们需要给Sub Reactor线程循环设置一个flag来控制，
//让selector被唤醒后不会马上进入下回合调用selector.select()的Sub Reactor线程循环，
//等待我们将新的IO事件注册完之后才能让Sub Reactor线程继续运行。
public class TCPSubReactor implements Runnable {
 
	private final ServerSocketChannel ssc;
	private final Selector selector;
	private boolean restart = false;
	int num;
 
	public TCPSubReactor(Selector selector, ServerSocketChannel ssc, int num) {
		this.ssc = ssc;
		this.selector = selector;
		this.num = num;
	}
 
	@Override
	public void run() {
		while (!Thread.interrupted()) { // 在線程被中斷前持續運行
			//System.out.println("waiting for restart");
			while (!Thread.interrupted() && !restart) { // 在線程被中斷前以及被指定重啟前持續運行
				try {
					if (selector.select() == 0)
						continue; // 若沒有事件就緒則不往下執行
				} catch (IOException e) {
					e.printStackTrace();
				}
				Set<SelectionKey> selectedKeys = selector.selectedKeys(); // 取得所有已就緒事件的key集合
				Iterator<SelectionKey> it = selectedKeys.iterator();
				while (it.hasNext()) {
					dispatch(it.next()); // 根據事件的key進行調度
					it.remove();
				}
			}
		}
	}
 
	/*
	 * name: dispatch(SelectionKey key) description: 調度方法，根據事件綁定的對象開新線程
	 */
	private void dispatch(SelectionKey key) {
		Runnable r = (Runnable) (key.attachment()); // 根據事件之key綁定的對象開新線程
		if (r != null)
			r.run();
	}
 
	public void setRestart(boolean restart) {
		this.restart = restart;
	}

}
