package com.netty;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeClientHandler extends ChannelHandlerAdapter{

    private final ByteBuf firstMsg;
    
     public TimeClientHandler() {
         String   content = "QUERY TIME";
         byte[] b = content.getBytes();
         firstMsg =  Unpooled.buffer(b.length);
         firstMsg.writeBytes(b);
    }
    
    
     //connect 
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //客户端和服务端连接成功调用
        ctx.writeAndFlush(firstMsg);
    }
    
   
	
    // read
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //客户端发送信息接收
          ByteBuf  buf  = (ByteBuf) msg;
          byte[] b = new byte[buf.readableBytes()];
          buf.readBytes(b);
          System.out.println(new String(b));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //异常 释放资源
        ctx.close();
        cause.printStackTrace();
    }
     
}
