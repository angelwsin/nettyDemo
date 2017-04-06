package com.encode.decode.msgPack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.concurrent.atomic.AtomicInteger;


//模拟 tcp 粘包/拆包
public class MsgPackClientChannelHandler extends ChannelHandlerAdapter{

    private final AtomicInteger count = new AtomicInteger(0);
    
    public MsgPackClientChannelHandler() {
       
   }
    
    public static void main(String[] args) {
       String x  = System.getProperty("line.separator");
       System.out.println((int)x.toCharArray()[1]);
       System.out.println((int)'\r');
    }
    
    //outBound inBound
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
         for(int i=0;i<2;i++){
             ctx.writeAndFlush("QUERY TIME") ;
         }
    }
    
    //idl 闲置
    @Skip
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        
         System.out.println("闲置");
         ctx.fireChannelInactive();
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                  count.incrementAndGet();
                  System.out.println(msg+",count="+count.get());
    }
    
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
    
    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        System.out.println("close");
        super.close(ctx, promise);
    }
    
    
}
