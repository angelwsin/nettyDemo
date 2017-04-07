package com.encode.decode.msgPack;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import com.netty.CommCompont;

public class TimeServer extends CommCompont{
	
	
    public static void main(String[] args)throws Exception {
        new TimeServer().bind(8902);
    }
	 public void bind(int port)throws Exception{
		  //nio线程组  reactor(反应器)线程模型    1.单线程  2.多线程 3.主从多线程
		  NioEventLoopGroup bossGroup = new NioEventLoopGroup();
		  NioEventLoopGroup workGroup = new NioEventLoopGroup();
		  
		  try {
			  ServerBootstrap  boot = new ServerBootstrap();
			  boot.group(bossGroup, workGroup)
			  .channel(NioServerSocketChannel.class)
			  //tcp缓冲区
			  .option(ChannelOption.SO_BACKLOG, 1024)
			  .handler(new LoggingHandler(LogLevel.DEBUG))
			  .childHandler(new ChildChannelHandler());
			  
			  //绑定端口 同步等待
			  ChannelFuture future =   boot.bind(port).sync();
			  //等待服务端 端口关闭
			  future.channel().closeFuture().sync().addListener(new GenericFutureListener<Future<? super Void>>() {

                public void operationComplete(Future<? super Void> future) throws Exception {
                            if(future.isSuccess()){
                                System.out.println("服务器停止");
                            }
                }
            });
		}finally {
			//优雅的关闭
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
		 
	 }
	 
	 class  ChildChannelHandler extends   ChannelInitializer<SocketChannel>{

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
		    
		    //解决粘包/拆包
		    msgPack(ch);
		    //没有考虑半包的问题
           //ch.pipeline().addLast(new TimeServerHandler());
		  //模拟粘包/拆包
            ch.pipeline().addLast(new MsgPackServerChannelHandler());
		}

		 
	 }

}
