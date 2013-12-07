package bimoku.crawler.contro;

import java.net.MalformedURLException;
import java.util.Properties;

import bimoku.crawler.download.DownLoadFile;
import bimoku.crawler.parse.TitleParse;
import bimoku.crawler.parse.UrlParse;
import bimoku.crawler.queue.IpProxylist;
import bimoku.crawler.queue.TitleQueue;
import bimoku.crawler.queue.UrlQueue;
import bimoku.crawler.spider.HttpConnectionManager;
import bimoku.extract.parser.Parser;

public class Controljd implements Runnable
{
private String firstUrl;
private Parser parser;
private Properties p;

public Controljd(String firstUrl ,Properties p,Parser parser )
{
this.firstUrl= firstUrl;
this.parser= parser;
this.p=p;
}
public void run() {

	System.out.println(firstUrl);
	
	String filepath=new String();
	DownLoadFile seconddownfile=new DownLoadFile();
	 UrlQueue SecondUrlQueue = new UrlQueue();
	 TitleQueue SecondTitleQueue= new TitleQueue(); 
	 String secondhtml = null;
	 HttpConnectionManager httpConnectionManager=new HttpConnectionManager();
	try {
		secondhtml = httpConnectionManager.getHtml(firstUrl);
	} catch (InterruptedException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}
	
	 try {
		UrlParse.getparse(SecondUrlQueue, secondhtml, p.getProperty("secondcategoryurl"));
	} catch (MalformedURLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	 
	
	 
	 while(!SecondUrlQueue.isQueueEmpty()){
		 String secondUrl= new String();		
		 UrlQueue bookUrlQueue = new UrlQueue();
		 
		 String nextPage=new String();
		 
		 secondUrl = SecondUrlQueue.deQueue();
		 nextPage = secondUrl;
		 while(!nextPage.equals(p.getProperty("defaultStr").trim())){
			 String thirdhtml = null;
			try {
				thirdhtml = httpConnectionManager.getHtml(nextPage);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			 try {
				UrlParse.getparse(bookUrlQueue, thirdhtml, p.getProperty("bookurl"));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 try {
				nextPage= p.getProperty("defaultStr").trim()+UrlParse.getparse(thirdhtml, p.getProperty("nextpage"));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 catch(NullPointerException e){
				 break;
			 }
		 }
		 
		 while(!bookUrlQueue.isQueueEmpty()){
			 
			
			 String bookurl=new String();
			 bookurl = bookUrlQueue.deQueue();
			 String bookhtml = null;
			try {
				bookhtml = httpConnectionManager.getHtml(bookurl);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			parser.parser(bookhtml, bookurl.length()<150?bookurl:bookurl.substring(0, 150));
		 }
	 }
}
}

