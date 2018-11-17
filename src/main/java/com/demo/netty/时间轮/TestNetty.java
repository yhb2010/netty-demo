package com.demo.netty.时间轮;

import io.netty.util.HashedWheelTimer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

//由于netty动辄管理100w+的连接，每一个连接都会有很多超时任务。比如发送超时、心跳检测间隔等，如果每一个定时任务都启动一个Timer,不仅低效，而且会消耗大量的资源。
//时间轮其实就是一种环形的数据结构，可以想象成时钟，分成很多格子，一个格子代码一段时间（这个时间越短，Timer的精度越高）。并用一个链表保存在该格子上的到期任务，同时一个指针随着时间一格一格转动，并执行相应格子中的到期任务。任务通过取摸决定放入那个格子。
//假设一个格子是1秒，则整个wheel能表示的时间段为8s，假如当前指针指向2，此时需要调度一个3s后执行的任务，显然应该加入到(2+3=5)的方格中，指针再走3次就可以执行了；如果任务要在10s后执行，应该等指针走完一个round零2格再执行，因此应放入4，同时将round（1）保存到任务中。检查到期任务时应当只执行round为0的，格子上其他任务的round应减1。
public class TestNetty {

	public static void main(String[] args) throws Exception {
		test2();
	}

	public static void test1() throws Exception {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	    HashedWheelTimer hashedWheelTimer = new HashedWheelTimer(100, TimeUnit.MILLISECONDS);
	    System.out.println("start:" + LocalDateTime.now().format(formatter));
	    hashedWheelTimer.newTimeout(timeout -> {
	        System.out.println("task :" + LocalDateTime.now().format(formatter));
	    }, 3, TimeUnit.SECONDS);
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
