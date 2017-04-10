package com.java.nio;

import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;

public class SelectProviderTest {
	
	public static void main(String[] args) throws Exception{
		Selector s1 =SelectorProvider.provider().openSelector();
		Selector s2 =SelectorProvider.provider().openSelector();
		System.out.println(s1==s2);
		
	}

}
