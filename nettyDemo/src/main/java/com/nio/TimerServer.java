package com.nio;

public class TimerServer {
	
	  public static void main(String[] args) throws Exception{
		
		  MutiplexerTimeServer timeServer = new MutiplexerTimeServer(9882);
		  new Thread(timeServer, "MutiplexerTimeServer").start();
		  
	}

}
