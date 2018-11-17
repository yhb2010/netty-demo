package com.demo.netty.时间轮;

public interface Timeout {

	public abstract Timer timer();

	public abstract TimerTask task();

	public abstract boolean isExpired();

	public abstract boolean isCancelled();

	public abstract boolean cancel();

}