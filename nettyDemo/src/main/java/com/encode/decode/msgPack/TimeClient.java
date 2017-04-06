package com.encode.decode.msgPack;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import com.netty.CommCompont;

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
            .handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    
                    //解决粘包/拆包
                    msgPack(ch);
                    //没有考虑到半包
                  // ch.pipeline().addLast(new TimeClientHandler());
                    //模拟粘包/拆包
                    ch.pipeline().addLast(new MsgPackClientChannelHandler());
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
            bossGroup.shutdownGracefully();
        }
    }
    
    
    
}
