package com.netty.def.proto;

import static io.netty.buffer.Unpooled.wrappedBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.caucho.hessian.io.HessianOutput;

public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage>{
    
    private static final byte[] LENGTH_PLACEHODLER  = new byte[4];

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
        }else{
           sendBuf.writeInt(0);
        }
        sendBuf.setIndex(4, sendBuf.readableBytes());
        
    }
    
    private void code(Object msg,ByteBuf buf)throws Exception{
        int pos = buf.writerIndex();
        buf.writeBytes(LENGTH_PLACEHODLER);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput hi = new HessianOutput(os);
        hi.writeObject(msg);
        buf.writeBytes(os.toByteArray());
        buf.setIndex(pos, buf.writerIndex()-pos-4);
    }

}
