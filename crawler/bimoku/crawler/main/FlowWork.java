package bimoku.crawler.main;

import java.util.Scanner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import bimoku.crawler.ipproxy.SetIpProxy;
import bimoku.crawler.crawler.Crawler;
import bimoku.crawler.crawlerimpl.Crawleramazon;
import bimoku.crawler.crawlerimpl.Crawlerdd;
import bimoku.crawler.crawlerimpl.Crawlerdouban;
import bimoku.crawler.crawlerimpl.Crawlerjd;
import bimoku.crawler.crawlerimpl.Crawlerpub;
import bimoku.extract.common.PropertyUtil;
import bimoku.extract.parser.ParserAmazon;
import bimoku.extract.parser.ParserChinapub;
import bimoku.extract.parser.ParserDD;
import bimoku.extract.parser.ParserDouban;
import bimoku.extract.parser.ParserJD;
import bimoku.realtimecrawler.CrawlerRealTime.CrawlerAmazonNews;
import bimoku.realtimecrawler.CrawlerRealTime.CrawlerAmazonPromotion;
import bimoku.realtimecrawler.CrawlerRealTime.CrawlerDdNews;
import bimoku.realtimecrawler.CrawlerRealTime.CrawlerDdOnSale;
import bimoku.realtimecrawler.CrawlerRealTime.CrawlerDdPromotion;
import bimoku.realtimecrawler.CrawlerRealTime.CrawlerJdOnSale;
import bimoku.realtimecrawler.CrawlerRealTime.CrawlerJdPromotion;

/**
 * @author meiliang
 * 
 */
public class FlowWork {
	private static ApplicationContext ctx = null;

	public static ApplicationContext getContext() {
		if (ctx == null) {
			ctx = new ClassPathXmlApplicationContext("classpath:/beans.xml");
		}
		return ctx;
	}

	public static void main(String[] args) throws Exception {

		//Crawler crawler = new Crawlerdouban("doubanConfig.properties",(ParserDouban) getContext().getBean("parserDouban"));
		System.out.println("******************bimoku***************");
		System.out.println("请输入你所要抓取的目标信息的代号：");
		System.out.println("【基本数据部分：1-豆瓣，2-亚马逊，3-当当，4-京东,5-中国互动】");
		System.out.println("【新书排行榜数据部分：6-亚马逊，7-当当】");
		System.out.println("【特价排行榜数据部分：8-当当，9-亚马逊，10-京东】");
		System.out.println("【促销排行榜数据部分：11-当当，12-京东】");
		System.out.println("******************bimoku***************");
		//Scanner in = new Scanner(System.in);
		
		int zhandian = 3;//Integer.parseInt(args[0]);//in.nextInt();
		
		switch(zhandian){
				case 1:douban();break;
				case 2:amazon();break;
				case 3:dd();break;
				case 4:jd();break;
				case 5:chinapub();break;
				
				case 6:CrawlerAmazonNews.crawler();break;
				case 7:CrawlerDdNews.crawler();break;
				case 8:CrawlerDdPromotion.crawler();break;
				case 9:CrawlerAmazonPromotion.crawler();break;
				case 10:CrawlerJdPromotion.crawler();break;
				case 11:CrawlerDdOnSale.crawler();break;
				case 12:CrawlerJdOnSale.crawler();break;
				default:
					System.out.println("输入格式错误！");
					System.exit(1);
					break;
		}
	}
	private static void douban(){
		Crawler crawler = new Crawlerdouban();
		SetIpProxy setIpProxy = new SetIpProxy();
		setIpProxy.setIpProxy();
		PropertyUtil.getProperty("doubanConfig.properties");
		crawler.crawler((ParserDouban) getContext().getBean("parserDouban"));	
	}
	private static void amazon(){
		Crawler crawler = new Crawleramazon();
		SetIpProxy setIpProxy = new SetIpProxy();
		setIpProxy.setIpProxy();
		PropertyUtil.getProperty("amazonConfig.properties");
		crawler.crawler((ParserAmazon) getContext().getBean("parserAmazon"));	
	}
	private static void dd(){
		Crawler crawler = new Crawlerdd();
		SetIpProxy setIpProxy = new SetIpProxy();
		setIpProxy.setIpProxy();
		PropertyUtil.getProperty("ddConfig.properties");
		crawler.crawler((ParserDD) getContext().getBean("parserDD"));	
	}
	private static void jd(){
		Crawler crawler = new Crawlerjd();
		SetIpProxy setIpProxy = new SetIpProxy();
		setIpProxy.setIpProxy();
		PropertyUtil.getProperty("jdConfig.properties");
		crawler.crawler((ParserJD) getContext().getBean("parserJD"));	
	}
	private static void chinapub(){
		Crawler crawler = new Crawlerpub();
		SetIpProxy setIpProxy = new SetIpProxy();
		setIpProxy.setIpProxy();
		PropertyUtil.getProperty("PubConfig.properties");
		crawler.crawler((ParserChinapub) getContext().getBean("parserChinapub"));	
	}
}
