package com.netty.ssl;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

import com.netty.CommCompont;

public class SSLClient extends CommCompont{

    private static  SSLEngine    sslEngine = null;
    
    public static void main(String[] args) throws Exception{
        //client 单向认证
        // sslEngine = ssl("cChat.jks", "cNetty",SLLSecurety.Client);
        //双向认证
        sslEngine = ssl("cChat.jks", "cNetty",SLLSecurety.Client_Server);
         sslEngine.setUseClientMode(true);
         new SSLClient().connect("localhost", 8978);
         
    }
    
    
    public void connect(String host,int port)throws Exception{
         NioEventLoopGroup  worker = new NioEventLoopGroup();
         
         try{
            Bootstrap boot = new Bootstrap();
            boot.group(worker)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, false)
            .handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new SslHandler(sslEngine));
                    packDecod(ch);
                }
            });
            ChannelFuture future  =  boot.connect(host, port).sync();
            ByteBuf  buf  = Unpooled.copiedBuffer(("netty ssl"+System.getProperty("line.separator")).getBytes());
            future.channel().writeAndFlush(buf);
            future.channel().closeFuture().sync();
         }finally{
             worker.shutdownGracefully();
         }
    }
}
