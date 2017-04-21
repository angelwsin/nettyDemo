package com.netty.def.proto;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProxyInvocationHandler implements InvocationHandler{
    
    ChannelFuture future ;
    
    

    public ProxyInvocationHandler(ChannelFuture future) {
        this.future = future;
    }



    public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
                                                                          throws Throwable {
        
        NettyMessage msg = new NettyMessage();
        Head head = new Head();
        head.setType(MessageType.RPC_REQ.getVal());
        msg.setHeads(head);
        final RpcRequest req = new RpcRequest();
        req.setMethodName(paramMethod.getName());
        req.setServiceName("rpcService");
        req.setMethodSign(new String[]{String.class.getName()});
        req.setParams(paramArrayOfObject);
        req.setUniqueId(Long.valueOf(10));
        msg.setBody(req);
        final CallBackPromise clall = new CallBackPromise();
        final Channel ch =  future.channel();
        ch.writeAndFlush(msg).addListener(new GenericFutureListener<Future<? super Void>>() {

         public void operationComplete(Future<? super Void> future) throws Exception {
              if(future.isSuccess()){
                  RpcClientHandler hand =  ch.pipeline().get(RpcClientHandler.class);
                  Object[] queue = new Object[2];
                  queue[0] = req;
                  queue[1] = clall;
                  hand.requestes.put(req.getUniqueId(), queue);
              }
         }
         
     });
        return clall.get().getResult();
    }

}
