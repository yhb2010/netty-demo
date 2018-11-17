package com.demo.netty.reactor.多reactor;

import java.io.IOException;
 
//Main Reactor：负责监听外部的连线请求，并派发给Acceptor处理。故Main Reactor中的selector只有注册OP_ACCEPT事件，也只能监听OP_ACCEPT事件。
//Acceptor接受连线后会给client绑定一个Handler并注册IO事件到Sub Reactor上监听，
//对于有多个Sub Reactor的情况下，IO事件选择注册给哪个Sub Reactor则是采用Round-robin的机制来分配。
//Sub Reactor：負責監聽IO事件，並派發IO事件給Handler處理。Sub Reactor線程的數量可以設置為CPU核心數。
public class Main {
	
	public static void main(String[] args) {
		try {
			TCPReactor reactor = new TCPReactor(8081);
			new Thread(reactor).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
 
}