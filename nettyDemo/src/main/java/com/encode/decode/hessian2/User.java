package com.encode.decode.hessian2;

import java.io.Serializable;

public class User implements Serializable{

    /**  */
    private static final long serialVersionUID = 1L;
    
    private String userName;
    private int    age;
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    
    

}
