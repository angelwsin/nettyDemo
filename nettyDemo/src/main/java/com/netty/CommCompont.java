package com.netty;

import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;

import com.encode.decode.msgPack.MsgPackDecoder;
import com.encode.decode.msgPack.MsgPackEncoder;
import com.netty.ssl.SLLSecurety;

public abstract class CommCompont {
    
    
    
    //netty 自带的编码器解决 粘包/拆包问题
    protected  void   packDecod(SocketChannel ch){
        ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
        ch.pipeline().addLast(new StringDecoder());
    }
    
    
    protected void msgPack(SocketChannel ch){
        ch.pipeline().addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2,0,2));
        ch.pipeline().addLast("msgPackDecoder", new MsgPackDecoder());
        ch.pipeline().addLast("LengthFieldPrepender", new LengthFieldPrepender(2));
        ch.pipeline().addLast("MsgPackEncoder", new MsgPackEncoder());
    }
    
    
    public static SSLEngine   ssl(String path,String pass,SLLSecurety type)throws Exception{
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        TrustManagerFactory tf = TrustManagerFactory.getInstance("SunX509");
        KeyStore ks = KeyStore.getInstance("JKS");
        FileInputStream is = new FileInputStream(System.getProperty("user.dir") + File.separator
                                                 + "ssl" + File.separator + path);
        ks.load(is, pass.toCharArray());
        kmf.init(ks, pass.toCharArray());
        SSLContext context = SSLContext.getInstance("SSL");
        switch (type) {
            case Client:
                tf.init(ks);
                context.init(null, tf.getTrustManagers(), null);
                break;
            case Server:
                //服务端不需要验证客户端的合法性 TrustManagers 为空
               //SecureRandom 为空  使用默认产生的随机数
                context.init(kmf.getKeyManagers(), null, null);
                break;
            case Client_Server:
                //双向验证
                tf.init(ks);
                context.init(kmf.getKeyManagers(),  tf.getTrustManagers(), null);
              break;

            default:
               throw new RuntimeException("参数错误");
        }
        
        return    context.createSSLEngine();
        
    }
    
    
   

}
