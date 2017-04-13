package com.netty.def.proto;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import com.util.CaseUtils;


//握手认证
public class LoginAuthReqHandler extends ChannelHandlerAdapter{
    
      
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(loginReq());
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                NettyMessage nMsg =  CaseUtils.caseTo(msg);
                if(nMsg.getHeads()!=null&&nMsg.getHeads().getType()==MessageType.LOGIN_RESP.getVal()){
                  Byte loginResp =  CaseUtils.caseTo(nMsg.getBody());
                  if(loginResp!=0){
                      //握手失败
                     ctx.close();
                  }else{
                     ctx.fireChannelRead(msg);
                  }
                }else{
                    ctx.fireChannelRead(msg);
                }
    }
    
    public NettyMessage  loginReq(){
         NettyMessage msg = new NettyMessage();
         Head head = new Head();
         head.setType(MessageType.LOGIN_REQ.getVal());
         msg.setHeads(head);
         return msg;
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }

}
