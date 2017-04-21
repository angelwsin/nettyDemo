package com.netty.def.proto;

import java.lang.reflect.Proxy;
import java.util.concurrent.CountDownLatch;

public class ClientFuture {
    
     public static CountDownLatch latch  = new CountDownLatch(1);
    
     public static void main(String[] args) throws Exception{
           
         ClientFuture f = new ClientFuture();
         RpcService service = f.proxy(RpcService.class);
         String s = service.say("zhangsn");
         System.out.println(s);
         
    }
    
    
    
    
    
    
    @SuppressWarnings({ "unchecked" })
    private <T> T proxy(Class<T> interfaces)throws Exception{
        if(Client.future==null)
        latch.await();
         return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
            new Class[] {interfaces}, new ProxyInvocationHandler(Client.future));

    }

}
