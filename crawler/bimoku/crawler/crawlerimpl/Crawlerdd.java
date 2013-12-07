package bimoku.crawler.crawlerimpl;

import java.net.MalformedURLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import bimoku.crawler.common.DBUtils;
import bimoku.crawler.contro.Controldd;
import bimoku.crawler.crawler.Crawler;
import bimoku.crawler.parse.UrlParse;
import bimoku.crawler.queue.UrlQueue;
import bimoku.crawler.spider.HttpConnectionManager;
import bimoku.extract.parser.Parser;

public class Crawlerdd implements Crawler {
public  void crawler(Parser parser)  {
	
	Properties p = new Properties();
	p=DBUtils.propertiesReader("dangdang.properties");
	
	UrlQueue firstUrlQueue = new UrlQueue();
	
	HttpConnectionManager httpConnectionManager = new HttpConnectionManager();

	 
	 String html = "";
	try {
		html = httpConnectionManager.getHtml(p.getProperty("starturl"));
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 
	 try {
		UrlParse.getparse(firstUrlQueue, html, p.getProperty("firstcategoryurl"));
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	 ExecutorService pool = Executors.newFixedThreadPool(30);
	 
	 while(!firstUrlQueue.isQueueEmpty()){
		
		 
		 String firstUrl= new String();
			 
		 firstUrl= p.getProperty("defaultStr")+firstUrlQueue.deQueue();
		
		 Controldd controldd=new Controldd(firstUrl,p,parser);
		
		 pool.execute(controldd);
	 }
	 pool.shutdown();
}
}
