package com.netty.pack;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import com.util.CaseUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeServerChannelHandler extends ChannelHandlerAdapter{
    
          private AtomicInteger count = new AtomicInteger(0);
         
         public void channelRead(io.netty.channel.ChannelHandlerContext ctx, Object msg) throws Exception {
               
                 String  body = CaseUtils.caseTo(msg);
                 System.out.println("THIS  server receiver  content "+body+",count="+count.incrementAndGet());
                 String currentTime  ="QUERY TIME".equals(body)?new Date().toString():"bad order";
                 ByteBuf  resp =  Unpooled.copiedBuffer(((currentTime+System.getProperty("line.separator"))).getBytes());
                 ctx.writeAndFlush(resp);
             
         };
         
         @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        }
         
         @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
        }

}
