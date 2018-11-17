package com.demo.netty.c10http.rest;

import com.cdel.util.helper.JacksonUtil;
import com.demo.netty.c10http.rest.domain.RequestDomain;
import com.demo.netty.c10http.rest.domain.ResponseDomain;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

public class HealthServerHandler extends ChannelInboundHandlerAdapter {

	private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");
    private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");
    private static final AsciiString CONNECTION = new AsciiString("Connection");
    private static final AsciiString KEEP_ALIVE = new AsciiString("keep-alive");

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (msg instanceof FullHttpRequest) {
            FullHttpRequest req = (FullHttpRequest) msg;//客户端的请求对象
            ResponseDomain responseJson = new ResponseDomain();//新建一个返回消息的Json对象

            //把客户端的请求数据格式化为Json对象
            RequestDomain requestJson = null;
            try{
               requestJson = JacksonUtil.jsonToObject(parseJosnRequest(req), RequestDomain.class);
            }catch(Exception e){
                ResponseJson(ctx, req, new String("error json"));
                return;
            }

            String uri = req.uri();//获取客户端的URL

            //根据不同的请求API做不同的处理(路由分发)，只处理POST方法
            if (req.method() == HttpMethod.POST) {
                if(uri.equals("/bmi")){
                    //计算体重质量指数
                    double height = 0.01* requestJson.getHeight();
                    double weight = requestJson.getWeight();
                    double bmi = weight/(height*height);
                    bmi = ((int)(bmi*100))/100.0;
                    responseJson.setBmi(bmi +"");
                }else if(uri.equals("/bmr")){
                    //计算基础代谢率
                    boolean isBoy = requestJson.getIsBoy();
                    double height = requestJson.getHeight();
                    double weight = requestJson.getWeight();
                    int age = requestJson.getAge();
                    double bmr = 0;
                    if(isBoy){
                        //66 + ( 13.7 x 体重kg ) + ( 5 x 身高cm ) - ( 6.8 x 年龄years )
                        bmr = 66+(13.7*weight) +(5*height) -(6.8*age);

                    }else{
                        //655 + ( 9.6 x 体重kg ) + ( 1.8 x 身高cm ) - ( 4.7 x 年龄years )
                        bmr =655 +(9.6*weight) +1.8*height -4.7*age;
                    }

                    bmr = ((int)(bmr*100))/100.0;
                    responseJson.setBmr(bmr + "");
                }else {
                    //错误处理
                    responseJson.setError("404 Not Find");
                }
            } else {
                //错误处理
                responseJson.setError("404 Not Find");
            }

            //向客户端发送结果
            ResponseJson(ctx, req, JacksonUtil.ObjecttoJSon(responseJson));
        }
    }

    /**
     * 响应HTTP的请求
     * @param ctx
     * @param req
     * @param jsonStr
     */
    private void ResponseJson(ChannelHandlerContext ctx, FullHttpRequest req ,String jsonStr) {
        boolean keepAlive = HttpUtil.isKeepAlive(req);
        byte[] jsonByteByte = jsonStr.getBytes();
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(jsonByteByte));
        response.headers().set(CONTENT_TYPE, "text/json");
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

        if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.write(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 获取请求的内容
     * @param request
     * @return
     */
    private String parseJosnRequest(FullHttpRequest request) {
        ByteBuf jsonBuf = request.content();
        String jsonStr = jsonBuf.toString(CharsetUtil.UTF_8);
        return jsonStr;
    }

}
