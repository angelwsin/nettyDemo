package com.aio;

public class TimeServer {

    
    public static void main(String[] args) {
        new Thread(new AsynTimeServerHandler("localhost", 8943)).start();
    }
}
