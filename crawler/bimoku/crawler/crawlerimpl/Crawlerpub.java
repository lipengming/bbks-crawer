package bimoku.crawler.crawlerimpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bimoku.crawler.ipproxy.SetIpProxy;
import bimoku.crawler.common.DBUtils;
import bimoku.crawler.contro.Controlpub;
import bimoku.crawler.crawler.Crawler;
import bimoku.crawler.download.DownLoadFile;
import bimoku.crawler.parse.TitleParse;
import bimoku.crawler.parse.UrlParse;
import bimoku.crawler.queue.IpProxy;
import bimoku.crawler.queue.IpProxylist;
import bimoku.crawler.queue.TitleQueue;
import bimoku.crawler.queue.UrlQueue;
import bimoku.crawler.spider.HttpConnectionManager;
import bimoku.extract.parser.Parser;

public class Crawlerpub implements Crawler {
	public void crawler(Parser parser) {
		
		Properties p = new Properties();
		p = DBUtils.propertiesReader("chinapub.properties");
		
		UrlQueue firstUrlQueue = new UrlQueue();
		
		HttpConnectionManager httpConnectionManager = new HttpConnectionManager();

		

		/*
		 * String classifyHtml =
		 * HttpConnectionManager.getHtml(p.getProperty("classify"));
		 * 
		 * downfile.downloadFile(p.getProperty("classify"),classifyHtml,p.
		 * getProperty("classifyFile"));
		 */

		String html = "";
		try {
			html = httpConnectionManager.getHtml(p.getProperty("starturl"));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			UrlParse.getparse(firstUrlQueue, html,
					p.getProperty("firstcategoryurl"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		ExecutorService pool = Executors.newFixedThreadPool(20);
		while (!firstUrlQueue.isQueueEmpty()) {

			String firstUrl = new String();
			firstUrl = firstUrlQueue.deQueue();
			
			Controlpub controlpub = new Controlpub(firstUrl, p,parser);
			pool.execute(controlpub);
			
		}
		pool.shutdown();
		
	}
}
