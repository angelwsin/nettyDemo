package com.encode.decode.msgPack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import org.msgpack.MessagePack;

public class MsgPackEncoder extends MessageToByteEncoder<Object>{

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        MessagePack pack = new MessagePack();
        try {
            byte[] b =pack.write(msg);
            out.writeBytes(b);  
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        
    }

}
