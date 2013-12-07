package bimoku.extract.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Patternmatch_Pub {

	public static void main(String[] args) {
		String content = "作者： Stephen Prata    丛书名： C和C++实务精选 出版社：人民邮电出版社 ISBN：9787115279460 上架时间：2012-6-8 出版日期：2012 年6月 开本：16开 页码：952 版次：6-1 所属分类： 计算机 > 软件与程序设计 > C++ > C++ 合作专区 > 微软技术图书 > 微软程序设计 > 微软C/C++/VC++";
		patternmatchContent(content);
	}

	public static String[] patternmatchContent(String content) {
		String[] paramcontent = new String[4];
		//出版社
		Pattern p1 = Pattern
				.compile("作者：[^/s]*[\u4e00-\u9fa5]*[^/s]*[\u4e00-\u9fa5]*[/s]*[^/s]*丛书名：");
		Matcher m1 = p1.matcher(content);
		boolean result1 = m1.find();
		if (result1) {
			paramcontent[0] = m1.group(0).replaceAll("丛书名：", "")
					.replaceAll("作者：", "");
			
			result1 = m1.find();
		}else{
			Pattern p11 = Pattern
					.compile("作者：[^/s]*[\u4e00-\u9fa5]*[^/s]*[\u4e00-\u9fa5]+[/s]*[^/s]*出版社：");
			Matcher m11 = p11.matcher(content);
			boolean result11 = m11.find();
			if(result11){
				paramcontent[0] = m11.group(0).replaceAll("出版社：", "")
						.replaceAll("作者：", "");
				
				result11 = m11.find();
			}
		}
       System.out.println(paramcontent[0]);
		//出版社
		Pattern p2 = Pattern
				.compile("出版社：[\u4e00-\u9fa5]*[^/s]*[\u4e00-\u9fa5]*[^/s]*[\u4e00-\u9fa5]+[/s]*[^/s]*ISBN：");
		Matcher m2 = p2.matcher(content);
		boolean result2 = m2.find();
		while (result2) {
			paramcontent[1] = m2.group(0)
					.replaceAll("出版社："," " )
					.replaceAll("ISBN：", "");
			result2 = m2.find();
			
			System.out.println(paramcontent[1]);
		}
		
		//ISBN
		Pattern p3 = Pattern.compile(
				"ISBN：[/s]*[0-9]*",
				Pattern.CASE_INSENSITIVE);
		Matcher m3 = p3.matcher(content);
		boolean result3 = m3.find();
		while (result3) {
			paramcontent[2] = m3.group(0).replaceAll("ISBN：", "");
			result3 = m3.find();
			System.out.println(paramcontent[2]);
		}
		
		//版次
		
		Pattern p4 = Pattern.compile("版次：[/s]*[0123456789]+[\\pP]*[0123456789]+",
				Pattern.CASE_INSENSITIVE);
		Matcher m4 = p4.matcher(content);
		boolean result4 = m4.find();
		while (result4)
			{paramcontent[3] = m4.group(0).replaceAll("版次：", "");
			System.out.println(paramcontent[3]);
			result4 = m4.find();
		}

		return paramcontent;
	}
	public static String[] patternmatchAUT_TRANS(String content) {
		Pattern p = Pattern
				.compile("~ [^/s]*[\u4e00-\u9fa5]*[^/s]*[a-zA-Z]*[\u4e00-\u9fa5]*[/s]*[^/s]*");
		Matcher m = p.matcher(content);
		String content2;
		String[] paramcontent = new String[2];
		boolean result = m.find();
		while (result) {
			 content2 = m.group(0);
			//System.out.println(temp);
			//temp.split(",");
			//System.out.println(temp.substring(0, temp.indexOf("(作者)")).trim());
		
			result = m.find();
			System.out.println(content2);
			String param[] = content2.split(",");
			
			for(int i=0;i<param.length;i++){
				if(patternmatchAUT(param[i])){
					if(paramcontent[0]!=null)
					{
						paramcontent[0] = paramcontent[0]+","+param[i].replaceAll("\\(作者\\)","").replaceAll("\\(编者\\)","").replaceAll("~ ", "");
					}else{
						paramcontent[0] = param[i].replaceAll("\\(作者\\)","").replaceAll("\\(编者\\)","").replaceAll("~ ", "");
					}
				}
				if(patternmatchTRANS(param[i])){
					if(paramcontent[1]!=null){
						
					
					paramcontent[1] = paramcontent[1]+","+param[i].replaceAll("\\(译者\\)"," ");
					}else{
						paramcontent[1] = param[i].replaceAll("\\(译者\\)"," ");
					}
					}
			}
			
		}
		return paramcontent;
	}	
		
	public static Boolean patternmatchAUT(String content){		
		
		
		//作者
		Pattern p1 = Pattern
				.compile("(作者)|(编者)");
		Matcher m1 = p1.matcher(content);
		
		boolean result1 = m1.find();
		return result1;
	}
		//译者
public static Boolean patternmatchTRANS(String content){		
		
		
		//作者
		Pattern p1 = Pattern
				.compile("(译者)");
		Matcher m1 = p1.matcher(content);
		
		boolean result1 = m1.find();
		return result1;
	}
}
