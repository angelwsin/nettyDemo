package com.encode.decode.protobuf;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class SubReqClient {
	
	         public static void main(String[] args) throws Exception{
				  new SubReqClient().doConnect("localhost", 8943);
			}
	
	       public void doConnect(String host,int port)throws Exception{
	    	   NioEventLoopGroup   boss = new NioEventLoopGroup();
	    	         try {
	    	        	 
	    	              Bootstrap boot = new Bootstrap();
	    	              boot.group(boss)
	    	              .channel(NioSocketChannel.class)
	    	              .option(ChannelOption.TCP_NODELAY, true)
	    	              .handler(new ChannelInitializer<Channel>() {

							@Override
							protected void initChannel(Channel ch) throws Exception {

								ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
								ch.pipeline().addLast(new ProtobufDecoder(SubRespProto.SubResp.getDefaultInstance()));
								ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
								ch.pipeline().addLast(new ProtobufEncoder());
								ch.pipeline().addLast(new SubReqClientHandler());
							}
						});
	    	              ChannelFuture  future =   boot.connect(host, port).sync();
	    	              future.channel().closeFuture().sync();
	    	              
					} finally {
						boss.shutdownGracefully();
					}
	    	            
	    	   
	       }

}
