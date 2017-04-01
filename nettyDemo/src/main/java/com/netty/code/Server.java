package com.netty.code;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import com.netty.TimeServer;
import com.netty.pack.TimeServerChannelHandler;

public class Server {
    
    
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
              .channel(NioServerSocketChannel.class) //利用反射生成指定的 channel
              //tcp缓冲区
              .option(ChannelOption.SO_BACKLOG, 1024)//channel的参数设置
              .childHandler(new ChildChannelHandler());
              
              //绑定端口 同步等待
              //在 bind 调用时 注册三个 handler
              //头handler HeadHandler  完成  nio  ServerSocketChannel.open()
              //第二个 ServerBootstrapAcceptor 中注册的handler childHandler
              //ServerBootstrapAcceptor 使命就是在服务端启动后 注册自定的 handler
              //第三个 尾 TailHandler
              ChannelFuture future =   boot.bind(port).sync();
              //等待服务端 端口关闭
              //最终的 调用的委托给 Unsafe
              future.channel().closeFuture().sync();
        }finally {
            //优雅的关闭
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
         
     }
     
     class  ChildChannelHandler extends   ChannelInitializer<SocketChannel>{

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            
            ChannelPipeline pipeline = ch.pipeline(); //channel中的管道 
            //管道 以链表的方式 组织 ChannelHandler
            //ChannelHandler 被封装在 DefaultChannelHandlerContext 中
          //DefaultChannelHandlerContext  会对注册的ChannelHandler 进行分组
            /**
             * 分组规则
             * DefaultChannelHandlerContext  定义了16中操作 分别对照ChannelHandler的方法
             * 根据ChannelHandler的实现类实现的方法给出 skipFlags 的标识 
             * skipFlags 的标识  的计算 实现了方法就进行  | 按位或操作
             * 
             */
            pipeline.addLast(new TimeServerChannelHandler());
            
             

        }

         
     }

}
