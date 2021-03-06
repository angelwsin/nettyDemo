package com.netty.code;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ServerChannelPreHandler extends ChannelHandlerAdapter{
    
    
    //相当于  注册 selector 
    @Skip
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    public void channelRead(io.netty.channel.ChannelHandlerContext ctx, Object msg)
                                                                                   throws Exception {

        System.out.println("channelRead 可读取 ");
        //推动到到下一个handler
        ctx.fireChannelRead(msg);
    };

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}
