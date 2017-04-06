package com.encode.decode.msgPack;

import java.util.ArrayList;
import java.util.List;

import org.msgpack.MessagePack;
import org.msgpack.template.Templates;

//msgPack 编解码
public class MsgPackMainTest {
    
    
    public static void main(String[] args) throws Exception{
        List<String> list = new ArrayList<String>(3);
        list.add("msgA");
        list.add("msgB");
        list.add("msgC");
        MessagePack msgPack = new MessagePack();
        byte[] b =  msgPack.write(list);
        List<String> sList=  msgPack.read(b, Templates.tList(Templates.TString));
        System.out.println(sList.get(0));
    }

}
