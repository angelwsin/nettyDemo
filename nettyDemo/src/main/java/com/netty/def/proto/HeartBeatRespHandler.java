package com.netty.def.proto;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import com.util.CaseUtils;

public class HeartBeatRespHandler extends ChannelHandlerAdapter{
    
    
    
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       
           NettyMessage nMsg =  CaseUtils.caseTo(msg);
           if(nMsg!=null&&nMsg.getHeads().getType()==MessageType.HEARTBET_REQ.getVal()){
               ctx.writeAndFlush(buidMsg());
           }else{
               ctx.fireChannelRead(msg);
           }
    }
    
    private NettyMessage buidMsg(){
        NettyMessage msg = new NettyMessage();
        Head head = new Head();
        head.setType(MessageType.HEARTBET_RESP.getVal());
        return msg;
    }

}
