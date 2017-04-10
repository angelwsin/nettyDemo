package com.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.File;

public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest>{
    

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        if(!msg.getDecoderResult().isSuccess()){
            SendError(ctx,HttpResponseStatus.BAD_REQUEST);
            return ;
        }
        if(msg.getMethod()!=HttpMethod.GET){
            SendError(ctx,HttpResponseStatus.METHOD_NOT_ALLOWED);
            return ;
        }
        String url = msg.getUri();
        System.out.println(url);
        
        if(url==null){
            SendError(ctx,HttpResponseStatus.FORBIDDEN);
            return ; 
        }
        File file = new File(System.getProperty("user.dir")+url);
        if(file.isHidden()||!file.exists()){
            SendError(ctx,HttpResponseStatus.NOT_FOUND);
            return ;
        }
        resp(ctx,file); 
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
    }
    private void SendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        ByteBuf buf = Unpooled.buffer(status.reasonPhrase().getBytes().length);
        buf.writeBytes(status.reasonPhrase().getBytes());
        DefaultFullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,status,buf);
        resp.headers().add(Names.CONTENT_TYPE,"text/html");
        ctx.writeAndFlush(resp);
    }
    
    private void resp(ChannelHandlerContext ctx,File file){
        StringBuffer buf = new StringBuffer();
        buf.append("<html>");
        if(file.isDirectory()){
          File[] files = file.listFiles();
          for(File f:files){
           buf.append("<a ").append("href='").append(f.getPath().replace(System.getProperty("user.dir"), "")).append("'>")
           .append(f.getName()).append("</a>");   
          }
         
        }
        buf.append("</html>");
        ByteBuf bu = Unpooled.buffer(buf.toString().getBytes().length);
        bu.writeBytes(buf.toString().getBytes());
        DefaultFullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK,bu);
        resp.headers().add(Names.CONTENT_TYPE,"text/html");
         
    }

}
