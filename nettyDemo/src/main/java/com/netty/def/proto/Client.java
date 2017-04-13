package com.netty.def.proto;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class Client{

    
    
    
    
     public static void main(String[] args) throws Exception{
        new Client().connect("localhost",8902);
    }

    private void connect(final String host, final int port) throws Exception{
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
                   ch.pipeline().addLast(new NettyMessageDecoder(1024*1024, 4, 4));
                   ch.pipeline().addLast(new NettyMessageEncoder());
                   ch.pipeline().addLast(new ReadTimeoutHandler(50));
                   ch.pipeline().addLast(new LoginAuthReqHandler());
                   ch.pipeline().addLast(new HeartBeatReqHandler());
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
            //断时重连
            bossGroup.execute(new Runnable() {
                
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                        connect(host, port);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    
    
    
}
