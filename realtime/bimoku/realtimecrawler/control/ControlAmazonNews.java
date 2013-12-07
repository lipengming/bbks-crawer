package bimoku.realtimecrawler.control;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Properties;




import bimoku.extract.common.PropertyUtil;
import bimoku.extract.common.exception.ExtractException;
import bimoku.extract.parser.HttpConnectionManager;
import bimoku.extract.parser.Parser;


import bimoku.realtimecrawler.parse.UrlParse;


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
public class ControlAmazonNews implements Runnable{

	private Parser parser;
	private String configPath;
	private String firstUrl;
	
	
	public ControlAmazonNews(String firstUrl,Parser parser,String configPath) {
		this.firstUrl = firstUrl;
		this.parser = parser;
		this.configPath = configPath;
		
	}

	public void run() {
		 //String filepath=new String();
		 UrlQueue SecondUrlQueue = new UrlQueue();
		
		 String secondhtml = HttpConnectionManager.getHtml(firstUrl);
		
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
			
			 secondUrl = SecondUrlQueue.deQueue();
			String html = HttpConnectionManager.getHtml(secondUrl);
			 try {
				UrlParse.getparse(rankPage, html,  PropertyUtil.readProperty(PropertyUtil.rankpage_newbook));
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
				 parser.parserForRealTime(bookhtml, RealTimeType.NEWS_BOOK,bookurl.length()<250?bookurl:bookurl.substring(0, 250));

				

			 }
		 }
	}
	
	
}
