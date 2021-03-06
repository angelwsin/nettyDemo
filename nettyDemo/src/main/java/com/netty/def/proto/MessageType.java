package com.netty.def.proto;

public enum MessageType {
    
    LOGIN_REQ((byte)1),
    LOGIN_RESP((byte)2),
    HEARTBET_REQ((byte)3),
    HEARTBET_RESP((byte)4),
    RPC_REQ((byte)5),
    RPC_RESP((byte)6);
    
    MessageType(byte val){
        this.val = val;
    }
    private byte val;
    public byte getVal() {
        return val;
    }
    
    
}
