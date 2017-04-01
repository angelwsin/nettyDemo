package com.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;

public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer>{
    
     private AsynchronousSocketChannel   socketChannel;
     
     

    public ReadCompletionHandler(AsynchronousSocketChannel socketChannel) {
        super();
        this.socketChannel = socketChannel;
    }

    public void completed(Integer result, ByteBuffer attachment) {
        ByteBuffer  buffer = (ByteBuffer) attachment;
        buffer.flip();
        byte[]  b = new byte[buffer.remaining()];
        buffer.get(b);
        String body = new String(b);
        System.out.println("THIS  server receiver  content "+body);
        String currentTime  ="QUERY TIME".equals(body)?new Date().toString():"bad order";
        try {
            doWriter(socketChannel, currentTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
  //写时 tcp缓存区满了 可能出现半包问题  要注册写事件 循环写
    private void  doWriter(final AsynchronousSocketChannel sc ,String response)throws IOException{
            if(response!=null&&response.trim().length()>0){
                System.out.println(response);
                byte[] bytes = response.getBytes();
                ByteBuffer  writerBuffer=ByteBuffer.allocate(bytes.length);
                writerBuffer.put(bytes);
                writerBuffer.flip();
                sc.write(writerBuffer, writerBuffer, new CompletionHandler<Integer, Object>() {

                    public void completed(Integer result, Object attachment) {
                        ByteBuffer  buffer = (ByteBuffer) attachment;
                        if(buffer.hasRemaining()){
                            sc.write(buffer,buffer,this);
                        }
                    }

                    public void failed(Throwable exc, Object attachment) {
                    }
                });
                
            }
    }
    

}
