package com.netty.pack;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.concurrent.atomic.AtomicInteger;

import com.util.CaseUtils;


//模拟 tcp 粘包/拆包
public class TimeClientChannelHandler extends ChannelHandlerAdapter{

    private final byte[] firstMsg;
    private final AtomicInteger count = new AtomicInteger(0);
    
    public TimeClientChannelHandler() {
        String   content = "QUERY TIME"+System.getProperty("line.separator");
        firstMsg = content.getBytes();
       
   }
    
    public static void main(String[] args) {
       String x  = System.getProperty("line.separator");
       System.out.println((int)x.toCharArray()[1]);
       System.out.println((int)'\r');
    }
    
    //outBound inBound
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
         ByteBuf  buf = null;
         for(int i=0;i<2;i++){
             buf = Unpooled.copiedBuffer(firstMsg);
             ctx.writeAndFlush(buf) ;
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
                  String x = CaseUtils.caseTo(msg);
                  System.out.println(x+",count="+count.get());
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
