package com.netty.ssl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

import com.netty.CommCompont;

//ssl/tls
public class SSLServer extends CommCompont{
    
     private static  SSLEngine    sslEngine = null;
    
    
    public static void main(String[] args)throws Exception {
        //sslEngine =  ssl("sChat.jks","sNetty",SLLSecurety.Server);
        //双向认证
        sslEngine =  ssl("sChat.jks","sNetty",SLLSecurety.Client_Server);
        sslEngine.setUseClientMode(false);
        sslEngine.setNeedClientAuth(true);
             new SSLServer().bind("localhost", 8978);
    }
    
    
    
    public void bind(String host,int port) throws Exception{
         NioEventLoopGroup  boss = new NioEventLoopGroup();
         NioEventLoopGroup  worker = new NioEventLoopGroup();
         
         try{
             ServerBootstrap boot = new ServerBootstrap();
             boot.group(boss, worker)
             .channel(NioServerSocketChannel.class)
             .option(ChannelOption.SO_BACKLOG, 1024)
             .handler(new LoggingHandler(LogLevel.DEBUG))
             .childHandler(new ChildHandler());
             
            ChannelFuture  future =   boot.bind(host, port).sync();
            
            future.channel().closeFuture().sync();
             
             
         }finally{
             boss.shutdownGracefully();
             worker.shutdownGracefully();
         }
    }
    
    
    
    class ChildHandler  extends  ChannelInitializer<SocketChannel>{

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new SslHandler(sslEngine));
            packDecod(ch);
            ch.pipeline().addLast(new ChannelHandlerAdapter(){
                
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    System.out.println(msg);
                }
                
                @Override
                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                                                                                       throws Exception {
                    cause.printStackTrace();
                }
            });
        }
        
    }
    
    
    
    
    
   

}
