package bimoku.extract.parser;

import java.io.File;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import com.bimoku.common.bean.BookDD;
import com.bimoku.common.bean.BookDetail;
import com.bimoku.common.bean.Comment;
import com.bimoku.integrate.DDIntegrated;
import com.bimoku.integrate.Integrated;
import com.bimoku.repository.dao.CommentDao;

@Component("parserDD")
public class ParserDD extends Parser{
	static CommentDao dao;
	static{
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/beans.xml");
		dao = ctx.getBean("commentDao",CommentDao.class);
	}
	@Autowired
	DDIntegrated ddIntegrated;
	
	@Override
	protected Integrated getIntegratedDao() {
		if(ddIntegrated == null)
			throw new RuntimeException("spring bean 实例化出错");
		return ddIntegrated;
	}

	@Override
	protected BookDetail fieldFilter(Map<String, String> map)  {
		BookDD bookDD = new BookDD();
		
		//String author = map.get(PropertyUtil.AUTHOR);
		String authorIntro = map.get(PropertyUtil.AUTHOR_INTRO);
		String bookName = map.get(PropertyUtil.BOOKNAME);
		String catelog = map.get(PropertyUtil.CLASSFY);
		String cover_pic = map.get(PropertyUtil.COVER_PIC);
		String directory = map.get(PropertyUtil.DIRECTORY);
		String contentcho = map.get(PropertyUtil.CONTENT_CHOICE);  //图书简介
		//String press = map.get(PropertyUtil.PRESS);
		String price = map.get(PropertyUtil.PRICE);
		String publicprice = map.get(PropertyUtil.PUBLISHED_PRICE);
		//String translator = map.get(PropertyUtil.TRANSLATOR);
		//String version = map.get(PropertyUtil.VERSION);
		String intro_clearfix = map.get(PropertyUtil.intro_clearfix);
		String item_id = map.get(PropertyUtil.ITEM_ID);
		String comment_url = PropertyUtil.readProperty(PropertyUtil.COMMENTURL).trim()+item_id;
		
		
		//System.out.println(price);
		//System.out.println(publicprice);
		//TODO 这个过程多处理
		String author = new String();
		String isbn = null; 
		String press = null;  //出版社
		String translator = null; //译者
		String version = null;  //版本
		Double pric = 0.0;  //价格
		Double pubpric = 0.0; //定价
		String[] infoparam = PatternmatchDD.patternmatch(intro_clearfix);
		
		//基本信息模块用模式匹配
		//控制字段的长度
		author = infoparam[0]==null?"": infoparam[0].substring(0, infoparam[0].length()>45?45:infoparam[0].length());;
		translator = infoparam[1]==null?"": infoparam[1].substring(0, infoparam[1].length()>45?45:infoparam[1].length());;
		press = infoparam[2]==null?"": infoparam[2].substring(0, infoparam[2].length()>45?45:infoparam[2].length());;;
		version = infoparam[3]==null?"": infoparam[3].substring(0, infoparam[3].length()>20?20:infoparam[3].length());;;

		
		
		try{
	    isbn = infoparam[4].trim();
		}catch(NullPointerException e){
			isbn = "";
		}
		
		try{
	    	pric = Double.valueOf(price.replaceAll("¥", "").trim());
	    }catch(Exception e){
	    	pric = 0.0;
	    }
		
		try{
			pubpric = Double.valueOf(publicprice.replaceAll("¥", "").trim());
	    }catch(Exception e){
	    	pubpric = 0.0;
	    }
		
	   
		try{
			cover_pic = cover_pic.substring(0, cover_pic.length()>150?150:cover_pic.length());
			}catch(Exception e){
				cover_pic = "";
			}
		try{
			bookName = bookName.substring(0, bookName.length()>45?44:bookName.length());
			}catch(Exception e){
				bookName = "";
			}
		try{
			catelog = catelog.substring(0, catelog.length()>50?49:catelog.length());
			}catch(Exception e){
				catelog = "";
			}
		Long t1 = System.currentTimeMillis();
		System.out.println(isbn+"+++"+item_id);
		System.out.println(comment_url);
		if(!"".equals(isbn)&&isbn !=null&&!"".equals(item_id)&&item_id!= null)
		{
			try {
			List<Comment> comments = ParsercommentDD.parsercomment(isbn,comment_url);
			
			int[] rscount = dao.batchSave(comments);
			if(rscount.length > 0){
				//有评论进去了
				bookDD.setHascomment(1);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}}
		System.out.println("TIMES: "+(System.currentTimeMillis() - t1));
		
		bookDD.setAuthor(author);
		bookDD.setAuthorIntro(authorIntro);
		bookDD.setBookName(bookName);
		bookDD.setCatelog(catelog);
		bookDD.setCover_pic(cover_pic);
		bookDD.setDirectory(directory);
		bookDD.setIsbn(isbn);
		bookDD.setPress(press);
		bookDD.setPrice(pric);
		bookDD.setTranslator(translator);
		bookDD.setVersion(version);
		bookDD.setPub_price(pubpric);
		bookDD.setOutLine(contentcho);
		//System.out.println(bookDD.toString());
		
		return bookDD;
	}

	@Override
	protected Map<String, String> getElementsInfo(String html,boolean isFile) throws Exception{
		Document doc;
		Map<String, String> map = new HashedMap();
		//File input = new File(filepath);
		if(isFile){
			File input = new File(html);
			 doc = Jsoup.parse(input, "UTF-8");
		}else{
			doc = Jsoup.parse(html);
		}
		
		//System.out.println(doc.select(PropertyUtil.readProperty(PropertyUtil.BOOKNAME)).first().text());
         
		map.put(PropertyUtil.BOOKNAME, doc.select(PropertyUtil.readProperty(PropertyUtil.BOOKNAME)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.BOOKNAME)).first().text());
		//map.put(PropertyUtil.AUTHOR, doc.select(PropertyUtil.readProperty(PropertyUtil.AUTHOR)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.AUTHOR)).first().text());
		//map.put(PropertyUtil.TRANSLATOR, doc.select(PropertyUtil.readProperty(PropertyUtil.TRANSLATOR)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.TRANSLATOR)).first().text());
		//map.put(PropertyUtil.PRESS, doc.select(PropertyUtil.readProperty(PropertyUtil.PRESS)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.PRESS)).first().text());
		//map.put(PropertyUtil.VERSION, doc.select(PropertyUtil.readProperty(PropertyUtil.VERSION)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.VERSION)).first().text());
		map.put(PropertyUtil.ITEM_ID, doc.select(PropertyUtil.readProperty(PropertyUtil.ITEM_ID)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.ITEM_ID)).first().text());
		//map.put(PropertyUtil.ISBN, doc.select(PropertyUtil.readProperty(PropertyUtil.ISBN)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.ISBN)).first().text());
		map.put(PropertyUtil.intro_clearfix, doc.select(PropertyUtil.readProperty(PropertyUtil.intro_clearfix)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.intro_clearfix)).first().text());
		map.put(PropertyUtil.PRICE, doc.select(PropertyUtil.readProperty(PropertyUtil.PRICE)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.PRICE)).first().text());
		map.put(PropertyUtil.PUBLISHED_PRICE, doc.select(PropertyUtil.readProperty(PropertyUtil.PUBLISHED_PRICE)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.PUBLISHED_PRICE)).first().text());

		Elements linksElements = doc.select(PropertyUtil.readProperty(PropertyUtil.CLASSFY));
		String CLASSFY = "";
		for (Element ele : linksElements) {
			CLASSFY += ele.text() + ">";
		}
		map.put(PropertyUtil.CLASSFY, CLASSFY);
		
		map.put(PropertyUtil.COVER_PIC, doc.select(PropertyUtil.readProperty(PropertyUtil.COVER_PIC)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.COVER_PIC)).first().attr("wsrc"));
