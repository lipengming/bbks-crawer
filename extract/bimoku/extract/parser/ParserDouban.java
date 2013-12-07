package bimoku.extract.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;



import bimoku.extract.common.PropertyUtil;
import bimoku.extract.parsercomment.ParsercommentDD;
import bimoku.extract.parsercomment.ParsercommentDouban;

import com.bimoku.common.bean.BookDB;
import com.bimoku.common.bean.BookDetail;
import com.bimoku.common.bean.Comment;
import com.bimoku.integrate.DBIntegrated;
import com.bimoku.integrate.Integrated;
import com.bimoku.repository.dao.CommentDao;

@Component("parserDouban")
public class ParserDouban extends Parser {
	static CommentDao dao;
	static{
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/beans.xml");
		dao = ctx.getBean("commentDao",CommentDao.class);
	}
	@Autowired
	DBIntegrated doubanIntegrated;

	@Override
	protected Integrated getIntegratedDao() {
		if (doubanIntegrated == null)
			throw new RuntimeException("spring bean 实例化出错");
		return doubanIntegrated;
	}

	@Override
	protected BookDetail fieldFilter(Map<String, String> map) {
		BookDB bookdouban = new BookDB();

		
		String authorIntro = map.get(PropertyUtil.AUTHOR_INTRO);
		String bookName = map.get(PropertyUtil.BOOKNAME);
		String cover_pic = map.get(PropertyUtil.COVER_PIC);
		 String directory = map.get(PropertyUtil.DIRECTORY);
		 String outline = map.get(PropertyUtil.BOOK_DESCIPTION);
		 String CLASSFY = map.get(PropertyUtil.CLASSFY);
		// String press = map.get(PropertyUtil.PRESS);
		// String price = map.get(PropertyUtil.PRICE);
		// String PUBLISHED_PRICE = map.get(PropertyUtil.PUBLISHED_PRICE);
		// String translator = map.get(PropertyUtil.TRANSLATOR);
		// String version = map.get(PropertyUtil.VERSION);
		String intro_clearfix = map.get(PropertyUtil.intro_clearfix);
		String commenturl = map.get(PropertyUtil.COMMENTURL);
		// TODO 这个过程多处理

		
		String author = "";
		String isbn = "";
		String press = "";
		String translator = "";
		String version = "";
		Double pric = 0.0;
		Double pub_pric = 0.0;

		// System.out.println(intro_clearfix);
		String[] infoparam = Patternmatch_Douban
				.patternmatchContent(intro_clearfix);

		try {
			author = infoparam[0].trim();
			author = author.substring(0, author.length() > 45 ? 44
					: author.length());
		} catch (NullPointerException e) {
			author = "";
		}

		try {
			press = infoparam[1].trim();
			press = press.substring(0, press.length() > 45 ? 44
					: press.length());
		} catch (NullPointerException e) {
			press = "";
		}
		try {
			
			pub_pric = Double.valueOf(infoparam[2]);
		} catch (Exception e) {
			pub_pric = 0.0;
		} 

		try {
			isbn = infoparam[3].trim();
		} catch (Exception e) {
			isbn = "";
		}
		try {
			translator = infoparam[4].trim();
			translator = translator.substring(0, translator.length() > 45 ? 44
					: translator.length());
		} catch (Exception e) {
			translator = "";
		}
		try {
			version = infoparam[5].trim();
			version = version.substring(0, version.length() > 20 ? 19
					: version.length());
		} catch (Exception e) {
			version = "";
		}
		try {
			cover_pic = cover_pic.substring(0, cover_pic.length() > 150 ? 150
					: cover_pic.length());
		} catch (Exception e) {
			cover_pic = "";
		}
		try {
			bookName = bookName.substring(0, bookName.length() > 45 ? 44
					: bookName.length());
		} catch (Exception e) {
			bookName = "";
		}
	
		try {
			CLASSFY = CLASSFY.substring(0, CLASSFY.length() > 50 ? 49
					: CLASSFY.length());
		} catch (Exception e) {
			CLASSFY = "";
		}
		Long t1 = System.currentTimeMillis();
		System.out.println(isbn+"+++");
		if(!"".equals(isbn)&&isbn !=null&&!"".equals(commenturl)&&commenturl!= null)
		{
			try {
			List<Comment> comments = ParsercommentDouban.parsercomment(isbn,commenturl);
			int[] rscount = dao.batchSave(comments);
			if(rscount.length > 0){
				//有评论进去了
				bookdouban.setHascomment(1);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
		System.out.println("TIMES: "+(System.currentTimeMillis() - t1));
	
		bookdouban.setAuthor(author);
		bookdouban.setAuthorIntro(authorIntro);
		bookdouban.setBookName(bookName);
		bookdouban.setCover_pic(cover_pic);
		 bookdouban.setDirectory(directory);
		bookdouban.setIsbn(isbn);
		bookdouban.setPress(press);
		bookdouban.setPrice(pric);
		bookdouban.setTranslator(translator);
		 bookdouban.setVersion(version);
		bookdouban.setPub_price(pub_pric);
		bookdouban.setOutLine(outline);
		bookdouban.setCatelog(CLASSFY);
		//System.out.println(bookdouban.toString());

		return bookdouban;
	}

	@Override
	protected Map<String, String> getElementsInfo(String html,boolean isFile)
			throws Exception {
		Document doc;
		Map<String, String> map = new HashedMap();
		
		if(isFile){
			File input = new File(html);
			 doc = Jsoup.parse(input, "UTF-8");
		}else{
		 doc = Jsoup.parse(html);
		}

		map.put(PropertyUtil.BOOKNAME,
				doc.select(PropertyUtil.readProperty(PropertyUtil.BOOKNAME))
						.first() == null ? "" : doc
						.select(PropertyUtil
								.readProperty(PropertyUtil.BOOKNAME)).first()
						.text());
		// map.put(PropertyUtil.AUTHOR_TRANSLATOR,
		// doc.select(PropertyUtil.readProperty(PropertyUtil.AUTHOR_TRANSLATOR)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.AUTHOR_TRANSLATOR)).first().text());
		// map.put(PropertyUtil.BOOK_DESCIPTION,
		// doc.select(PropertyUtil.readProperty(PropertyUtil.BOOK_DESCIPTION)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.BOOK_DESCIPTION)).first().text());
		// map.put(PropertyUtil.PRESS,
		// doc.select(PropertyUtil.readProperty(PropertyUtil.PRESS)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.PRESS)).first().text());
		// map.put(PropertyUtil.VERSION,
		// doc.select(PropertyUtil.readProperty(PropertyUtil.VERSION)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.VERSION)).first().text());
		// TODO map.put(PropertyUtil.ITEM_ID,
		// doc.select(PropertyUtil.readProperty(PropertyUtil.ITEM_ID)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.ITEM_ID)).first().text());
		// map.put(PropertyUtil.ISBN,
		// doc.select(PropertyUtil.readProperty(PropertyUtil.ISBN)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.ISBN)).first().text());
		map.put(PropertyUtil.intro_clearfix,
				doc.select(
						PropertyUtil.readProperty(PropertyUtil.intro_clearfix))
						.first() == null ? "" : doc
						.select(PropertyUtil
								.readProperty(PropertyUtil.intro_clearfix))						.first().text());
		// map.put(PropertyUtil.PRICE,
		// doc.select(PropertyUtil.readProperty(PropertyUtil.PRICE)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.PRICE)).first().text());
		// map.put(PropertyUtil.PUBLISHED_PRICE,
		// doc.select(PropertyUtil.readProperty(PropertyUtil.PUBLISHED_PRICE)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.PUBLISHED_PRICE)).first().text());

		// Elements linksElements =
		// doc.select(PropertyUtil.readProperty(PropertyUtil.CLASSFY));
		// String CLASSFY = "";
		// for (Element ele : linksElements) {
		// CLASSFY += ele.text() + ">";
		// }
		 map.put(PropertyUtil.CLASSFY, doc.select(PropertyUtil.readProperty(PropertyUtil.CLASSFY)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.CLASSFY)).first().text());
		 map.put(PropertyUtil.COMMENTURL, doc.select(PropertyUtil.readProperty(PropertyUtil.COMMENTURL)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.COMMENTURL)).first().attr("href"));
		map.put(PropertyUtil.COVER_PIC,
				doc.select(PropertyUtil.readProperty(PropertyUtil.COVER_PIC))
						.first() == null ? "" : doc
						.select(PropertyUtil
								.readProperty(PropertyUtil.COVER_PIC)).first()
						.attr("href"));
		
		
		String[] param = getProductdescriptionAll(doc,
				PropertyUtil.readProperty(PropertyUtil.PRODUCTDESCRIPTION_KEY),
				PropertyUtil
						.readProperty(PropertyUtil.PRODUCTDESCRIPTION_VALUE));
		map.put(PropertyUtil.BOOK_DESCIPTION, param[0]);
		map.put(PropertyUtil.AUTHOR_INTRO, param[1]);
		map.put(PropertyUtil.DIRECTORY, param[2]);
		
		// map.put(PropertyUtil.ATTACH_IMAGE_SHOW,
		// doc.select(PropertyUtil.readProperty(PropertyUtil.ATTACH_IMAGE_SHOW)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.ATTACH_IMAGE_SHOW)).first().text());
		// map.put(PropertyUtil.COMMENTURL,
		// doc.select(PropertyUtil.readProperty(PropertyUtil.COMMENTURL)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.COMMENTURL)).first().text());

		return map;
	}
	protected String[] getProductdescriptionAll(Document doc,
			String productdescription_key, String productdescription_value) {
		String[] param = new String[3];
		ArrayList<String> prodes_key = new ArrayList<String>();
		ArrayList<Element> prodes_value = new ArrayList<Element>();
		try {
			
			Elements linksElementkey = doc.select(productdescription_key);
			for (Element ele : linksElementkey) {
				prodes_key.add(ele.text());
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Elements linksElementvalue = doc.select(productdescription_value);
			for (Element ele : linksElementvalue) {
				prodes_value.add(ele);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		int index=-1;
		try {
			 index = prodes_key.indexOf("内容简介");
			if(prodes_value.get(index).select("span.short>div.intro")!=null)
			{
				param[0] = prodes_value.get(index).select("span.short>div.intro").first().text().replaceAll("\\(展开全部\\)", "");
			}else{
				param[0] = prodes_value.get(index).select("div.intro").first().text();
			}
			
		}catch(NullPointerException e){
			param[0] = prodes_value.get(index).select("div.intro").first().text();
		}
			catch (IndexOutOfBoundsException e) {
		
			param[0] = "";
		}
		
		
		try {
			 index = prodes_key.indexOf("作者简介");
			if(prodes_value.get(index).select("span.short>div.intro")!=null)
			{
				param[1] = prodes_value.get(index).select("span.short>div.intro").first().text().replaceAll("\\(展开全部\\)", "");
			}else{
				param[1] = prodes_value.get(index).select("div.intro").first().text();
			}
		} catch(NullPointerException e){
			param[1] = prodes_value.get(index).select("div.intro").first().text();
		}catch (IndexOutOfBoundsException e) {
			param[1] = "";
		}
		try {
			 index = prodes_key.indexOf("目录");
			
				param[2] = prodes_value.get(index).text().replaceAll("\\(更多\\)", "");
			
			
		} catch (IndexOutOfBoundsException e) {
			param[2] = "";
			
		}
		return param;
	}
}