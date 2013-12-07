package bimoku.realtimecrawler.parse;



import java.net.MalformedURLException;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bimoku.realtimecrawler.queue.TitleQueue;


public class TitleParse {
    public static void getparse(TitleQueue titlequeue,String html,String xpath) throws MalformedURLException{
    	

	    Document doc = Jsoup.parse(html);
    	
    	Elements linksElements =  doc.select(xpath);
    	for (Element ele : linksElements) {
    		
    		titlequeue.enQueue(ele.text().replaceAll("[\\?/:*|<>\"]", "_"));
    	}
    }
}
