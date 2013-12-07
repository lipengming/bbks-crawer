package bimoku.crawler.parse;



import java.net.MalformedURLException;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bimoku.crawler.queue.TitleQueue;


public class TitleParse {
    public static void getparse(TitleQueue titlequeue,String html,String xpath) throws MalformedURLException{
    	

	    Document doc = Jsoup.parse(html);
    	
    	Elements linksElements =  doc.select(xpath);
    	for (Element ele : linksElements) {
    		
    		titlequeue.enQueue(ele.text().replaceAll("[\\?/:*|<>\"]", "_"));
    	}
    }
 public static String getparse(String html,String xpath) throws MalformedURLException{
    	
         String title = new String();
	    Document doc = Jsoup.parse(html);
    	
    	Element linksElements =  doc.select(xpath).last();
    	
    		
    		title=linksElements.text().replaceAll("[\\?/:*|<>\"]", "_");
    		return title;
    	
    }
}
