package bimoku.realtimecrawler.control;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import com.bimoku.common.bean.RealTimeType;

import bimoku.extract.common.PropertyUtil;
import bimoku.extract.common.exception.ExtractException;
import bimoku.extract.parser.HttpConnectionManager;
import bimoku.extract.parser.Parser;
import bimoku.realtimecrawler.parse.UrlParse;
import bimoku.realtimecrawler.queue.UrlQueue;

/**
 * 抽取的线程
 * 
 * @author 梅良
 * @author LPM 跟新优化代码，，使用实现runnable的方法，方便使用线程池
 *
 */
public class ControlDdPromotion implements Runnable{

	private Parser parser;
	private String configPath;
	private String firstUrl;
	
	
	public ControlDdPromotion(String firstUrl,Parser parser,String configPath) {
		this.firstUrl = firstUrl;
		this.parser = parser;
		this.configPath = configPath;
		
	}

	public void run() {
		 //String filepath=new String();
		 UrlQueue SecondUrlQueue = new UrlQueue();
		
		 System.out.println(firstUrl);
		 String secondhtml = HttpConnectionManager.getHtml(firstUrl.trim());
		
		 try {
			UrlParse.getparse(SecondUrlQueue, secondhtml, PropertyUtil.readProperty(PropertyUtil.secondcategoryurl_promotion));
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
			 
			 String rankPage=new String(); //保存排行榜的不同页面
			
			 secondUrl = PropertyUtil.readProperty(PropertyUtil.defaultStr_promotion) + SecondUrlQueue.deQueue();
			 System.out.println(secondUrl);
			 String html = HttpConnectionManager.getHtml(secondUrl);
			/* try {
				UrlParse.getparse(rankPage, html,  PropertyUtil.readProperty(PropertyUtil.rankpage_onsale));
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			 while(true){
				 
				// String rankpageurl = rankPage.deQueue();
				// String thirdhtml = HttpConnectionManager.getHtml(rankpageurl);
				
				 try {
					UrlParse.getparse(bookUrlQueue, html, PropertyUtil.readProperty(PropertyUtil.bookurl_promotion));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 try {
					rankPage = PropertyUtil.readProperty(PropertyUtil.defaultStr_promotion) + UrlParse.getparse(html, PropertyUtil.readProperty(PropertyUtil.rankpage_promotion));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch(NullPointerException e){
					break;
				}
				 if(rankPage.equals(PropertyUtil.readProperty(PropertyUtil.defaultStr_promotion))){
					 break;
				 }
				html = HttpConnectionManager.getHtml(rankPage);
			 }
			
			 while(!bookUrlQueue.isQueueEmpty()){
						
				 String bookurl=new String();
				 bookurl = bookUrlQueue.deQueue();
				
				 System.out.println(bookurl);
				
				 String bookhtml = HttpConnectionManager.getHtml(bookurl.trim());
				 parser.parserForRealTime(bookhtml, RealTimeType.PROMOTION_RANK,bookurl.length()<150?bookurl:bookurl.substring(0, 150));

			 }
		 }
	}
	
	
}
