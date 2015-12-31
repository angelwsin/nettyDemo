package com.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MutiplexerTimeServer implements Runnable{
	  private Selector selector;
	  private ServerSocketChannel serChannel;
	  private volatile boolean stop;
	  
	  /*
	   * 初始化多路复用器，绑定监听端口
	   */
	  public MutiplexerTimeServer(int port) {
		// TODO Auto-generated constructor stub
		  try {
			selector = Selector.open();
			serChannel = ServerSocketChannel.open();
			serChannel.configureBlocking(false);
			serChannel.socket().bind(new InetSocketAddress(port), 1024);
			serChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		  
	}
	  
	  public void stop(){
		  this.stop = true;
	  }
	public void run() {
		// TODO Auto-generated method stub
		while(!stop){
			try {
				selector.select(1000);
				Set<SelectionKey> SelectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> it =  SelectionKeys.iterator();
				SelectionKey key = null;
				while(it.hasNext()){
					    key = it.next();
					    it.remove();
					    try {
							handlerInput(key);
						} catch (Exception e) {
							// TODO: handle exception
							if(key!=null){
								key.cancel();
								if(key.channel()!=null){}
								  key.channel().close();
							}
						}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	private void handlerInput(SelectionKey key)throws Exception{
		  if(key.isValid()){
			   if(key.isAcceptable()){
				 ServerSocketChannel ssc =       (ServerSocketChannel) key.channel();
				 SocketChannel  sc = ssc.accept();
				 sc.configureBlocking(false);
				 sc.register(selector, SelectionKey.OP_READ);
			   }
			   
			   if(key.isReadable()){
				    SocketChannel   sc =  (SocketChannel) key.channel();
				    ByteBuffer readerBuffer =ByteBuffer.allocate(1024);
				    int readBytes  = sc.read(readerBuffer);
				    if(readBytes>0){
				    	readerBuffer.flip();
				    	byte[] bytes  = new byte[readerBuffer.remaining()];
				    	 readerBuffer.get(bytes);
				    	 String  body = new String(bytes,"UTF-8");
				    	 System.out.println("THIS  server receiver  content "+body);
				    	 String currentTime  ="QUERY TIME".equals(body)?new Date().toString():"bad order";
				    	 doWriter(sc,currentTime);
				    }else if(readBytes<0){
				    	key.cancel();
				    	sc.close();
				    }
			   }
		  }
	}
	
	private void  doWriter(SocketChannel sc ,String response)throws IOException{
		    if(response!=null&&response.trim().length()>0){
		    	byte[] bytes = response.getBytes();
		    	ByteBuffer  writerBuffer=ByteBuffer.allocate(bytes.length);
		    	writerBuffer.put(writerBuffer);
		    	writerBuffer.flip();
		    	sc.write(writerBuffer);
		    	
		    }
	}

}
