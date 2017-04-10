package com.netty.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;


/*
 * websocket 区别于传统的http(半双工)
 * 1)全双工
 * 2)通过 ping/pong 保持链路
 * 3)无需轮询
 * 
 * webSocket连接
 * 1)发送一个http 请求建立连接
 *  附加头信息：
 *  Upgrade:websocket
 * 2)发送消息
 * 
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object>{
    
    
    private  WebSocketServerHandshaker       handShaker ;

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        
        if(msg instanceof FullHttpRequest){
            processHttpRequest(ctx,(FullHttpRequest)msg);
        }else if(msg instanceof WebSocketFrame){
            processWebSocketFrame(ctx,(WebSocketFrame)msg);
        }
        
        
        
    }
    
    private void processWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame msg) {
        
        //关闭指令
        if(msg instanceof CloseWebSocketFrame){
            handShaker.close(ctx.channel(), (CloseWebSocketFrame)msg.retain());
            return ;
        }
        
        if(msg instanceof PingWebSocketFrame){//ping 指令
            ctx.channel().write(new PongWebSocketFrame(msg.content().retain()));
            return ;
        }
        
        //只支持 文本
        if(!(msg instanceof TextWebSocketFrame)){
            return ;
        }
        
        TextWebSocketFrame text =  (TextWebSocketFrame) msg;
        ctx.channel().write(new TextWebSocketFrame(text.text()+"welcome"));
        
    }

    //传统的http请求创建连接
    @SuppressWarnings("static-access")
    private void processHttpRequest(ChannelHandlerContext ctx, FullHttpRequest msg) {
        System.out.println(msg);
        if(!msg.getDecoderResult().isSuccess()||!"websocket".equals(msg.headers().get("Upgrade"))){
            SendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return ;
        }
        
        WebSocketServerHandshakerFactory  factory = new WebSocketServerHandshakerFactory("ws://localhost:8080/websocket", null, false);
        handShaker = factory.newHandshaker(msg);
        if(handShaker==null){
            factory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        }else{
            handShaker.handshake(ctx.channel(), msg);
        }
    }

    private void SendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        ByteBuf buf = Unpooled.buffer(status.reasonPhrase().getBytes().length);
        buf.writeBytes(status.reasonPhrase().getBytes());
        DefaultFullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,status,buf);
        resp.headers().add(Names.CONTENT_TYPE,"text/html");
        ctx.writeAndFlush(resp);
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}
