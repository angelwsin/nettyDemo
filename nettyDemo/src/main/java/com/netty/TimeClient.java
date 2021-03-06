package com.netty;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import com.netty.pack.TimeClientChannelHandler;

public class TimeClient extends CommCompont{

    
    
    
    
     public static void main(String[] args) throws Exception{
        new TimeClient().connect("localhost",8902);
    }

    private void connect(String host, int port) throws Exception{
        NioEventLoopGroup  bossGroup = new NioEventLoopGroup();
        try{
            Bootstrap  boot = new Bootstrap();
            boot.group(bossGroup)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 100)
            .handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    
                    //解决粘包/拆包
                    packDecod(ch);
                    //没有考虑到半包
                  // ch.pipeline().addLast(new TimeClientHandler());
                    //模拟粘包/拆包
                    ch.pipeline().addLast(new TimeClientChannelHandler());
                }
            });
            
            ChannelFuture future =  boot.connect(host, port).sync();
            
            future.channel().closeFuture().sync().addListener(new GenericFutureListener<Future<? super Void>>() {

                public void operationComplete(Future<? super Void> future) throws Exception {
                             if(future.isSuccess()){
                                 System.out.println("客户端退出");
                             }
                }
            });
        }finally{
            //优雅的退出
            bossGroup.shutdownGracefully(2000, 30000, TimeUnit.DAYS);
        }
    }
    
    
    
}
