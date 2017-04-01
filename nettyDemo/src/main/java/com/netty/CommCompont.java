package com.netty;

import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public abstract class CommCompont {
    
    
    
    //netty 自带的编码器解决 粘包/拆包问题
    protected  void   packDecod(SocketChannel ch){
        ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
        ch.pipeline().addLast(new StringDecoder());
    }
    
    
   

}
