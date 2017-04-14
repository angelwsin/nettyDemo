package com.netty.def.proto;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import com.util.CaseUtils;

public class LoginAuthRespHandler extends ChannelHandlerAdapter{
    
       private final ConcurrentHashMap<String, Boolean> nodeCheck = new ConcurrentHashMap<String, Boolean>();
       private final String[]        witheList = new String[]{"127.0.0.1","10.63.37.38"};
    
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
             NettyMessage nMsg = CaseUtils.caseTo(msg);
             System.out.println(nMsg);
             if(nMsg.getHeads()!=null&&nMsg.getHeads().getType()==MessageType.LOGIN_REQ.getVal()){
                 InetSocketAddress addres = CaseUtils.caseTo(ctx.channel().remoteAddress());
                 //重复登录
                 NettyMessage respMsg = null;
                 if(nodeCheck.containsKey(addres.toString())){
                     respMsg = buidResp((byte)-1);
                 }else{
                    String ip =   addres.getAddress().getHostAddress();
                    boolean isOk = false;
                    for(String withe : witheList){
                        if(withe.equals(ip)){
                           isOk = true;
                           break;
                        }
                    }
                    respMsg = buidResp(isOk?(byte)0:(byte)-1);
                    if(isOk){
                        nodeCheck.put(addres.toString(), true);
                    }
                    ctx.writeAndFlush(respMsg);
                 }
             }else{
                 ctx.fireChannelRead(msg);
             }
    }
    
    
    private NettyMessage buidResp(byte resp){
        NettyMessage msg = new NettyMessage();
        Head head = new Head();
        head.setType(MessageType.LOGIN_RESP.getVal());
        msg.setHeads(head);
        msg.setBody(resp);
        return msg;
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       ctx.close();
       ctx.fireExceptionCaught(cause);
       cause.printStackTrace();
    }

}
