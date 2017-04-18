package com.netty.optimization;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.atomic.AtomicInteger;

public class ClientChannelHandler extends ChannelHandlerAdapter{
    
    
    
       private static final AtomicInteger count = new AtomicInteger(0);
    
    
    
    @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
           
          
        }
    
    
     @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        int x  =  count.incrementAndGet();
        System.out.println(msg);
        if(x==5){
           throw new Exception("第"+x+"中断"); 
        }
        
    }
    
    
    
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
         ctx.close();
         System.out.println("关闭客户端");
         cause.printStackTrace();
    }

}
