package bimoku.extract.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Patternmatch_Amazon {

	public static void main(String[] args) {
		String content = "出版社: 重庆出版集团，重庆出版社; 第1版 (2010年5月1日) 丛书名: 国医绝学健康馆 平装: 95页 语种： 简体中文 开本: 32  条形码: 9787229019358 商品尺寸: 16.2 x 11.4 x 0.8 cm 商品重量: 141 g 品牌: 日知图书 ASIN: B003NHS4HI 用户评分: 平均4.5 星  浏览全部评论 (81 条商品评论) 81条评论 5 星:  (54) 4 星:  (16) 3 星:  (9) 2 星:  (1) 1 星:  (1) › 查看全部 81 条商品评论... 亚马逊热销商品排名: 图书商品里排第7,230名 (查看图书商品销售排行榜) 第6位 - 图书 > 健身与保健 > 养生 > 运动与按摩 第13位 - 图书 > 医学 > 中医学 > 中医养生 > 针灸 第16位 - 图书 > 医学 > 中医学 > 中医养生 > 穴位按摩  您想告诉我们您发现了更低的价格?2013-8-23 10:14:32 com.bimoku.persistence.dao.impl.BaseDaoMysqlImpl search";
		patternmatchContent(content);
	}

	public static String[] patternmatchContent(String content) {
		String[] paramcontent = new String[4];
		//出版社
		Pattern p1 = Pattern
				.compile("出版社: [^/s]*[\u4e00-\u9fa5]*[^/s]*[\u4e00-\u9fa5]+[/s]*[^/s]*;");
		Matcher m1 = p1.matcher(content);
		boolean result1 = m1.find();
		while (result1) {
			paramcontent[0] = m1.group(0).replaceAll("出版社: ", "")
					.replaceAll(";", "");
			//System.out.println(paramcontent[0]);
			result1 = m1.find();
		}

		//版次
		Pattern p2 = Pattern
				.compile(";[\u4e00-\u9fa5]*[^/s]*[\u4e00-\u9fa5]*[^/s]*[\u4e00-\u9fa5]+[/s]*[^/s]*[平装丛书名精装外文书名语种]");
		Matcher m2 = p2.matcher(content);
		boolean result2 = m2.find();
		while (result2) {
			paramcontent[1] = m2.group(0)
					.replaceAll(";|平装[^/s]*[\u4e00-\u9fa5]*[^/s]*[\u4e00-\u9fa5]+[/s]*[^/s]*|丛书名[^/s]*[\u4e00-\u9fa5]*[^/s]*[\u4e00-\u9fa5]+[/s]*[^/s]*|精装[^/s]*[\u4e00-\u9fa5]*[^/s]*[\u4e00-\u9fa5]+[/s]*[^/s]*|外文书名[^/s]*[\u4e00-\u9fa5]*[^/s]*[\u4e00-\u9fa5]+[/s]*[^/s]*|语种[^/s]*[\u4e00-\u9fa5]*[^/s]*[\u4e00-\u9fa5]+[/s]*[^/s]*", " ")
					.replaceAll("", "");
			result2 = m2.find();
			paramcontent[1] = paramcontent[1].replaceAll("平装|丛书名|精装|外文书名|语种", "");
			//System.out.println(paramcontent[1]);
		}
		
		
		Pattern p3 = Pattern.compile(
				"ASIN: [/s]*[a-zA-Z0-9]*",
				Pattern.CASE_INSENSITIVE);
		Matcher m3 = p3.matcher(content);
		boolean result3 = m3.find();
		while (result3) {
			paramcontent[2] = m3.group(0).replaceAll("ASIN:", "");
			result3 = m3.find();
			//System.out.println(paramcontent[2]);
		}
		
		
		Pattern p4 = Pattern.compile("ISBN:[ ][/s]*[0123456789]+",
				Pattern.CASE_INSENSITIVE);
		Matcher m4 = p4.matcher(content);
		boolean result4 = m4.find();
		if (result4)
			{paramcontent[3] = m4.group(0).replaceAll("ISBN: ", "");
			//System.out.println(paramcontent[3]);
			result4 = m4.find();
		}else{
			Pattern p44 = Pattern.compile("条形码:[ ][/s]*[0123456789]+",
					Pattern.CASE_INSENSITIVE);
			Matcher m44 = p44.matcher(content);
			boolean result44 = m44.find();
			if(result44){
			paramcontent[3] = m44.group(0).replaceAll("条形码:", "");
			//System.out.println(paramcontent[3]);
			result44 = m44.find();
			}
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
			//System.out.println(content2);
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
