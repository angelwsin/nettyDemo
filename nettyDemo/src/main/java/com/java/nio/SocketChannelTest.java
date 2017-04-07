package com.java.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SocketChannelTest {
    
    
    public static void main(String[] args)throws Exception {
       Selector selector =  Selector.open();
       ServerSocketChannel server = ServerSocketChannel.open();
     //selector 和 channel组合使用必须是非阻塞
       server.configureBlocking(false);
       server.socket().bind(new InetSocketAddress(8493));
       //selector注册感兴趣的事件
      server.register(selector,0);
       
       while(true){
         //非阻塞 立即返回 可能为null
         //阻塞等待 客户端连接
           SocketChannel  clientChannel =  server.accept();
           System.out.println("accept,"+clientChannel);
         if(clientChannel!=null){
             clientChannel.configureBlocking(false);
             //读取客户端
             clientChannel.register(selector, SelectionKey.OP_READ); 
         
        selector.select(); 
        //在 selector 就绪的事件
        Set<SelectionKey> keys =   selector.selectedKeys();
        Iterator<SelectionKey> it =  keys.iterator();
        while(it.hasNext()){
            SelectionKey selectKey =  it.next();
            System.out.println(selectKey.readyOps());
            if(selectKey.isValid()){
                //检查是不是 SelectionKey.OP_ACCEPT 事件
                if(selectKey.isReadable()){
                    //读就绪
                    ByteBuffer  buffer = ByteBuffer.allocate(1024);
                    //channel 写入buffer
                    StringBuilder builder = new StringBuilder();
                    while(clientChannel.read(buffer)>0){
                         buffer.flip();
                        byte[] b = new byte[buffer.limit()];
                        builder.append(new String(b));
                        buffer.clear();
                    }
                    if("exit".equals(builder.toString())){
                        selectKey.cancel();
                        clientChannel.close();
                    }
                   
                }
            }
            
            //移除处理的事件 否则还在队列中
            it.remove();
        }
        
         }
         
         Thread.sleep(800);
        
       }
    }

}
