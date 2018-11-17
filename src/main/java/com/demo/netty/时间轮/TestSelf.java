package com.demo.netty.时间轮;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class TestSelf {

	public static void main(String[] args) throws Exception {
		test1();
	}

	public static void test1() throws Exception {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	    HashedWheelTimer hashedWheelTimer = new HashedWheelTimer(1000, TimeUnit.MILLISECONDS);
	    System.out.println("start:" + LocalDateTime.now().format(formatter));
	    hashedWheelTimer.newTimeout(timeout -> {
	        System.out.println("task1 :" + LocalDateTime.now().format(formatter));
	    }, 10, TimeUnit.SECONDS);
	    hashedWheelTimer.newTimeout(timeout -> {
	    	System.out.println("task2 :" + LocalDateTime.now().format(formatter));
	    }, 10, TimeUnit.SECONDS);
	    hashedWheelTimer.newTimeout(timeout -> {
	    	System.out.println("task3 :" + LocalDateTime.now().format(formatter));
	    }, 12, TimeUnit.SECONDS);
	    Thread.sleep(5000);
	}

	//可以看到，当前一个任务执行时间过长的时候，会影响后续任务的到期执行时间的。也就是说其中的任务是串行执行的。所以，要求里面的任务都要短平快。
	public static void test2() throws Exception {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	    HashedWheelTimer hashedWheelTimer = new HashedWheelTimer(100, TimeUnit.MILLISECONDS);
	    System.out.println("start:" + LocalDateTime.now().format(formatter));
	    hashedWheelTimer.newTimeout(timeout -> {
	        Thread.sleep(3000);
	        System.out.println("task1:" + LocalDateTime.now().format(formatter));
	    }, 3, TimeUnit.SECONDS);
	    hashedWheelTimer.newTimeout(timeout -> System.out.println("task2:" + LocalDateTime.now().format(
	            formatter)), 4, TimeUnit.SECONDS);
	    Thread.sleep(10000);
	}

}
