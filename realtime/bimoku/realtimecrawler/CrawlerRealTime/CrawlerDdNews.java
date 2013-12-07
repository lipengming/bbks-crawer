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
import bimoku.realtimecrawler.control.ControlDdnews;




import bimoku.realtimecrawler.parse.UrlParse;


import bimoku.realtimecrawler.queue.UrlQueue;


public class CrawlerDdNews {

	private static ApplicationContext ctx = null;

	public static ApplicationContext getContext() {
		if (ctx == null) {
			ctx = new ClassPathXmlApplicationContext("classpath:/beans.xml");
		}
		return ctx;
	}

	public static void extract(String configPath, Parser parser) throws MalformedURLException {
		/*
		 * File file = new
		 * File(PropertyUtil.getProperty(configPath).getProperty("directory"));
		 * File[] files = file.listFiles();
		 */
		PropertyUtil.getProperty(configPath);
		//DownLoadFile downfile = new DownLoadFile();
		UrlQueue firstUrlQueue = new UrlQueue();
		//TitleQueue firstTitleQueue = new TitleQueue();
		String html = HttpConnectionManager.getHtml(PropertyUtil.readProperty(PropertyUtil.starturl_newbook));
		UrlParse.getparse(firstUrlQueue, html,
				PropertyUtil.readProperty(PropertyUtil.firstcategoryurl_newbook));
		/*TitleParse.getparse(firstTitleQueue, html,
				p.getProperty("firstcategorytitle"));*/
		System.out.println(firstUrlQueue.isQueueEmpty());
         
         
		// 创建一个固定大小[10]的线程池
		ExecutorService pool = Executors.newFixedThreadPool(1);

		while (!firstUrlQueue.isQueueEmpty()) {
			String firstUrl = new String();
			//String fitstTitle = new String();
			firstUrl = firstUrlQueue.deQueue();
			//System.out.println(firstUrl);
			ControlDdnews controldd = new ControlDdnews(firstUrl, parser, configPath);
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
