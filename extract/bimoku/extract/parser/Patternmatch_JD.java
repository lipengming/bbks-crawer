package bimoku.extract.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Patternmatch_JD {

	public static void main(String[] args) {
		String content = "商品名称： 基于硅技术24GHz汽车雷达微波电路(影印版)/国外电子信息精品著作 出版社： 科学 出版时间：2012-06-01 作者：(德)伊萨克诺夫 开本：16开 印刷时间： 2012-06-01 定价： 50 页数：208 印次： 1 ISBN号：9787030344779 商品类型：图书 版次： 1";
		patternmatchContent(content);
	}

	public static String[] patternmatchContent(String content) {
		String[] paramcontent = new String[4];
		//出版社
		Pattern p1 = Pattern
				.compile("出版社：[^/s]*[\u4e00-\u9fa5]*[^/s]*[\u4e00-\u9fa5]+[/s]*[^/s]*出版时间");
		Matcher m1 = p1.matcher(content);
		boolean result1 = m1.find();
		while (result1) {
			paramcontent[0] = m1.group(0).replaceAll("出版社：", "")
					.replaceAll("出版时间", "");
			//System.out.println(paramcontent[0]);
			result1 = m1.find();
		}

		//版次
		Pattern p2 = Pattern
				.compile("作者：[\u4e00-\u9fa5]*[^/s]*[\u4e00-\u9fa5]*[^/s]*[\u4e00-\u9fa5]+[/s]*[^/s]*开本");
		Matcher m2 = p2.matcher(content);
		boolean result2 = m2.find();
		while (result2) {
			paramcontent[1] = m2.group(0)
					.replaceAll("作者：", "")
					.replaceAll("开本", "");
			result2 = m2.find();
			
		//	System.out.println(paramcontent[1]);
		}
		
		
		
		
		
		Pattern p3 = Pattern.compile("ISBN号：[/s]*[0123456789]+",
				Pattern.CASE_INSENSITIVE);
		Matcher m3 = p3.matcher(content);
		boolean result3 = m3.find();
		while(result3)
			{
			paramcontent[2] = m3.group(0).replaceAll("ISBN号：", "");
			//System.out.println(paramcontent[2]);
			result3 = m3.find();
		}

		Pattern p4 = Pattern.compile("版次：[ ][/s]*[0123456789]+",
				Pattern.CASE_INSENSITIVE);
		Matcher m4 = p4.matcher(content);
		boolean result4 = m4.find();
		while(result4)
			{
			paramcontent[3] = m4.group(0).replaceAll("版次：", "");
			//System.out.println(paramcontent[3]);
			result4 = m4.find();
		}
		

		return paramcontent;
	}
	public static String[] patternmatchAUT_TRANS(String content) {
		String[] paramcontent = new String[2];
		//作者
		Pattern p1 = Pattern
				.compile("[^/s]*[\u4e00-\u9fa5]*[^/s]*[\u4e00-\u9fa5]+[/s]*[^/s]*著");
		Matcher m1 = p1.matcher(content);
		boolean result1 = m1.find();
	    if (result1) {
			paramcontent[0] = m1.group(0).replaceAll("著", "")
					.replaceAll("，", "");
			//System.out.println(paramcontent[0]);
			result1 = m1.find();
		}else{
			Pattern p11 = Pattern
					.compile("[^/s]*[\u4e00-\u9fa5]*[^/s]*[\u4e00-\u9fa5]+[/s]*[^/s]*编");
			Matcher m11 = p11.matcher(content);
			boolean result11 = m11.find();
			if(result11){
			paramcontent[0] = m11.group(0).replaceAll("编", "").replaceAll("，", "");
		//	System.out.println(paramcontent[0]);
			result11 = m11.find();
			}
		}

		//版次
		Pattern p2 = Pattern
				.compile("著[\u4e00-\u9fa5]*[^/s]*[\u4e00-\u9fa5]*[^/s]*[\u4e00-\u9fa5]+[/s]*[^/s]*译");
		Matcher m2 = p2.matcher(content);
		boolean result2 = m2.find();
		while (result2) {
			paramcontent[1] = m2.group(0)
					.replaceAll("译", " ").replaceAll("著", "").replaceAll("，", "");
			result2 = m2.find();
			
		//	System.out.println(paramcontent[1]);
		}
		return paramcontent;
	}	
		
/*	public static Boolean patternmatchAUT(String content){		
		
		
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
	}*/
}
