package com.netty.def.proto;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.SynchronousQueue;

import com.util.CaseUtils;

public class RpcClientHandler extends ChannelHandlerAdapter{

      private SynchronousQueue<RpcResponse>  queue = new SynchronousQueue<RpcResponse>();
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
               NettyMessage nMsg = CaseUtils.caseTo(msg);
               if(nMsg!=null&&nMsg.getBody() instanceof RpcResponse){
                   RpcResponse  resp = CaseUtils.caseTo(nMsg.getBody());
                   if(resp.getResult() instanceof RpcException){
                       RpcException e = CaseUtils.caseTo(resp.getResult());
                       throw e;
                   }else{
                       queue.put(resp);
                       System.out.println(resp.getResult());
                   }
               }
    }
    
    public RpcResponse getResult(){
       
         try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         return null;
    }
    
    public static void main(String[] args) throws Exception{
        RpcClientHandler h = new RpcClientHandler();
        h.queue.take();
        System.out.println("pp");
    }
    
}
