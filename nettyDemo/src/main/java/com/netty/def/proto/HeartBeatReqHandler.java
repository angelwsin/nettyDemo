package com.netty.def.proto;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

import com.util.CaseUtils;

public class HeartBeatReqHandler extends ChannelHandlerAdapter {
    
      private ScheduledFuture<?>   heartHeat;
   
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        NettyMessage nMsg =    CaseUtils.caseTo(msg);
                        //定时检测心跳
                        
                        if(nMsg.getHeads()!=null&&nMsg.getHeads().getType()==MessageType.LOGIN_RESP.getVal()){
                            heartHeat=  ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
                        }else if(nMsg.getHeads()!=null&&nMsg.getHeads().getType()==MessageType.HEARTBET_RESP.getVal()){
                            System.out.println(msg);
                        }else{
                            ctx.fireChannelRead(nMsg);
                        }
    }
    
    
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       if(heartHeat!=null){
           heartHeat.cancel(true);
           heartHeat = null;
       }
       ctx.fireExceptionCaught(cause);
    }
    
    class HeartBeatTask implements Runnable{

        ChannelHandlerContext ctx;
        public HeartBeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }
        public void run() {
            
            NettyMessage msg = new NettyMessage();
            Head head = new Head();
            head.setType(MessageType.HEARTBET_REQ.getVal());
            msg.setHeads(head);
            ctx.writeAndFlush(msg);
        }
        
        
    }
    
    
    

}
