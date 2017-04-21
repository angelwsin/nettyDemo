package com.netty.def.proto;

import java.io.Serializable;

public class RpcResponse implements Serializable{
    
    
     /**  */
    private static final long serialVersionUID = 1L;
    private Object result;
    private Long     uniqueId;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Long getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Long uniqueId) {
        this.uniqueId = uniqueId;
    }

    
     
    
}
