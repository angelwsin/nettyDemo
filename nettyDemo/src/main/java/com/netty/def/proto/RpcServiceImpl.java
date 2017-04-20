package com.netty.def.proto;

public class RpcServiceImpl implements RpcService{

    public void say() {
        System.out.println("hello ");
    }

    public String say(String name) {
        System.out.println("hello "+name);
        return "hello "+name;
    }

}
