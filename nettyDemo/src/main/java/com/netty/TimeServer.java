package com.netty;

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

import com.netty.pack.TimeServerChannelHandler;

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
			  //backlog 内核为此套接口排队的最大连接数
			  //给定的监听端口 内核维护两个队列 未连接队列和已连接队列
			  //划分的已经根据三次握手  当第一次握手创建放在 未连接队列 直到三次握手完成从未连接队列
			  //移动到已连接队列的队尾  当进程调用 accept 从已连接的对头移除一个给进程
			  //当已连接的队列为空 进程则睡眠 直到已连接队列有值 则唤醒
			  .option(ChannelOption.SO_BACKLOG, 1024)
			  .handler(new LoggingHandler(LogLevel.DEBUG))
			  .childHandler(new ChildChannelHandler());
			  
			  //绑定端口 同步等待   sync 调用 wait()
			  ChannelFuture future =   boot.bind(port).sync();
			  
			  //等待服务端 端口关闭        sync 调用 wait() 等待 I/O线程通知  注意不要在handler中使用 会死锁
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
            packDecod(ch);
		    //没有考虑半包的问题
           //ch.pipeline().addLast(new TimeServerHandler());
		  //模拟粘包/拆包
            ch.pipeline().addLast(new TimeServerChannelHandler());
		}

		 
	 }
	 
	 /*
	  * netty server 启动步骤  
	  * 1.利用ServerBootstrap 的build模式设置创建  socketServer 的参数 
	  *   主要的 reator线程组,创建的channel类型,创建channel的参数设置
	  * 2.ServerBootstrap 调用bind方法启动服务端
	  *  1)AbstractBootstrap.initAndRegister() 初始化channel并完成 channelRegistered 事件
	  *    步骤
	  *     >ServerBootstrap.createChannel() 根据channel 参数利用工厂模式反射创建
	  *      NioServerSocketChannel, 并从group( group().next()) 中取出一个eventLoop
	  *      和childGroup 一并设置到NioServerSocketChannel中
	  *      NioServerSocketChannel创建初始化的信息：
	  *      (1)ch =  ServerSocketChannel的创建(底层nio的使用)
	  *      (2)config =  DefaultServerSocketChannelConfig(承载channel的Option配置信息)
	  *      (3)eventLoop,childGroup  reactor线程模型的主从模型
	  *      (4)readInterestOp 感兴趣的事件SelectionKey.OP_ACCEPT
	  *      (5)parent 为空(如果是NioSocketChannel的话 就是NioServerSocketChannel)
	  *      (6)unsafe = NioMessageUnsafe(底层的一些操作委托)
	  *      (7)pipeline =  DefaultChannelPipeline(维持着处理事件的handler队列)
	  *     >ServerBootstrap.init(Channel) 初始化操作
	  *     (1)channel.config().setOptions(options); 设置options
	  *     (2)channel.pipeline().addLast(handler()) 向pipeline添加handler
	  *     (3)channel.pipeline().addLast(ChannelInitializer)当channelRegistered事件触发时调用
	  *        然后才向pipeline添加handle=ServerBootstrapAcceptor,ServerBootstrapAcceptor用来处理
	  *        NioSocketChannel的相关操作如childHandler的添加
	  *     >AbstractChannel.AbstractUnsafe.register(ChannelPromise)channel注册到Selector完成
	  *      (1)AbstractChannel.doRegister() 完成javaChannel().register(channel注册到Selector完成)
	  *         pipeline.fireChannelRegistered() 触发channelRegistered事件把
	  *         ServerBootstrapAcceptor添加到pipeline中
	  *     >AbstractBootstrap.doBind0(ChannelFuture, Channel, SocketAddress, ChannelPromise)
	  *      (1)AbstractChannel.bind(SocketAddress, ChannelPromise)触发bind事件
	  *         调用HeadHandler.bind(ChannelHandlerContext, SocketAddress, ChannelPromise)
	  *      (2)NioServerSocketChannel.doBind(SocketAddress)
	  *        javaChannel().socket().bind(localAddress, config.getBacklog()) 完成bind
	  *      (3)NioServerSocketChannel.isActive()
	  *      javaChannel().socket().isBound()   检查是否bind
	  *      (4)如果bind触发pipeline.fireChannelActive()
	  *      (5)channel.config().isAutoRead()===>channel.read()===>HeadHandler.read(ChannelHandlerContext)
	  *      (6)AbstractNioChannel.doBeginRead() 注册感兴趣的事件
	  *     >NioEventLoop.run()  线程监听事件并处理
	  *    3.NioEventLoop.run() 线程处理任务
	  *    1)socket I/O 任务
	  *      >SelectionKey.OP_ACCEPT  NioMessageUnsafe.read()==>
	  *       NioServerSocketChannel.doReadMessages(List<Object>){
	  *       SocketChannel ch = javaChannel().accept()
	  *       buf.add(new NioSocketChannel(this, childEventLoopGroup().next(), ch)) 构造接入的NioSocketChannel
	  *       pipeline.fireChannelRead(readBuf.get(i))}===>
	  *       ServerBootstrapAcceptor.channelRead(ChannelHandlerContext ctx, Object msg){
	  *       child.pipeline().addLast(childHandler)  添加childHandler
	  *       child.unsafe().register(child.newPromise())}===>
	  *       pipeline.fireChannelRegistered()
	  *       NioSocketChannel.isActive(){ch.isOpen() && ch.isConnected()}===>
	  *       pipeline.fireChannelActive(){channel.read()}
	  *       za
	  *       
	  *    
	  *    
	  *    
	  *    2)ScheduledFutureTask 定时任务
	  *    3)NioTask
	  *    
	  *    4.netty中的事件分为inbound和outbound
	  *     1)inbound 通常i/o线程触发
	  *     ChannelHandlerContext.fireChannelRegistered()  对应 channel注册到selector
	  *     ChannelHandlerContext.fireChannelActive() tcp链路建立成功 分socket(connect)和serversocket(bind)
	  *     ChannelHandlerContext.fireChannelRead(Object msg) 读事件   
	  *     ChannelHandlerContext.fireChannelReadComplete()  读操作完成
	  *     ChannelHandlerContext.fireExceptionCaught(Throwable cause) 异常事件
	  *     ChannelHandlerContext.fireUserEventTriggered(Object event) 自定义事件
	  *     ChannelHandlerContext fireChannelWritabilityChanged() channel的可写状态变化通知事件
	  *     ChannelHandlerContext.fireChannelInactive() tcp连接关闭，连接不可用事件
	  *     
	  *     
	  *     2)outbound 通常用户主动发出的i/o操作
	  *     ChannelHandlerContext.bind(SocketAddress localAddress) 绑定本地地址serversocket(bind)
	  *     ChannelHandlerContext.connect(SocketAddress remoteAddress) 客户端连接服务端事件socket(connect)
	  *     ChannelHandlerContext.write(Object msg, ChannelPromise promise); 发送事件
	  *     ChannelHandlerContext.flush() 刷新
	  *     ChannelHandlerContext.read()  读事件
	  *     ChannelHandlerContext.disconnect(ChannelPromise promise) 断开连接事件
	  *     ChannelHandlerContext.close(ChannelPromise promise) 关闭当前channel
	  *     
	  *    5.拆包和组包（半包问题）cumulation 记录半包读
	  *     解决
	  *     1）固定长度
	  *     2）回车换行分割 LineBasedFrameDecoder
	  *     3）分割符区分整包消息 DelimiterBasedFrameDecoder
	  *     4）指定长度标识整包消息 LengthFieldBasedFrameDecoder ，LengthFieldPrepender(对应 第一个字段为长度的，自动填充）
	  *    6.handler
	  *    1)系统  HeadHandler
	  *    2)编码  ByteToMessageDecoder，MessageToMessageCodec，MessageToMessageEncoder
	  *    3)其他系统  流量整型handler,读写超时handler,日志handler(LoggingHandler) 
	  *    7.ChannelFuture Promise   异步添加 listenner
	  *    
	  *    ChannelFuture  (解决nio的异步） 开始一个I/O操作ChannelFuture
	  *    1)有两种状态 
	  *     uncompleted  非失败 非成功 非取消
	  *     completed     失败      成功    取消
	  *    异步I/O超时：1.tcp层面的超时 2.业务层面的超时 
	  *    
	  *    uncompleted             completed
	  *    isDone = false          失败 isDone = true  cause not null
	  *    isSuccess =false         成功isDone = true  isSuccess =true
	  *    isCancelled =false       取消isDone = true  isCancelled =true
	  *    cause  = null
	  *    
	  *    7.心跳检测
	  *    1)ping-pong 请求响应
	  *    2)ping-ping型  双向心跳检测
	  *    netty 提供三种检测
	  *    >读空闲  ReadTimeoutHandler
	  *    >写空闲  WriteTimeoutHandler
	  *    >读写空闲检测 IdleStateHandler
	  *    8.ChannelHandlerContext.flush() 半包的处理
	  *    ChannelOutboundBuffer 循环写
	  *    // A circular buffer used to store messages.  The buffer is arranged such that:  flushed <= unflushed <= tail.  The
      *    // flushed messages are stored in the range [flushed, unflushed).  Unflushed messages are stored in the range
      *    // [unflushed, tail)
      *    
      *     当在写时 网络出现异常    服务端会清除所有未发送的消息
      *     try {
                doWrite(outboundBuffer);
            } catch (Throwable t) {
                outboundBuffer.failFlushed(t); //remove()
            } finally {
                inFlush0 = false;
            }
	  *     
	  *      
	  *      
	  *            ServerBootstrap.initAndRegister()           AbstractChannel.doRegister()
	  *      ServerBootstrap-----NioServerSocketChannel-----------------------------------pipeline.fireChannelRegistered()
	  *      
	  *      AbstractBootstrap.doBind0                  NioServerSocketChannel.isActive()
	  *      --------------------------HeadHandler.bind()------------------------------------pipeline.fireChannelActive()
	  *      
	  *      channel.read()                              AbstractNioChannel.doBeginRead()注册感兴趣的事件SelectionKey.OP_ACCEPT
	  *      ----------------------HeadHandler.read(ChannelHandlerContext)-------------------selectionKey.interestOps(interestOps | readInterestOp)
	  *      
	  *      
	  *      NioEventLoop.run()                     SelectionKey.OP_ACCEPT
	  *                                              javaChannel().accept()
	  *      ------------------------线程监听事件并处理--------------------------NioSocketChannel(this, childEventLoopGroup().next(), ch)
	  *      
	  *      ServerBootstrapAcceptor.channelRead(ChannelHandlerContext ctx, Object msg)
	  *      -------------------------------------------------------------------------------pipeline.fireChannelRegistered()
	  *      
	  */    
	 
	
	  
	  

}
