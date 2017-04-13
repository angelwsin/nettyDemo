package com.netty.def.proto;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import com.caucho.hessian.io.HessianInput;

public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder{

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf  buf =  (ByteBuf) super.decode(ctx, in);
        if(buf==null) return null;
        NettyMessage msg = new NettyMessage();
        Head head = new Head();
        head.setCrcCode(buf.readInt());
        head.setLength(buf.readInt());
        head.setSessionID(buf.readLong());
        head.setPriority(buf.readByte());
        head.setType(buf.readByte());
        
        int size = buf.readInt();
        if(size>0){
           Map<String,Object> att = new HashMap<String,Object>();
           byte[] array = null;
           byte[] vale = null;
           for(int i=0;i<size;i++){
               array = new byte[buf.readInt()];
               buf.readBytes(array);
               vale = new byte[buf.readInt()];
               att.put(new String(array,"utf-8"), decode(vale));
           }
           head.setAttach(att);
        }
        
        if(in.readableBytes()>4){
            byte[] val = new byte[in.readableBytes()];
            in.readBytes(val);
            msg.setBody(decode(val));
        }
        return msg;
    }
    
    private   Object decode(byte[] b)throws Exception{
        ByteArrayInputStream is = new ByteArrayInputStream(b);
        HessianInput hi = new HessianInput(is);
        return hi.readObject();
    }
}
