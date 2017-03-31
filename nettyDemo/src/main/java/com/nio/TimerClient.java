package com.nio;

public class TimerClient {
    
    public static void main(String[] args) {
         
        new Thread(new TimerCientHandler("localhost", 9882)).start();;
        
        
    }

}
