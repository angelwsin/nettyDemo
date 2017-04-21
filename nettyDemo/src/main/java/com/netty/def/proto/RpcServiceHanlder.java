package com.netty.def.proto;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.util.CaseUtils;

public class RpcServiceHanlder extends ChannelHandlerAdapter{

     private static final  Map<String,Object> SERVICES =  new HashMap<String, Object>();
     
     static{
         SERVICES.put("rpcService", new RpcServiceImpl());
     }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
              NettyMessage nMsg = CaseUtils.caseTo(msg);
              System.out.println(msg);
              if(nMsg.getBody() instanceof RpcRequest){
                  RpcRequest req = CaseUtils.caseTo(nMsg.getBody());
                 Object obj =  SERVICES.get(req.getServiceName());
                 RpcResponse resp = new RpcResponse();
                 NettyMessage mResp = new NettyMessage();
                 Head head = new Head();
                 head.setType(MessageType.RPC_RESP.getVal());
                 mResp.setHeads(head);
                 resp.setUniqueId(req.getUniqueId());
                 if(obj==null){
                     resp.setResult(new RpcException("服务不存在"));
                     mResp.setBody(resp);
                     ctx.writeAndFlush(mResp);
                     return;
                 }
                 Class<?>[] signes = null;
                 if(req.getMethodSign()!=null){
                     signes =  new Class<?>[req.getMethodSign().length];
                     for(int i=0;i<req.getMethodSign().length;i++){
                         signes[i]=Class.forName(req.getMethodSign()[i]);
                     }
                 }
                 Method method =  obj.getClass().getMethod(req.getMethodName(),signes==null?null:signes);
                 if(method==null){
                     throw new Exception("没有此方法"+req.getMethodName());
                 }
                
                 try {
                  Object result = method.invoke(obj, req.getParams()); 
                  resp.setResult(result);
                }catch (Exception e) {
                    resp.setResult(e);
                }catch(Throwable a){
                    resp.setResult(a);
                }finally{
                 mResp.setBody(resp);
                 ctx.writeAndFlush(mResp);
                }
                
              }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }
}
