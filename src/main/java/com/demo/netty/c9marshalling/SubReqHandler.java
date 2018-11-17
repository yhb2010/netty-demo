package com.demo.netty.c9marshalling;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SubReqHandler extends ChannelInboundHandlerAdapter {

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg);
        SubscriptResp sub = new SubscriptResp();
        sub.setDesc("desc");
        sub.setSubScriptID(999);
        sub.setRespCode("0");
        ctx.writeAndFlush(sub);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

}
