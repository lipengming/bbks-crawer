package com.bimoku.util.xml;

import java.util.ArrayList;
import java.util.List;

public class Min {
	public static void main(String[] args) {
		
	}
	
	public static int minLength(String s){
		
		return 0;
	}
	
	/**
	 * 一个字符串有多少种分组方式
	 * @param s
	 * @return
	 */
	public static List<String> group(String s){
		List<String> arr = new ArrayList<String>();
		
		return arr;
	}
	
	
	
	/**
	 * 替换规则
	 * @param s
	 * @return
	 */
	private static String replace(String s){
		if(s == null || "".equals("") || s.length() > 2)
			return null;
		if(!s.contains("a")){
			return "a";
		}
		else if(!s.contains("b")){
			return "b";
		}
		else if(!s.contains("c")){
			return "c";
		}
		return null;
	}
}
