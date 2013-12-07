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
import bimoku.extract.parsercomment.ParsercommentJD;

import com.bimoku.common.bean.BookDetail;
import com.bimoku.common.bean.BookJD;
import com.bimoku.common.bean.Comment;
import com.bimoku.integrate.Integrated;
import com.bimoku.integrate.JDIntegrated;
import com.bimoku.repository.dao.CommentDao;

@Component("parserJD")
public class ParserJD extends Parser {
	static CommentDao dao;
	static {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"classpath:/beans.xml");
		dao = ctx.getBean("commentDao", CommentDao.class);
	}
	@Autowired
	JDIntegrated JDIntegrated;

	@Override
	protected Integrated getIntegratedDao() {
		if (JDIntegrated == null)
			throw new RuntimeException("spring bean 实例化出错");
		return JDIntegrated;
	}

	@Override
	protected BookDetail fieldFilter(Map<String, String> map) {
		BookJD bookjd = new BookJD();

		String author = map.get(PropertyUtil.AUTHOR);
		String authorIntro = map.get(PropertyUtil.AUTHOR_INTRO); // 作者简介
		String bookName = map.get(PropertyUtil.BOOKNAME);
		String cover_pic = map.get(PropertyUtil.COVER_PIC);
		String directory = map.get(PropertyUtil.DIRECTORY); // 目录
		String bookdis = map.get(PropertyUtil.BOOK_DESCIPTION); // 目录
		String isbn = map.get(PropertyUtil.ISBN);
		String press = map.get(PropertyUtil.PRESS);
		String price = map.get(PropertyUtil.PRICE);
		String PUBLISHED_PRICE = map.get(PropertyUtil.PUBLISHED_PRICE);
		String translator = map.get(PropertyUtil.TRANSLATOR);
		String version = map.get(PropertyUtil.VERSION);

		String CLASSFY = map.get(PropertyUtil.CLASSFY);
		String item_id = map.get(PropertyUtil.ITEM_ID);
		item_id = item_id.replaceAll("商品编码：", "").trim();
		String comment_url = PropertyUtil.readProperty(PropertyUtil.COMMENTURL)
				+ item_id + "-0-1-0.html";
		// TODO 这个过程多处理

		Double pric = 0.0;
		Double pub_pric = 0.0;

		try {
			author = author.substring(0,
					author.length() > 45 ? 44 : author.length());
		} catch (Exception e) {
			author = "";
		}
		try {
			press = press.substring(0,
					press.length() > 45 ? 44 : press.length());
		} catch (Exception e) {
			press = "";
		}
		try {
			version = version.substring(0,
					version.length() > 20 ? 19 : version.length());
		} catch (Exception e) {
			version = "";
		}
		try {
			translator = translator.substring(0, translator.length() > 45 ? 44
					: translator.length());
		} catch (Exception e) {
			translator = "";
		}

		try {

			price = price.replaceAll("￥", "").trim();
			pric = Double.valueOf(price);
		} catch (NumberFormatException e) {
			pric = 0.0;
		}

		try {
			PUBLISHED_PRICE = PUBLISHED_PRICE.replaceAll("￥", "").trim();
			pub_pric = Double.valueOf(PUBLISHED_PRICE);
		} catch (NumberFormatException e) {
			pub_pric = 0.0;
		}

		try {
			cover_pic = cover_pic.substring(0, cover_pic.length() > 150 ? 150
					: cover_pic.length());
			// cover_pic = cover_pic.replaceAll("http://", "").trim();
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
			CLASSFY = CLASSFY.substring(0,
					CLASSFY.length() > 50 ? 49 : CLASSFY.length());
		} catch (Exception e) {
			CLASSFY = "";
		}
		System.out.println(isbn + "===" + item_id);
		if (isbn != null && !"".equals(isbn) && !"".equals(item_id)
				&& item_id != null) {
			try {
				System.out.println("++++++");
				List<Comment> comments = ParsercommentJD.parsercomment(isbn,
						comment_url);
				int[] rscount = dao.batchSave(comments);
				if (rscount.length > 0) {
					// 有评论进去了
					bookjd.setHascomment(1);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// TODO

		bookjd.setAuthor(author);
		bookjd.setAuthorIntro(authorIntro);
		bookjd.setBookName(bookName);
		bookjd.setCover_pic(cover_pic);
		bookjd.setDirectory(directory);
		bookjd.setIsbn(isbn);
		bookjd.setPress(press);
		bookjd.setPrice(pric);
		bookjd.setTranslator(translator);
		bookjd.setVersion(version);
		bookjd.setPub_price(pub_pric);
		bookjd.setOutLine(bookdis);
		bookjd.setCatelog(CLASSFY);
		// System.out.println(bookjd.toString());

		return bookjd;
	}

	@Override
	protected Map<String, String> getElementsInfo(String html, boolean isFile)
			throws Exception {
		Document doc;
		Map<String, String> map = new HashedMap();
		if (isFile) {
			File input = new File(html);
			doc = Jsoup.parse(input, "UTF-8");
		} else {
			doc = Jsoup.parse(html);
		}

		map.put(PropertyUtil.BOOKNAME,
				doc.select(PropertyUtil.readProperty(PropertyUtil.BOOKNAME))
						.first() == null ? "" : doc
						.select(PropertyUtil
								.readProperty(PropertyUtil.BOOKNAME)).first()
						.text());
		map.put(PropertyUtil.PRICE,
				doc.select(PropertyUtil.readProperty(PropertyUtil.PRICE))
						.first() == null ? "" : doc
						.select(PropertyUtil.readProperty(PropertyUtil.PRICE))
						.first().text());
		map.put(PropertyUtil.PUBLISHED_PRICE,
				doc.select(
						PropertyUtil.readProperty(PropertyUtil.PUBLISHED_PRICE))
						.first() == null ? "" : doc
						.select(PropertyUtil
								.readProperty(PropertyUtil.PUBLISHED_PRICE))
						.first().text());

		ArrayList<String> item_idlist = new ArrayList<String>();
		try{
			Elements eles = doc.select(PropertyUtil

				.readProperty(PropertyUtil.ITEM_ID));
		for(Element ele:eles){
			item_idlist.add(ele.text());
		}
		while(!item_idlist.isEmpty()){
			String item_id = item_idlist.remove(0);
			if(item_id.matches("商品编码：[0-9]*"))
			{map.put(PropertyUtil.ITEM_ID,item_id);
			break;
			}
		}
		}catch(Exception e){
		//donothing	
		}
		map.put(PropertyUtil.COVER_PIC,
				doc.select(PropertyUtil.readProperty(PropertyUtil.COVER_PIC))
						.first() == null ? "" : doc
						.select(PropertyUtil
								.readProperty(PropertyUtil.COVER_PIC)).first()
						.attr("src"));

		if (doc.select(PropertyUtil.readProperty(PropertyUtil.ISBN)).first() != null) {
			map.put(PropertyUtil.ISBN,
					doc.select(PropertyUtil.readProperty(PropertyUtil.ISBN))
							.first().text());
			if (doc.select(PropertyUtil.readProperty(PropertyUtil.AUTHOR))
					.first() != null) {
				String author_trans = doc
						.select(PropertyUtil.readProperty(PropertyUtil.AUTHOR))
						.first().text();
				String[] param = Patternmatch_JD
						.patternmatchAUT_TRANS(author_trans);

				map.put(PropertyUtil.AUTHOR, param[0]);
				map.put(PropertyUtil.TRANSLATOR, param[1]);
			}
			// map.put(PropertyUtil.BOOK_DESCIPTION,
			// doc.select(PropertyUtil.readProperty(PropertyUtil.BOOK_DESCIPTION)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.BOOK_DESCIPTION)).first().text());
			map.put(PropertyUtil.PRESS,
					doc.select(PropertyUtil.readProperty(PropertyUtil.PRESS))
							.first() == null ? "" : doc
							.select(PropertyUtil
									.readProperty(PropertyUtil.PRESS)).first()
							.text());
			// map.put(PropertyUtil.VERSION,
			// doc.select(PropertyUtil.readProperty(PropertyUtil.VERSION)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.VERSION)).first().text());
			// TODO map.put(PropertyUtil.ITEM_ID,
			// doc.select(PropertyUtil.readProperty(PropertyUtil.ITEM_ID)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.ITEM_ID)).first().text());

			// 暂时先抽取一个分类
			map.put(PropertyUtil.CLASSFY,
					doc.select(PropertyUtil.readProperty(PropertyUtil.CLASSFY))
							.first() == null ? "" : doc
							.select(PropertyUtil
									.readProperty(PropertyUtil.CLASSFY))
							.first().text());
			String[] param = getProductdescriptionAll(
					doc,
					PropertyUtil
							.readProperty(PropertyUtil.PRODUCTDESCRIPTION_KEY),
					PropertyUtil
							.readProperty(PropertyUtil.PRODUCTDESCRIPTION_VALUE));
			map.put(PropertyUtil.BOOK_DESCIPTION, param[0]);
			map.put(PropertyUtil.AUTHOR_INTRO, param[1]);
			map.put(PropertyUtil.DIRECTORY, param[2]);
		} else {

			String intro_clearfix = doc.select(
					PropertyUtil.readProperty(PropertyUtil.intro_clearfix))
					.first() == null ? "" : doc
					.select(PropertyUtil
							.readProperty(PropertyUtil.intro_clearfix)).first()
					.text();
			// System.out.println(intro_clearfix);
			String[] param = Patternmatch_JD
					.patternmatchContent(intro_clearfix);
			map.put(PropertyUtil.PRESS, param[0]);
			map.put(PropertyUtil.AUTHOR, param[1]);
			map.put(PropertyUtil.ISBN, param[2]);
			map.put(PropertyUtil.VERSION, param[3]);
			map.put(PropertyUtil.CLASSFY,
					doc.select(PropertyUtil.readProperty(PropertyUtil.CLASSFY2))
							.first() == null ? "" : doc
							.select(PropertyUtil
									.readProperty(PropertyUtil.CLASSFY2))
							.first().text());
			String[] paramcon = getProductdescriptionAll2(
					doc,
					PropertyUtil
							.readProperty(PropertyUtil.PRODUCTDESCRIPTION_KEY2),
					PropertyUtil
							.readProperty(PropertyUtil.PRODUCTDESCRIPTION_VALUE2));
			map.put(PropertyUtil.BOOK_DESCIPTION, paramcon[0]);
			map.put(PropertyUtil.AUTHOR_INTRO, paramcon[1]);
			map.put(PropertyUtil.DIRECTORY, paramcon[2]);
		}

		return map;
	}

	protected String[] getProductdescriptionAll(Document doc,
			String productdescription_key, String productdescription_value) {
		String[] param = new String[3];
		ArrayList<String> prodes_key = new ArrayList<String>();
		ArrayList<String> prodes_value = new ArrayList<String>();
		try {
			Elements linksElementkey = doc.select(productdescription_key);
			for (Element ele : linksElementkey) {
				prodes_key.add(ele.text());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Elements linksElementvalue = doc.select(productdescription_value);
			for (Element ele : linksElementvalue) {
				prodes_value.add(ele.html());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			int index = prodes_key.indexOf("内容简介");
			param[0] = prodes_value.get(index);
		} catch (IndexOutOfBoundsException e) {
			param[0] = "";
		}
		try {
			int index = prodes_key.indexOf("作者简介");
			param[1] = prodes_value.get(index);
		} catch (IndexOutOfBoundsException e) {
			param[1] = "";
		}
		try {
			int index = prodes_key.indexOf("目录");
			param[2] = prodes_value.get(index);
		} catch (IndexOutOfBoundsException e) {
			param[2] = "";
		}
		return param;
	}

	protected String[] getProductdescriptionAll2(Document doc,
			String productdescription_key, String productdescription_value) {
		String[] param = new String[3];
		ArrayList<String> prodes_key = new ArrayList<String>();
		ArrayList<String> prodes_value = new ArrayList<String>();
		try {
			Elements linksElementkey = doc.select(productdescription_key);
			for (Element ele : linksElementkey) {
				prodes_key.add(ele.text());
			}
			prodes_key.remove(0);
		} catch (Exception e) {

		}

		try {
			Elements linksElementvalue = doc.select(productdescription_value);
			for (Element ele : linksElementvalue) {
				prodes_value.add(ele.html());
				// System.out.println(ele.html());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			int index = prodes_key.indexOf("内容提要");
			param[0] = prodes_value.get(index);
		} catch (IndexOutOfBoundsException e) {
			param[0] = "";
		}
		try {
			int index = prodes_key.indexOf("作者简介");
			param[1] = prodes_value.get(index);
		} catch (IndexOutOfBoundsException e) {
			param[1] = "";
		}
		try {
			int index = prodes_key.indexOf("目录");
			param[2] = prodes_value.get(index);
		} catch (IndexOutOfBoundsException e) {
			param[2] = "";
		}
		return param;
	}
}