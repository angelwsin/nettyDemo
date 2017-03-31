package com.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class AsynTimerClientHandler implements  CompletionHandler<Void, AsynTimerClientHandler>,Runnable{
    
    private String host;
    private int    port;
    private AsynchronousSocketChannel  socketChannel;
    private CountDownLatch        latch = new CountDownLatch(1);
    
    

    public AsynTimerClientHandler(String host, int port) {
        super();
        this.host = host;
        this.port = port;
        try {
            this.socketChannel = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        this.socketChannel.connect(new InetSocketAddress(host, port), this, this);
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void completed(Void result, final AsynTimerClientHandler attachment) {
        String   content = "QUERY TIME";
        ByteBuffer buffer = ByteBuffer.allocate(content.getBytes().length);
        buffer.put(content.getBytes());
        buffer.flip();
        attachment.socketChannel.write(buffer,buffer,new CompletionHandler<Integer, ByteBuffer>() {

            public void completed(Integer result, ByteBuffer buffer) {
                 if(buffer.hasRemaining()){
                     attachment.socketChannel.write(buffer, buffer, this);
                 }else{
                     ByteBuffer buf = ByteBuffer.allocate(1024);
                     attachment.socketChannel.read(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {

                        public void completed(Integer result, ByteBuffer buf) {
                             buf.flip();
                             byte[] b  = new byte[buf.remaining()];
                             buf.get(b);
                             System.out.println(new String(b));
                             attachment.latch.countDown();
                            
                        }

                        public void failed(Throwable exc, ByteBuffer buf) {
                            try {
                                attachment.socketChannel.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                 }
                
            }

            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } 
            }
        });
        
    }

    public void failed(Throwable exc, AsynTimerClientHandler attachment) {
                    try {
                        attachment.clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
    }

}
