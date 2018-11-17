package com.demo.netty.reactor.多reactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

//状态介面
public interface HandlerState {
 
	public void changeState(TCPHandler h);
 
	public void handle(TCPHandler h, SelectionKey sk, SocketChannel sc, ThreadPoolExecutor pool) throws IOException ;

}
