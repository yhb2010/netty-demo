package com.demo.netty.时间轮;

public interface TimerTask {

	public abstract void run(Timeout timeout) throws Exception;

}
