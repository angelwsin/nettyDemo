package com.encode.decode.hessian2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.netty.def.proto.Head;
import com.netty.def.proto.NettyMessage;

public class HessianTest {
    
    public static void main(String[] args) throws Exception{
        User user = new User();
        user.setAge(12);
        user.setUserName("zhangsan");
        HessianTest test = new HessianTest();
        User u = (User) test.decode(test.encode(user));
        System.out.println(u.getUserName());
        NettyMessage msg  = new NettyMessage();
        msg.setBody((byte)1);
        msg.setHeads(new Head());
        NettyMessage msgH = (NettyMessage) test.decode(test.encode(msg));
        System.out.println(msgH.getBody());
    }
    
    
    public byte[]  encode(Object obj)throws Exception{
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput hessian = new HessianOutput(os);
        hessian.writeObject(obj);
        return os.toByteArray();
    }
    
    public Object decode(byte[] b)throws Exception{
        ByteArrayInputStream is = new ByteArrayInputStream(b);
        HessianInput     hessian = new HessianInput(is);
        return hessian.readObject();
    }

}
