package com.util;

public class InstanceFactory {
    
    
    public static <T> T  newInstance(Class<T> claz){
               try {
                return claz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
           return null;
    }

}
