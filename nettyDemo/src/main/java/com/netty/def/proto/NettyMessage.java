package com.netty.def.proto;

import java.io.Serializable;

public class NettyMessage implements Serializable{
    
    /**  */
    private static final long serialVersionUID = 1L;
    private Head  heads;
    private Object body;
    public Head getHeads() {
        return heads;
    }
    public void setHeads(Head heads) {
        this.heads = heads;
    }
    public Object getBody() {
        return body;
    }
    public void setBody(Object body) {
        this.body = body;
    }
    @Override
    public String toString() {
        return "NettyMessage [heads=" + heads + "]";
    }
    
    

}
