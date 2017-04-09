package com.encode.decode.protobuf;

import java.util.ArrayList;
import java.util.List;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class SubReqClientHandler  extends  ChannelHandlerAdapter {
	
	
	
	    @Override
	    public void channelActive(ChannelHandlerContext ctx) throws Exception {
	    	          SubReqProto.SubReq.Builder build = SubReqProto.SubReq.newBuilder();
	    	          build.setSubReqID(90490343);
	    	          build.setUserName("xiaoming");
	    	          build.setProductName("netty");
	    	          List<String> address = new ArrayList<String>();
	    	          address.add("hangzhou");
	    	          build.addAllAddress(address);
	    	          ctx.writeAndFlush(build.build());
	    }
	    
	    @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	    	               SubRespProto.SubResp    resp = (SubRespProto.SubResp)  msg;
	    	                System.out.println(resp);
	    }
	    
	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	    	ctx.close();
	    }

}
