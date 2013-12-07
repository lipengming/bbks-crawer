package bimoku.extract.parsercomment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import bimoku.crawler.ipproxy.SetIpProxy;
import bimoku.crawler.spider.HttpConnectionManager;
import bimoku.extract.common.PropertyUtil;

import com.bimoku.common.bean.Comment;

public class ParsercommentDouban {

	public static void main(String[] args) throws InterruptedException {
		SetIpProxy setIpProxy = new SetIpProxy();
		setIpProxy.setIpProxy();
		PropertyUtil.getProperty("doubanConfig.properties");
		List<Comment> comments = parsercomment("9787540463540","http://book.douban.com/subject/1200840/reviews");
		//TODO ..........INSERT TO DB
	}
	
	
	
	public static List<Comment> parsercomment(String isbn, String commenturl)
			throws InterruptedException {
		System.out.println("================"+commenturl);
		HttpConnectionManager httpcon = new HttpConnectionManager();
		// Comment comment = new Comment();
		ArrayList<Element> commentlist = new ArrayList<Element>(); // 用户名
		int count = 5;// 最多抓取的页数
		String com_nextPage = commenturl;
		while (count > 0) {
			String commenthtml = httpcon.getHtml(com_nextPage);
			// System.out.println(commenthtml);
			Document doc = Jsoup.parse(commenthtml);
			Elements com_item = doc.select(PropertyUtil.readProperty(PropertyUtil.com_item));
			for (Element ele : com_item) {
				//System.out.println("++++++"+ele.html());
				commentlist.add(ele);
			}

			try {
				Element nextpage = doc.select(
						PropertyUtil.readProperty(PropertyUtil.com_nextPage))
						.first();
				com_nextPage = nextpage.attr("href");
				
				if(com_nextPage==null||"".equals(com_nextPage)){
					break;
				}
			} catch (Exception e) {
				break;
			}
			count--;
		}
		
		return Todetail(isbn, commentlist);
	}

	private static List<Comment> Todetail(String isbn, ArrayList commentlist) {
		List<Comment> comms = new ArrayList<Comment>();
		System.out.println("++++++");
		Iterator<?> it1 = commentlist.iterator();
		while (it1.hasNext()) {
			Element doc = (Element) it1.next();
			//System.out.println((String) it1.next());
			Element com_user = doc.select(
					PropertyUtil.readProperty(PropertyUtil.com_user)).first();
			Element com_avatar = doc.select(
					PropertyUtil.readProperty(PropertyUtil.com_avatar)).first();
			Element com_content = doc.select(
					PropertyUtil.readProperty(PropertyUtil.com_content))
					.first();
			Element create_at = doc.select(
					PropertyUtil.readProperty(PropertyUtil.create_at)).first();
			Comment comment = new Comment();
			comment.setIsbn(isbn);
			//System.out.println(com_user.text()+"++++++");
			try{
				comment.setCom_user(com_user.text().substring(0, com_user.text().length()>45?45:com_user.text().length()));
			
			//System.out.println(com_user.text());
			comment.setCom_avatar(com_avatar.attr("src").substring(0,com_avatar.attr("src").length()>150?150:com_avatar.attr("src").length()));
			comment.setCom_content(com_content.text());
			 //System.out.println(comment.getCom_content());
			comment.setCreate_at(create_at.text().substring(0,create_at.text().length()>45?45:create_at.text().length()));
			}catch(NullPointerException e){
					
				break;
			}
			comment.setCom_zhandian("Douban");
			// TODO
			comms.add(comment);		
		}
		return comms;
	}
}
