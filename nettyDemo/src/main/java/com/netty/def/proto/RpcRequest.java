package com.netty.def.proto;

import java.io.Serializable;

public class RpcRequest implements Serializable {
    
    
     /**  */
    private static final long serialVersionUID = 1L;
    private String serviceName;
     private String methodName;
     private Object[] params;
     private String[] methodSign;
    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    public String getMethodName() {
        return methodName;
    }
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    public Object[] getParams() {
        return params;
    }
    public void setParams(Object[] params) {
        this.params = params;
    }
    public String[] getMethodSign() {
        return methodSign;
    }
    public void setMethodSign(String[] methodSign) {
        this.methodSign = methodSign;
    }
     
     
     
     

}
