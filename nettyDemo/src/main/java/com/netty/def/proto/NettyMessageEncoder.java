package com.netty.def.proto;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map.Entry;

import com.caucho.hessian.io.HessianOutput;

public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage>{
    

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, List<Object> out)
                                                                                        throws Exception {
        
        ByteBuf  sendBuf = Unpooled.buffer();
        sendBuf.writeInt(msg.getHeads().getCrcCode());
        sendBuf.writeInt(msg.getHeads().getLength());
        sendBuf.writeLong(msg.getHeads().getSessionID());
        sendBuf.writeByte(msg.getHeads().getPriority());
        sendBuf.writeByte(msg.getHeads().getType());
        sendBuf.writeInt(msg.getHeads().getAttach().size());
        
        byte[] keyByte = null;
        for(Entry<String, Object> entry :msg.getHeads().getAttach().entrySet()){
             keyByte  = entry.getKey().getBytes("utf-8");
             sendBuf.writeInt(keyByte.length);
             sendBuf.writeBytes(keyByte);
             code(entry.getValue(), sendBuf);
        }
        
        if(msg.getBody()!=null){
            code(msg.getBody(),sendBuf);
        }
        sendBuf.setInt(4, sendBuf.readableBytes()-8);
        out.add(sendBuf);
    }
    
    private void code(Object msg,ByteBuf buf)throws Exception{
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput hi = new HessianOutput(os);
        hi.writeObject(msg);
        buf.writeBytes(os.toByteArray());
    }

}
