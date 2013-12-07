package bimoku.crawler.crawlerimpl;


import java.net.MalformedURLException;
import java.util.Properties;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bimoku.crawler.common.DBUtils;
import bimoku.crawler.contro.Controljd;
import bimoku.crawler.crawler.Crawler;
import bimoku.crawler.download.DownLoadFile;
import bimoku.crawler.parse.UrlParse;
import bimoku.crawler.queue.UrlQueue;
import bimoku.crawler.spider.HttpConnectionManager;
import bimoku.extract.parser.Parser;


public class Crawlerjd implements Crawler{
public  void crawler(Parser parser){
	
	Properties p = new Properties();
	p=DBUtils.propertiesReader("JD.properties");
	
	
	
	UrlQueue firstUrlQueue = new UrlQueue();
	
	HttpConnectionManager httpConnectionManager=new HttpConnectionManager();
	
	
	
		
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
	 
	
	
		ExecutorService pool = Executors.newFixedThreadPool(20);
	 while(!firstUrlQueue.isQueueEmpty()){
		
		 
		 String firstUrl= new String();
		 
		 firstUrl=firstUrlQueue.deQueue();
		
		 Controljd controljd=new Controljd(firstUrl,p,parser);
		 pool.execute(controljd);
			
	 }
	 pool.shutdown();
		
}
}
