package bimoku.crawler.parse;



import java.net.MalformedURLException;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bimoku.crawler.queue.UrlQueue;

public class UrlParse {
        public static void getparse(UrlQueue urlqueue,String html,String xpath) throws MalformedURLException{
        	

		    Document doc = Jsoup.parse(html);
        	
        	Elements linksElements =  doc.select(xpath);
        
        	for (Element ele : linksElements) {
        	
        		urlqueue.enQueue(ele.attr("href"));
        	}
        }
        public static String getparse(String html,String xpath) throws MalformedURLException{
        	  Document doc = Jsoup.parse(html);
          	
          	Element linksElements =  doc.select(xpath).last();
          	
          		
          		return(linksElements.attr("href"));
          	
        }
}
