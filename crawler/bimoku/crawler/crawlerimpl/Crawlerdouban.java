package bimoku.crawler.crawlerimpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bimoku.crawler.contro.Controldouban;
import bimoku.crawler.crawler.Crawler;
import bimoku.extract.parser.Parser;

/**
 * 
 * @author meiliang 8/17
 * 
 * 
 */

public class Crawlerdouban implements Crawler {
	public void crawler(Parser parser) {

		// 豆瓣的数量太大，不宜使用线程池。
		// 线程池，是将所有的任务load到一个队列中，这个队列不宜太过庞大，所以，使用单个线程就好。
		 ExecutorService pool = Executors.newFixedThreadPool(10);
		for (int index = 100 * 10000; index < 1000 * 10000; index += 20 * 10000) {
			int startindex = index;
			int endindex = index + 199999;
			Controldouban controldouban = new Controldouban(startindex,
					endindex, parser);
			 pool.execute(controldouban);
			//controldouban.run();
		}
		 pool.shutdown();
	}

}
