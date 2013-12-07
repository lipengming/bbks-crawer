package bimoku.realtimecrawler.CrawlerRealTime;


import java.net.MalformedURLException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bimoku.common.bean.RealTimeType;

import bimoku.extract.common.PropertyUtil;
import bimoku.extract.common.exception.ExtractException;
import bimoku.extract.parser.HttpConnectionManager;
import bimoku.extract.parser.Parser;
import bimoku.extract.parser.ParserDD;
import bimoku.extract.parser.ParserJD;
import bimoku.realtimecrawler.control.ControlAmazonNews;
import bimoku.realtimecrawler.control.ControlDdnews;
import bimoku.realtimecrawler.control.ControlDdPromotion;




import bimoku.realtimecrawler.parse.UrlParse;


import bimoku.realtimecrawler.queue.UrlQueue;


public class CrawlerJdPromotion {

	private static ApplicationContext ctx = null;

	public static ApplicationContext getContext() {
		if (ctx == null) {
			ctx = new ClassPathXmlApplicationContext("classpath:/beans.xml");
		}
		return ctx;
	}

	public static void extract(String configPath, Parser parser) throws MalformedURLException {
		
		PropertyUtil.getProperty(configPath);
	
		UrlQueue firstUrlQueue = new UrlQueue();
		
		String html = HttpConnectionManager.getHtml(PropertyUtil.readProperty(PropertyUtil.starturl_promotion));
		UrlParse.getparse(firstUrlQueue, html,
				PropertyUtil.readProperty(PropertyUtil.firstcategoryurl_promotion));
		
		System.out.println(firstUrlQueue.isQueueEmpty());
         
         
		
		ExecutorService pool = Executors.newFixedThreadPool(1);

		while (!firstUrlQueue.isQueueEmpty()) {
			String bookUrl = new String();			
			bookUrl = firstUrlQueue.deQueue();
			System.out.println(bookUrl);
			Pattern p1 = Pattern
					.compile("http://book.jd.com");
			Matcher m1 = p1.matcher(bookUrl);
			boolean result1 = m1.find();
			if (result1) {
				long t1 = System.currentTimeMillis();
				String bookhtml = HttpConnectionManager.getHtml(bookUrl.trim());
				 long t2 = System.currentTimeMillis();
				//接口要修改...要抓异常
				parser.parserForRealTime(bookhtml, RealTimeType.PROMOTION_RANK, bookUrl.length()<75?bookUrl:bookUrl.substring(0, 75));
			   
			    System.out.println(t2-t1);
			}
			
		}
	}
	public static void crawler() throws BeansException, MalformedURLException {
		extract("jdConfig.properties",
				(ParserJD) getContext().getBean("parserJD"));
	}
public static void main(String[] args) {
	try {
		crawler();
	} catch (BeansException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}
