package com.demo.netty.时间轮;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface Timer {

	public abstract Timeout newTimeout(TimerTask timertask, long l, TimeUnit timeunit);

	public abstract Set stop();

}