//		map.put(PropertyUtil.EDITOR_CHOICE, doc.select(PropertyUtil.readProperty(PropertyUtil.EDITOR_CHOICE)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.EDITOR_CHOICE)).first().text());
		map.put(PropertyUtil.CONTENT_CHOICE, doc.select(PropertyUtil.readProperty(PropertyUtil.CONTENT_CHOICE)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.CONTENT_CHOICE)).first().text());
		map.put(PropertyUtil.AUTHOR_INTRO, doc.select(PropertyUtil.readProperty(PropertyUtil.AUTHOR_INTRO)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.AUTHOR_INTRO)).first().text());
		map.put(PropertyUtil.DIRECTORY, doc.select(PropertyUtil.readProperty(PropertyUtil.DIRECTORY)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.DIRECTORY)).first().text());

//		map.put(PropertyUtil.MEDIA_REVIEWS, doc.select(PropertyUtil.readProperty(PropertyUtil.MEDIA_REVIEWS)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.MEDIA_REVIEWS)).first().text());
		//map.put(PropertyUtil.EXTRACT, doc.select(PropertyUtil.readProperty(PropertyUtil.EXTRACT)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.EXTRACT)).first().text());
		//map.put(PropertyUtil.ATTACH_IMAGE_SHOW, doc.select(PropertyUtil.readProperty(PropertyUtil.ATTACH_IMAGE_SHOW)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.ATTACH_IMAGE_SHOW)).first().text());
		//map.put(PropertyUtil.COMMENTURL, doc.select(PropertyUtil.readProperty(PropertyUtil.COMMENTURL)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.COMMENTURL)).first().text());

		
		return map;
	}
	
	//处理掉大字段中的标签
	public static String trimTag(String content) {
		String regEx = "<[^>]+>";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(content);
		String result = content;
		if (m.find()) {
			result = m.replaceAll("");
		}
		result = result.replace("<", "").replace(">", "").replaceAll("/p", "");
		return result;
	}
}