package com.bio.threadPool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
//http://www.oschina.net/question/565065_86540  线程池 详解
public class TimeServerHandlerExcutorPool  {
	
	  private  ExecutorService  executor;
	  
	 public TimeServerHandlerExcutorPool(int maxPoolSize,int queueSize) {
		// TODO Auto-generated constructor stub
		  executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), maxPoolSize, 120L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize));
	}
	  
	 public void execute(Runnable task){
		    executor.execute(task);
	 }

}
