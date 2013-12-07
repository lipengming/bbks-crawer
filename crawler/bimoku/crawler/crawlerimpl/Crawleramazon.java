package bimoku.crawler.crawlerimpl;

import java.net.MalformedURLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import bimoku.crawler.common.DBUtils;
import bimoku.crawler.contro.Controlamazon;
import bimoku.crawler.crawler.Crawler;
import bimoku.crawler.download.DownLoadFile;
import bimoku.crawler.parse.TitleParse;
import bimoku.crawler.parse.UrlParse;
import bimoku.crawler.queue.TitleQueue;
import bimoku.crawler.queue.UrlQueue;
import bimoku.crawler.spider.HttpConnectionManager;
import bimoku.extract.parser.Parser;


public class Crawleramazon implements Crawler{
public  void crawler(Parser parser) {
	
	Properties p = new Properties();
	p=DBUtils.propertiesReader("amazon.properties");
	UrlQueue firstUrlQueue = new UrlQueue();
	
	HttpConnectionManager httpConnectionManager = new HttpConnectionManager();

	/*String classifyHtml = "";
	try {
		classifyHtml = httpConnectionManager.getHtml(p.getProperty("classify"));
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}*/
	 	 
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
	
	//创建一个固定大小的线程池
			ExecutorService pool = Executors.newFixedThreadPool(20);
	 while(!firstUrlQueue.isQueueEmpty()){
		
		 
		 String firstUrl= new String();
			 
		 firstUrl=p.getProperty("defaultStr").trim()+firstUrlQueue.deQueue();
		
		 Controlamazon controlamazon=new Controlamazon(firstUrl,p,parser);
		 pool.execute(controlamazon);
			//将任务放入线程池
	 }
	 pool.shutdown();
		//任务完成关闭线程池
}
}
