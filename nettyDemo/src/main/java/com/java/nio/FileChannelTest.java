package com.java.nio;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class FileChannelTest {
    
    
    //文件channel的事例
    public static void main(String[] args)throws Exception {
        RandomAccessFile file = new RandomAccessFile(System.getProperty("user.dir")+File.separator+"jvisualvm", "rw");
        FileChannel channel = file.getChannel();
        
        //创建缓存
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while(channel.read(buffer)!=-1){
            buffer.flip();
            while(buffer.hasRemaining()){
                System.out.println(new String(buffer.array(), "utf-8").toString());
            }
            buffer.clear();
        }
        file.close();
        
    }

}
