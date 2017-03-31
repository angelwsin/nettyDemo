package com.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel,Object>{

    public void completed(AsynchronousSocketChannel result, Object attachment) {
        AsynTimeServerHandler handler =  (AsynTimeServerHandler) attachment;
        //接受后续客户端的连接
        handler.asynServerSocketChannel.accept(attachment, this);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        result.read(buffer, buffer, new ReadCompletionHandler(result));
    }

    public void failed(Throwable exc, Object attachment) {
        AsynTimeServerHandler handler =  (AsynTimeServerHandler) attachment;
        handler.latch.countDown();
    }

}
