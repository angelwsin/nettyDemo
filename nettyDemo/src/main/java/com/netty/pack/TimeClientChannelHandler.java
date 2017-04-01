package com.netty.pack;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;


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
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
         ByteBuf  buf = null;
         for(int i=0;i<100;i++){
             buf = Unpooled.copiedBuffer(firstMsg);
             ctx.writeAndFlush(buf) ;
         }
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                  ByteBuf  buf = (ByteBuf) msg;
                  byte[] b = new byte[buf.readableBytes()];
                  buf.readBytes(b);
                  count.incrementAndGet();
                  System.out.println(new String(b)+",count="+count.get());
    }
    
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
