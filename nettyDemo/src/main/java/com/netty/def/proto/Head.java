package com.netty.def.proto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Head implements Serializable{
    
    /**  */
    private static final long serialVersionUID = 1L;
    private int crcCode;
    private int length;
    private long sessionID;
    private byte  priority;
    private byte type;
    
    private Map<String,Object> attach  = new HashMap<String, Object>();

    public int getCrcCode() {
        return crcCode;
    }

    public void setCrcCode(int crcCode) {
        this.crcCode = crcCode;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getSessionID() {
        return sessionID;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public Map<String, Object> getAttach() {
        return attach;
    }

    public void setAttach(Map<String, Object> attach) {
        this.attach = attach;
    }

    @Override
    public String toString() {
        return "Head [crcCode=" + crcCode + ", length=" + length + ", sessionID=" + sessionID
               + ", priority=" + priority + ", type=" + type + ", attach=" + attach + "]";
    }
    

    
}
