package com.encode.decode.msgPack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

import org.msgpack.MessagePack;

public class MsgPackDecoder extends MessageToMessageDecoder<ByteBuf>{

    // msg 要解码的数据   out 要解码后的对象的数组
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out)
                                                                                   throws Exception {
        int  length = msg.readableBytes();
        byte[]  dst = new byte[length];
        msg.getBytes(msg.readerIndex(), dst, 0, length);
        MessagePack pack = new MessagePack();
        out.add(pack.read(dst));
    }

}
