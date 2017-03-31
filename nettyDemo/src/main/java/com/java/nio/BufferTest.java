package com.java.nio;

import java.nio.ByteBuffer;

public class BufferTest {
    
    public static void main(String[] args) {
        //buffer的创建
        
       ByteBuffer buffer =  ByteBuffer.allocate(1024);
       //buffer写入数据   可以从channel中写入
       buffer.put("hello world".getBytes());
       //写模式切换到读模式
       buffer.flip();
       byte[]  b  = new byte[buffer.limit()];
       buffer.get(b);
       System.out.println(new String(b).toString());
       buffer.clear();
    }

}
