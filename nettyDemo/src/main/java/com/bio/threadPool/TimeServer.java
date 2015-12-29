package com.bio.threadPool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TimeServer {
	
	  public static void main(String[] args) {
		          ServerSocket server = null;
				try {
					server = new ServerSocket(9867);
					TimeServerHandlerExcutorPool executor = new TimeServerHandlerExcutorPool(50, 1000);
					  while(true){
			            	Socket socket =  server.accept();
			            	executor.execute(new TimeServerHandler(socket));
			            }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					if(server!=null){
						try {
							server.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
		          
		           
		          
	}

}
