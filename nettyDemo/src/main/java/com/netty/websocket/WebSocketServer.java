package com.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer {
    
    public static void main(String[] args)throws Exception {
        new   WebSocketServer().bind("localhost", 8080);
     }
     
     
     
     
      public void bind(String host,int port)throws Exception{
          NioEventLoopGroup  bossGroup = new NioEventLoopGroup();
          NioEventLoopGroup  workGroup = new NioEventLoopGroup();
          try{
              ServerBootstrap boot = new ServerBootstrap();
              boot.group(bossGroup, workGroup)
              .channel(NioServerSocketChannel.class)
              .handler(new LoggingHandler(LogLevel.INFO))
              .childHandler(new ChannelInitializer<Channel>() {

                 @Override
                 protected void initChannel(Channel ch) throws Exception {
                     ch.pipeline().addLast("http-codec",new HttpServerCodec());
                     ch.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65535));
                     ch.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
                     ch.pipeline().addLast(new WebSocketServerHandler());
                 }
             });
              ChannelFuture  future =  boot.bind(host, port).sync();
              future.channel().closeFuture().sync();
          }finally{
              bossGroup.shutdownGracefully();
              workGroup.shutdownGracefully();
              
          }
      }

}
