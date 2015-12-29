package com.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TimeServer {
	
	  public static void main(String[] args) {
		          ServerSocket server = null;
				try {
					server = new ServerSocket(9867);
					  while(true){
			            	Socket socket =  server.accept();
			            	  new Thread(new TimeServerHandler(socket)).start();
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
