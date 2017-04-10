package com.encode.decode.protobuf;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class SubReqServer {
	
	
	      public static void main(String[] args) throws Exception{
	    	 new  SubReqServer().bind("localhost", 8943);
		}
	
	
	
	 public void bind(String host,int port)throws Exception{
		        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
		        NioEventLoopGroup workGroup =  new NioEventLoopGroup();
		        
		        try{
		        	ServerBootstrap  boot = new ServerBootstrap();
		        	boot.group(bossGroup, workGroup)
		        	.channel(NioServerSocketChannel.class)
		        	.option(ChannelOption.SO_BACKLOG, 100)
		        	.handler(new LoggingHandler(LogLevel.INFO))
		        	.childHandler(new SubChildHandler());
		        	
		        	ChannelFuture future = boot.bind(host, port).sync();
		        	future.channel().closeFuture().sync();
		        }finally{
		        	bossGroup.shutdownGracefully();
		        	workGroup.shutdownGracefully();
		        }
	 }
	 
	 
	 class SubChildHandler  extends ChannelInitializer<SocketChannel>{

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			
			ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
			ch.pipeline().addLast(new ProtobufDecoder(SubReqProto.SubReq.getDefaultInstance()));
			ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
			ch.pipeline().addLast(new ProtobufEncoder());
			ch.pipeline().addLast(new SubReqServerHandler());
			
		}
		 
	 }

}
