package com.netty;


import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeServerHandler extends ChannelHandlerAdapter{

    
     
     
    
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf  buf = (ByteBuf) msg;
        byte[]  b = new byte[buf.readableBytes()];
        buf.readBytes(b);
        String  body = new String(b,"UTF-8");
        System.out.println("THIS  server receiver  content "+body);
        String currentTime  ="QUERY TIME".equals(body)?new Date().toString():"bad order";
        ByteBuf  resp =  Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.write(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //异常 释放资源
        ctx.close();
    }
     
}
