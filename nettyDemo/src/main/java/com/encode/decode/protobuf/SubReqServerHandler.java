package com.encode.decode.protobuf;

import com.encode.decode.protobuf.SubReqProto.SubReq;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class SubReqServerHandler  extends  ChannelHandlerAdapter {

	
	             @Override
	            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	            	             SubReqProto.SubReq    req =   (SubReqProto.SubReq) msg;
	            	             System.out.println(req);
	            	             if("xiaoming".equalsIgnoreCase(  req.getUserName())){
	            	            	 ctx.writeAndFlush(resp(req));
	            	             }
	            }
	             
	             
	             private Object resp(SubReq req) {
	            	 SubRespProto.SubResp.Builder  build = SubRespProto.SubResp.newBuilder();
	            	 build.setSubReqID(req.getSubReqID());
	            	 build.setDesc(" order success");
	            	 build.setRespCode("Ok");
	            	 return build.build();
				}


				@Override
	            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	                 ctx.close();
	                 
	            }
}
