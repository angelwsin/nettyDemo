package com.netty.def.proto;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.util.CaseUtils;

public class RpcClientHandler extends ChannelHandlerAdapter{

      public volatile  Map<Long,Object[]> requestes = new ConcurrentHashMap<Long,Object[]>();
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
               NettyMessage nMsg = CaseUtils.caseTo(msg);
               if(nMsg!=null&&nMsg.getBody() instanceof RpcResponse){
                   RpcResponse  resp = CaseUtils.caseTo(nMsg.getBody());
                   handleResult(resp);
               }
    }
    
   
    
    
    
    public void  handleResult(RpcResponse  resp){
         Long  id =  resp.getUniqueId();
         Object[] queue =  requestes.remove(id);
         if(queue==null){
            //不存在 
         }
         CallBackPromise clall = CaseUtils.caseTo(queue[1]);
         clall.setSuccess(resp);
    }
    
  
    
}
