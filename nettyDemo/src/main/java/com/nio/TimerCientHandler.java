package com.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TimerCientHandler  implements Runnable{

    private String host;
    private int    port;
    private Selector selector;
    private SocketChannel socketChannel;
    
    
    
    
    public TimerCientHandler(String host, int port) {
        super();
        this.host = host;
        this.port = port;
        try {
            this.selector = Selector.open();
            this.socketChannel = SocketChannel.open();
            this.socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
       
      
    }




    public void run() {
        
        try {
            doConnect();
        } catch (IOException e) {
           e.printStackTrace();
           System.exit(-1);
        }
        
        while(true){
            try {
                this.selector.select();
              Set<SelectionKey> keys =   this.selector.selectedKeys();
               Iterator<SelectionKey> it = keys.iterator();
               while(it.hasNext()){
                   SelectionKey selKey = it.next();
                   it.remove();
                   handleInput(selKey);
               }
            } catch (IOException e) {
               e.printStackTrace();
               
            }
            
        }
    }
    
    
    private void handleInput(SelectionKey selKey)throws IOException {
         if(selKey.isValid()){
            SocketChannel channel =  (SocketChannel) selKey.channel();
            if (selKey.isConnectable()) {
                if (channel.finishConnect()) {
                    channel.register(this.selector, SelectionKey.OP_READ);
                    doWrite(channel);
                
            } else {
                System.exit(-1);
            }
                
            }
            
            if(selKey.isReadable()){
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                while(channel.read(buffer)>0){
                    buffer.flip();
                     byte[] b = new byte[buffer.limit()];
                     buffer.get(b);
                     System.out.println(new String(b));
                     buffer.clear();
                }
             }
         }
            
        
    }




    public void doConnect()throws IOException{
        //如果直连成功 注册读 并写
        if(this.socketChannel.connect(new InetSocketAddress(this.host,this.port))){
            this.socketChannel.register(this.selector,SelectionKey.OP_READ);
            doWrite( this.socketChannel);
        }else{
            this.socketChannel.register(this.selector,SelectionKey.OP_CONNECT); 
        }
    }




    private void doWrite(SocketChannel channel) throws IOException{
        String   content = "QUERY TIME";
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(content.getBytes());
        buffer.flip();
        channel.write(buffer);
        
    }

}
