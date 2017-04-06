package com.netty;

import com.encode.decode.msgPack.MsgPackDecoder;
import com.encode.decode.msgPack.MsgPackEncoder;

import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public abstract class CommCompont {
    
    
    
    //netty 自带的编码器解决 粘包/拆包问题
    protected  void   packDecod(SocketChannel ch){
        ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
        ch.pipeline().addLast(new StringDecoder());
    }
    
    
    protected void msgPack(SocketChannel ch){
        ch.pipeline().addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2,0,2));
        ch.pipeline().addLast("msgPackDecoder", new MsgPackDecoder());
        ch.pipeline().addLast("LengthFieldPrepender", new LengthFieldPrepender(2));
        ch.pipeline().addLast("MsgPackEncoder", new MsgPackEncoder());
    }
    
    
   

}
