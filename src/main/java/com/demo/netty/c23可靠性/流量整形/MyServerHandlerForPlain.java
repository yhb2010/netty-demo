package com.demo.netty.c23可靠性.流量整形;

import io.netty.channel.ChannelHandlerContext;

//ServerHandler：当有客户端连接上了后就开始给客户端发送消息。并且通过『Channel#isWritable』方法以及『channelWritabilityChanged』事件来监控可写性，以判断啥时需要停止数据的写出，啥时可以开始继续写出数据。同时写了一个简易的task来计算每秒数据的发送速率（并非精确的计算）。
public class MyServerHandlerForPlain extends MyServerCommonHandler {

    @Override
    protected void sentData(ChannelHandlerContext ctx) {
        sentFlag = true;
        ctx.writeAndFlush(tempStr, getChannelProgressivePromise(ctx, future -> {
            if(ctx.channel().isWritable() && !sentFlag) {
                sentData(ctx);
            }
        }));
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        if(ctx.channel().isWritable() && !sentFlag) {
//          System.out.println(" ###### 重新开始写数据 ######");
            sentData(ctx);
        } else {
//          System.out.println(" ===== 写暂停 =====");
        }
    }

}