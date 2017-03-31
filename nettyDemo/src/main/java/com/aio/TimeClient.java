package com.aio;

public class TimeClient {
    
    
    public static void main(String[] args) {
        new Thread(new AsynTimerClientHandler("localhost",8943)).start();
    }

}
