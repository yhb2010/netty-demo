package com.demo.netty.c12私有协议栈;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NettyClient {

	private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

	public static void main(String[] args) {
		new NettyClient().connect(14200);
	}

	private void connect(int port) {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			//1.BootStrap 和 ServerBootstrap 类似,不过他是对非服务端的 channel 而言，比如客户端或者无连接传输模式的 channel。
			Bootstrap b = new Bootstrap();
			//2.如果你只指定了一个 EventLoopGroup，那他就会即作为一个 boss group ，也会作为一个 workder group，尽管客户端不需要使用到 boss worker 。
			//3.代替NioServerSocketChannel的是NioSocketChannel,这个类在客户端channel 被创建时使用。
			b.group(group).channel(NioSocketChannel.class)
				//4.不像在使用 ServerBootstrap 时需要用 childOption() 方法，因为客户端的 SocketChannel 没有父亲。
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<Channel>() {
					@Override
					protected void initChannel(Channel ch) throws Exception {
						//利用Netty的ChannelPipeline和ChannelHandler机制，可以非常方便的实现功能的解耦和业务产品的定制。例如本例中的心跳定时器、握手请求和后端的业务处理可以通过不同的Handler来实现，类似于Aop。通过Handler chain的机制可以方便的实现切面拦截和定制，相比于Aop它的性能更高。
						//用于netty消息解码，为了防止由于单条消息过大导致的内存溢出或者畸形码流导致解码错位引起内存分配失败，我们对单条消息最大长度进行了上限限制
						ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4, -8, 0));
						//心跳超时的实现非常简单，直接利用Netty的ReadTimeoutHandler机制，当一定周期内（默认值50s）没有读取到对方任何消息时，需要主动关闭链路。
						//如果是客户端，重新发起连接：如果是服务端，释放资源，清除客户端登录缓存信息，等待客户端重连。
						//netty消息解码器，用于协议消息的自动编码，随后依次增加了读超时handler、握手请求handler和心跳消息handler
						ch.pipeline().addLast("MessageEncoder", new NettyMessageEncoder());
						ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
						ch.pipeline().addLast("LoginAuthHandler", new LoginAuthReqHandler());
						ch.pipeline().addLast("HeartBeatHandler", new HeartBeatReqHandler());
					}
				});

			// 发起异步连接操作与之前的不同，这次我们绑定了本地端口，主要用于服务端重复登录保护，另外，从产品管理角度看，一般情况下不允许系统随便使用随机端口。
			//5.我们用 connect() 方法代替了 bind() 方法。
			ChannelFuture future = b.connect(new InetSocketAddress(port), new InetSocketAddress(14201)).sync();
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			//当客户端感知断连事件之后，释放资源，重新发起连接，具体实现代码如下：
			//首先监听网络断连事件，如果channel关闭，则执行后续的重连任务，通过Bootstarp重新发起连接，客户端挂在closeFuture上监听链路关闭信号，一旦关闭，则创建重连定时器，5s之后重新发起连接，直到重连成功。
			//服务器端感知到断连事件之后，需要情况缓存的登录认证注册消息，以保证后续客户端能够正常连接。
			//所以资源释放完成之后，清空资源，再次发起重连操作。
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					try {
						TimeUnit.SECONDS.sleep(5);
						connect(port);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}

	}

}