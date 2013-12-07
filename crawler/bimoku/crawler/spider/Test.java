package bimoku.crawler.spider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import bimoku.crawler.ipproxy.SetIpProxy;
import bimoku.crawler.download.DownLoadFile;
import bimoku.extract.parser.ParserJD;



/** test
  * @author meiliang
  * @createtime
  */
public class Test {
     
     
    /* public static void main(String[] args){
         HttpConnectionManager httpConnectionManager = new HttpConnectionManager();
         String html = httpConnectionManager.getHtml("http://www.qq.com");
         Document doc = Jsoup.parse(html);
         Elements newsList = doc.select("[class=ft fl]").select("ul").select("li").select("a");
         for (Element element : newsList) {
             System.out.println(element.attr("href") + "----" + element.text());
         }
     }*/
	private static ApplicationContext ctx = null;

	public static ApplicationContext getContext() {
		if (ctx == null) {
			ctx = new ClassPathXmlApplicationContext("classpath:/beans.xml");
		}
		return ctx;
	}
	public static void main(String[] args) throws InterruptedException {
		String url="http://item.jd.com/16002356.html";
		 HttpConnectionManager httpConnectionManager = new HttpConnectionManager();
		 SetIpProxy setIpProxy = new SetIpProxy();
		 setIpProxy.setIpProxy();
		 String html = httpConnectionManager.getHtml(url);
		 System.out.println(html);
		 ParserJD  parser = (ParserJD) getContext().getBean("parserJD");
		 parser.parser(html, url.length()<150?url:url.substring(0, 150));
          DownLoadFile downfile=new DownLoadFile();
         downfile.downloadFile(url,html,"G:/");
         
	}
 }