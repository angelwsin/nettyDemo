package com.encode.decode.msgPack;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class MsgPackServerChannelHandler extends ChannelHandlerAdapter{
    
          private AtomicInteger count = new AtomicInteger(0);
         
         public void channelRead(io.netty.channel.ChannelHandlerContext ctx, Object msg) throws Exception {
                 System.out.println("THIS  server receiver  content "+msg+",count="+count.incrementAndGet());
                 String currentTime  ="QUERY TIME".equals(msg)?new Date().toString():"bad order";
                 ctx.writeAndFlush(currentTime);
             
         };
         
         @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        }
         
         @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        }

}
