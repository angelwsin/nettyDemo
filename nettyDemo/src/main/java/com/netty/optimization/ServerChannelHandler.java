package com.netty.optimization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.util.CaseUtils;

public class ServerChannelHandler extends ChannelHandlerAdapter{

      private static final ConcurrentHashMap<String, List<String>> MESSAGE = new ConcurrentHashMap<String, List<String>>();
    
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
          System.out.println(msg);
          InetSocketAddress address = CaseUtils.caseTo(ctx.channel().remoteAddress());
          System.out.println(address.getHostString());
          if(MESSAGE.containsKey(address.getHostString())){
              final List<String>   msges = MESSAGE.get(address.getHostString());
              final Iterator<String> it = msges.iterator();
               while(it.hasNext()){
                   TimeUnit.MILLISECONDS.sleep(100);
                   ByteBuf  buff =  Unpooled.copiedBuffer(it.next().getBytes());
                   ctx.writeAndFlush(buff).addListener(new GenericFutureListener<Future<? super Void>>() {
                   
                     public void operationComplete(Future<? super Void> future) throws Exception {
                         if(future.cause()!=null){
                             System.out.println("发送失败");
                         }else{
                             it.remove();
                         }
                     }
                 }); 
               }
          }else{
              final List<String> msges = new ArrayList<String>(); 
              MESSAGE.put(address.getHostString(), msges);
              for(int i=0;i<100;i++){
                  TimeUnit.MILLISECONDS.sleep(100);
                  final int x = i;
                  final String ms = i+">>>"+msg.toString()+address.getHostString()+System.getProperty("line.separator");
                  final ByteBuf buf = Unpooled.copiedBuffer(ms.getBytes());
                  ctx.writeAndFlush(buf).addListener(new GenericFutureListener<Future<? super Void>>() {
                  
                    public void operationComplete(Future<? super Void> future) throws Exception {
                          if(future.cause()!=null){
                              System.out.println("发送失败"+x);
                              msges.add(ms);
                          }
                    }
                });
              }
          }
          
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(" close ");
        ctx.close();
        cause.printStackTrace();
    }
}
