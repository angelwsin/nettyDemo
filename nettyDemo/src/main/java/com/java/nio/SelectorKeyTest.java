package com.java.nio;

import io.netty.channel.ChannelException;
import io.netty.util.internal.SystemPropertyUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SelectorKeyTest {
    
    Selector selector;
    private Set<SelectionKey> selectedKeys;

    private final SelectorProvider provider;
    
    static {
        String key = "sun.nio.ch.bugLevel";
        try {
            String buglevel = System.getProperty(key);
            if (buglevel == null) {
                System.setProperty(key, "");
            }
        } catch (SecurityException e) {
        }


    }
    public SelectorKeyTest() {
        provider = SelectorProvider.provider();
    }
    
    
    private Selector openSelector() {
        final Selector selector;
        try {
            selector = provider.openSelector();
        } catch (IOException e) {
            throw new ChannelException("failed to open a new selector", e);
        }


        try {
            Set<SelectionKey> selectedKeySet = new HashSet<SelectionKey>();

            Class<?> selectorImplClass =
                    Class.forName("sun.nio.ch.SelectorImpl", false, ClassLoader.getSystemClassLoader());

            // Ensure the current selector implementation is what we can instrument.
            if (!selectorImplClass.isAssignableFrom(selector.getClass())) {
                return selector;
            }

            Field selectedKeysField = selectorImplClass.getDeclaredField("selectedKeys");
            Field publicSelectedKeysField = selectorImplClass.getDeclaredField("publicSelectedKeys");

            selectedKeysField.setAccessible(true);
            publicSelectedKeysField.setAccessible(true);

            selectedKeysField.set(selector, selectedKeySet);
            publicSelectedKeysField.set(selector, selectedKeySet);

            selectedKeys = selectedKeySet;
        } catch (Throwable t) {
            selectedKeys = null;
        }

        return selector;
    }

    
    public static void main(String[] args) throws Exception{
       SelectorKeyTest  sel =  new SelectorKeyTest();
        Selector selector =  sel.openSelector();
        ServerSocketChannel server = ServerSocketChannel.open();
      //selector 和 channel组合使用必须是非阻塞
        server.configureBlocking(false);
        server.socket().bind(new InetSocketAddress(8493));
        //selector注册感兴趣的事件
       SelectionKey selKey = server.register(selector,0);
       //使用selKey 注册感兴趣的事件
        selKey.interestOps(SelectionKey.OP_ACCEPT);
        
        while(true){
          //非阻塞 立即返回 可能为null
          //阻塞等待 客户端连接
            
           int x =   selector.selectNow();
           System.out.println("selectnow,"+x);
           Thread.sleep(2000);
            SocketChannel  clientChannel =  server.accept();
            System.out.println("accept,"+sel.selectedKeys.toArray().length);
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
             System.out.println("read,"+sel.selectedKeys.toArray().length);
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
          
          Thread.sleep(2000);
         
        }
    }

}
