package com.netty.byteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;

public class ByteBufTest {

    
    public static void main(String[] args) {
        
        //java ByteBuffer
        
        // ByteBuffer  的三大属性   limit option capacity
        // 由写到读 要调用 flip()
        ByteBuffer  buf  = ByteBuffer.allocate(1024);
        
        //netty 的 ByteBuf
        ByteBuf  b = Unpooled.buffer(1024);
        b.readBytes("netty".getBytes());
        //读取位置
        b.readerIndex();
        //读完后清理
        b.discardReadBytes();
        //写位置
        b.writerIndex();
        b.markReaderIndex();
        b.resetReaderIndex();
        b.markWriterIndex();
        b.resetWriterIndex();
        
        //ByteBuffer 和 ByteBuf转换
        ByteBuffer buffer =  b.nioBuffer();
        
        
    }
}
