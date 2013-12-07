package bimoku.realtimecrawler.control;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;




import bimoku.extract.common.PropertyUtil;
import bimoku.extract.common.exception.ExtractException;
import bimoku.extract.parser.HttpConnectionManager;
import bimoku.extract.parser.Parser;

import bimoku.realtimecrawler.parse.TitleParse;
import bimoku.realtimecrawler.parse.UrlParse;

import bimoku.realtimecrawler.queue.TitleQueue;
import bimoku.realtimecrawler.queue.UrlQueue;


import com.bimoku.common.bean.RealTimeType;
import com.bimoku.util.FileUtils;

/**
 * 抽取的线程
 * 
 * @author 梅良
 * @author LPM 跟新优化代码，，使用实现runnable的方法，方便使用线程池
 * 
 */
public class ControlDdnews implements Runnable {

	private Parser parser;
	private String configPath;
	private String firstUrl;

	public ControlDdnews(String firstUrl, Parser parser, String configPath) {
		this.firstUrl = firstUrl;
		this.parser = parser;
		this.configPath = configPath;

	}

	public void run() {
		 //String filepath=new String();
		 UrlQueue SecondUrlQueue = new UrlQueue();
		firstUrl = PropertyUtil.readProperty(PropertyUtil.defaultStr_newbook) + firstUrl ;
		System.out.println(firstUrl);
		String secondhtml = HttpConnectionManager.getHtml(firstUrl.trim());
		
		 try {
			UrlParse.getparse(SecondUrlQueue, secondhtml, PropertyUtil.readProperty(PropertyUtil.secondcategoryurl_newbook));
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 /*try {
			TitleParse.getparse(SecondTitleQueue, secondhtml, p.getProperty("secondcategorytitle"));
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		 
		 while(!SecondUrlQueue.isQueueEmpty()){
			 String secondUrl= new String();
			
			
			 UrlQueue bookUrlQueue = new UrlQueue();
			 
			 UrlQueue rankPage=new UrlQueue(); //保存排行榜的不同页面
			
			 secondUrl = PropertyUtil.readProperty(PropertyUtil.defaultStr_newbook) + SecondUrlQueue.deQueue();
			 System.out.println(secondUrl);
			 String defaultpageurl = secondUrl.replaceAll("page=[0-9]*.*", "page=");
			 
			String html = HttpConnectionManager.getHtml(secondUrl);
			Document doc = Jsoup.parse(html);
			Element element = doc.select(PropertyUtil.readProperty(PropertyUtil.rankpage_newbook)).get(2);
			String indexStr = element.text();
			indexStr = indexStr.replaceAll("共显示： TOP", "");
			//System.out.println(indexStr+"+++++++++");
			int index = Integer.parseInt(indexStr);
			int page;//该目录下新书的页数
			if(index == 0){
				continue; //如果页数为0，说明该目录没有新书
			}else{
				if(index%20 ==0){
					page = index/20;
				}else{
					page = index/20+1;
				}
			}
			for(int i=0;i<page;i++){
				int pageindex = i+1;
				String pageurl = defaultpageurl +pageindex;
				//System.out.println(pageurl);
				rankPage.enQueue(pageurl);
			}
			
			/*try {
				UrlParse.getparse(rankPage, html,  PropertyUtil.readProperty(PropertyUtil.rankpage_newbook));
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			 while(!rankPage.isQueueEmpty()){
				 String rankpageurl = rankPage.deQueue();
				 String thirdhtml = HttpConnectionManager.getHtml(rankpageurl);
				
				 try {
					UrlParse.getparse(bookUrlQueue, thirdhtml, PropertyUtil.readProperty(PropertyUtil.bookurl_newbook));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			 }
			
			 while(!bookUrlQueue.isQueueEmpty()){
						
				 String bookurl=new String();
				 bookurl = bookUrlQueue.deQueue();
				
				 System.out.println(bookurl);
				
				 String bookhtml = HttpConnectionManager.getHtml(bookurl.trim());
				 parser.parserForRealTime(bookhtml, RealTimeType.NEWS_BOOK,bookurl.length()<150?bookurl:bookurl.substring(0, 150));
				

			 }
		 }
	}

	
}
