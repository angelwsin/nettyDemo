package com.netty.code;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import com.netty.TimeServerHandler;

public class Server {
    
    public static void main(String[] args)throws Exception {
        new Server().bind(8902);
    }
     public void bind(int port)throws Exception{
          //nio线程组  reactor(反应器)线程模型    1.单线程  2.多线程 3.主从多线程
         
         //1.初始化线程调到器  netty 实现Executor 自定义执行线程器 ThreadPerTaskExecutor
         //2.初始化事件执行器的对象池EventExecutor,EventLoop  EventLoop的实现者 NioEventLoop  负责 I/O的操作
         //每一个 NioEventLoop 维持一个任务队列          任务的管理由SingleThreadEventExecutor实现         NioEventLoop单线程的
         //每个NioEventLoop 对应一个Selector  把要监听的 channel 绑定到 NioEventLoop线程中去  
         //EventLoopGroup  管理EventLoop的对象池    策略递增的索引%对象池  hash
          NioEventLoopGroup bossGroup = new NioEventLoopGroup();
          NioEventLoopGroup workGroup = new NioEventLoopGroup();
          
          try {
              ServerBootstrap  boot = new ServerBootstrap();
              boot.group(bossGroup, workGroup)
              .channel(NioServerSocketChannel.class) //利用反射生成指定的 channel
              //tcp缓冲区
              .option(ChannelOption.SO_BACKLOG, 1024)//channel的参数设置
              //ServerBootstrapAcceptor 触发 ChildChannelHandler 的调用
              .childHandler(new ChildChannelHandler());
              
              //绑定端口 同步等待
              //在 bind 调用时 注册三个 handler
              //头handler HeadHandler  完成  nio  ServerSocketChannel.open()
              //第二个 ServerBootstrapAcceptor 中注册的handler childHandler
              // io.netty.channel.socket.nio.NioServerSocketChannel.doReadMessages(List<Object>) 触发
              //ServerBootstrapAcceptor 在客户端连接第一个调用  把客户端的channel 等参数传递给childHandler
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
            
            //  ChannelPipeline pipeline = ch.pipeline(); channel中的管道 
            //管道 以链表的方式 组织 ChannelHandler
            //ChannelHandler 被封装在 DefaultChannelHandlerContext 中
          //DefaultChannelHandlerContext  会对注册的ChannelHandler 进行分组
            //ChannelPipeline 分两种 一种对server的处理，另一种是Server接受client acceptor 的处理
            //ChannelPipeline client端的和server模式一样
            /**
             * 分组规则
             * DefaultChannelHandlerContext  定义了16中操作 分别对照ChannelHandler的方法
             * 根据ChannelHandler的实现类实现的方法给出 skipFlags 的标识 
             * skipFlags 的标识  的计算 实现了方法就进行  | 按位或操作
             * 
             * 当 client的连接 添加handler到 client端的管道中
             * NioEventLoop  中 run 监听事件
             */
            ch.pipeline().addLast(new ServerChannelPreHandler());
            ch.pipeline().addLast(new TimeServerHandler());
            
             

        }

         
     }

}
