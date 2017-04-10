package com.encode.decode.protobuf;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;

public class SubProtoBufTest {

	
	public static  byte[]  encode(SubReqProto.SubReq subReq){
		
		return subReq.toByteArray();
	}
	
	public static SubReqProto.SubReq  decode(byte[] body)throws InvalidProtocolBufferException{
		return SubReqProto.SubReq.parseFrom(body);
	}
	
	public static SubReqProto.SubReq  createSubReq(){
		SubReqProto.SubReq.Builder  builder = SubReqProto.SubReq.newBuilder();
		builder.setSubReqID(1);
		builder.setUserName("zhangsan");
		builder.setProductName("haha");
		List<String> address = new ArrayList<String>();
		address.add("shanghai");
		address.add("shenzhen");
		address.add("guangzhou");
		address.add("beijing");
		builder.addAllAddress(address);
		return builder.build();
	}
	
	public static void main(String[] args) throws Exception{
		SubReqProto.SubReq req1 = createSubReq();
		SubReqProto.SubReq req2 = decode(encode(req1));
		System.out.println(req2.getUserName());
		System.out.println(req1.equals(req2));
	}
}
