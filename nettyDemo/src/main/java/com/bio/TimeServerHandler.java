package com.bio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class TimeServerHandler  implements Runnable {
           private Socket  socket;
           
           
	public TimeServerHandler(Socket socket) {
			this.socket = socket;
		}


	public void run() {
		// TODO Auto-generated method stub
		BufferedReader  reader  = null;
		BufferedWriter writer = null;
		try {
			 reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			  writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			 while(true){
				String  in =   reader.readLine();
				writer.write(in+"server");
			 }
		} catch (Exception e) {
			// TODO: handle exception
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if(writer!=null){
				 try {
					writer.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if(this.socket!=null){
				  try {
					this.socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		   
	}
	


}
