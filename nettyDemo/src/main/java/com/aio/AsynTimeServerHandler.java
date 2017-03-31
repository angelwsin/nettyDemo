package com.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class AsynTimeServerHandler implements Runnable{
    
     private String host;
     private int    port;
     protected AsynchronousServerSocketChannel  asynServerSocketChannel;
     protected CountDownLatch  latch  = new CountDownLatch(1);;
     
     

    public AsynTimeServerHandler(String host, int port) {
        super();
        this.host = host;
        this.port = port;
        try {
            this.asynServerSocketChannel = AsynchronousServerSocketChannel.open();
            this.asynServerSocketChannel.bind(new InetSocketAddress(host,port));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }



    public void run() {
        
        
        
        try {
            doAccept();
        } catch (IOException e) {
           e.printStackTrace();
        }
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    private void doAccept()throws IOException {
        //AcceptCompletionHandler 接受客户端成功连接的处理
        this.asynServerSocketChannel.accept(this, new AcceptCompletionHandler());
    }
    
    

}
