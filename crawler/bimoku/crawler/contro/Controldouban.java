package bimoku.crawler.contro;





import bimoku.crawler.spider.HttpConnectionManagerdouban;

import bimoku.extract.parser.Parser;

public class Controldouban implements Runnable
{
private int startindex;
private int endindex;
private Parser parser;

public Controldouban(int startindex,int endindex ,Parser parser)
{
this.startindex= startindex;
this.endindex= endindex;
this.parser = parser;

}
public void run() {
         String defaultStr= "http://book.douban.com/subject/";
		
		 HttpConnectionManagerdouban httpConnectionManager=new HttpConnectionManagerdouban();
		 String htmlurl = "";
		 String html="";		 
		 for(int index=startindex;index<=endindex;index++){
			 htmlurl=defaultStr+index+"/";
			 System.out.println(htmlurl);
			 try {
				html=httpConnectionManager.getHtml(htmlurl);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 if(html =="noexist"){
				   continue;
			 }else{
				 parser.parser(html, htmlurl.length()<150?htmlurl:htmlurl.substring(0, 150));
				
			 }
		 }

}
}
