package com.netty.optimization;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.TimeUnit;

import com.netty.CommCompont;

public class TimeClient extends CommCompont{

    
      private volatile boolean  isSend = false;
    
    
     public static void main(String[] args) throws Exception{
        new TimeClient().connect("localhost",8902);
    }

    private void connect(final String host, final int port) throws Exception{
        NioEventLoopGroup  bossGroup = new NioEventLoopGroup();
        try{
            Bootstrap  boot = new Bootstrap();
            boot.group(bossGroup)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
          //  .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 100)
            .handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    
                    //解决粘包/拆包
                    packDecod(ch);
                    //没有考虑到半包
                  // ch.pipeline().addLast(new TimeClientHandler());
                    //模拟粘包/拆包
                    ch.pipeline().addLast(new ClientChannelHandler());
                }
            });
            
            ChannelFuture future =  boot.connect(host, port).sync();
            
            //
            //if(!isSend){
            String msg  = "hello world"+System.getProperty("line.separator");
            ByteBuf   buf = Unpooled.copiedBuffer(msg.getBytes());
            future.channel().writeAndFlush(buf);
            isSend = true;
            //}
            future.channel().closeFuture().sync().addListener(new GenericFutureListener<Future<? super Void>>() {

                public void operationComplete(Future<? super Void> future) throws Exception {
                             if(future.isSuccess()){
                                 System.out.println("客户端退出");
                             }
                }
            });
        }finally{
            //优雅的退出
            TimeUnit.SECONDS.sleep(15);
            bossGroup.execute(new Runnable() {
                
                public void run() {
                    try {
                        connect(host, port);
                    } catch (Exception e) {
                       e.printStackTrace();
                    }
                }
            });
        }
    }
    
    
    
}
