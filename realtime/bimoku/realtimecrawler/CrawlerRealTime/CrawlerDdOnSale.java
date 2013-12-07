package bimoku.realtimecrawler.CrawlerRealTime;


import java.net.MalformedURLException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import bimoku.extract.common.PropertyUtil;
import bimoku.extract.parser.HttpConnectionManager;
import bimoku.extract.parser.Parser;
import bimoku.extract.parser.ParserDD;
import bimoku.realtimecrawler.control.ControlAmazonNews;
import bimoku.realtimecrawler.control.ControlDdOnSale;
import bimoku.realtimecrawler.control.ControlDdnews;
import bimoku.realtimecrawler.control.ControlDdPromotion;




import bimoku.realtimecrawler.parse.UrlParse;


import bimoku.realtimecrawler.queue.UrlQueue;
/*及时数据
 * 当当网----销售排行榜
 * 
 * 
*/
public class CrawlerDdOnSale {

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
		
		String html = HttpConnectionManager.getHtml(PropertyUtil.readProperty(PropertyUtil.starturl_onsale));
		UrlParse.getparse(firstUrlQueue, html,
				PropertyUtil.readProperty(PropertyUtil.firstcategoryurl_onsale));
	
		System.out.println(firstUrlQueue.isQueueEmpty());
         
		firstUrlQueue.deQueue();
		firstUrlQueue.deQueue();
		// 创建一个固定大小[10]的线程池
		ExecutorService pool = Executors.newFixedThreadPool(1);

		while (!firstUrlQueue.isQueueEmpty()) {
			String firstUrl = new String();
			
			firstUrl = firstUrlQueue.deQueue();
		
			ControlDdOnSale controldd = new ControlDdOnSale(firstUrl, parser, configPath);
			// 把任务放到线程池的处理队列里面，等待处理
			pool.execute(controldd);
		}
		// 处理完成后，关闭线程池
		pool.shutdown();
	}

	public static void crawler() throws BeansException, MalformedURLException {
		extract("ddConfig.properties",
				(ParserDD) getContext().getBean("parserDD"));
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
