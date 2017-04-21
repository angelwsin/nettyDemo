package com.netty.def.proto;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
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

    static ChannelFuture future ;
    
     static{
         
           new Thread(){
               public void run() {
                   try {
                   new Client().connect("localhost",8902); 
                   } catch (Exception e) {
                       e.printStackTrace();
                    }
               };
           }.start();
           
        
     }
    
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
                   ch.pipeline().addLast(new RpcClientHandler());
                }
            });
            
             future =  boot.connect(host, port).sync().addListener(new GenericFutureListener<Future<? super Void>>() {

                public void operationComplete(Future<? super Void> future) throws Exception {
                     if(future.isSuccess()){
                         ClientFuture.latch.countDown();
                     }
                }
            });
           
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
    
    
     class RCPClient extends Thread{
         Channel  channel ;
         
          public RCPClient(Channel channel) {
            this.channel = channel;
        }

        @Override
        public void run() {
            NettyMessage msg = new NettyMessage();
            Head head = new Head();
            head.setType(MessageType.RPC_REQ.getVal());
            msg.setHeads(head);
            final RpcRequest req = new RpcRequest();
            req.setMethodName("say");
            req.setServiceName("rpcService");
            req.setMethodSign(new String[]{String.class.getName()});
            req.setParams(new String[]{"rpc"});
            req.setUniqueId(Long.valueOf(10));
            msg.setBody(req);
            final CallBackPromise clall = new CallBackPromise();
           channel.writeAndFlush(msg).addListener(new GenericFutureListener<Future<? super Void>>() {

            public void operationComplete(Future<? super Void> future) throws Exception {
                 if(future.isSuccess()){
                     RpcClientHandler hand =  channel.pipeline().get(RpcClientHandler.class);
                     Object[] queue = new Object[2];
                     queue[0] = req;
                     queue[1] = clall;
                     hand.requestes.put(req.getUniqueId(), queue);
                 }
            }
            
        });
           try {
            System.out.println(clall.get().getResult());
        } catch (Exception e) {
            e.printStackTrace();
        } 
          
        }
     }
    
}
