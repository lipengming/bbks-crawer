package bimoku.extract.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.map.HashedMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



import bimoku.extract.common.PropertyUtil;

import com.bimoku.common.bean.BookDD;
import com.bimoku.common.bean.BookDetail;
import com.bimoku.common.bean.BookPub;
import com.bimoku.integrate.DDIntegrated;
import com.bimoku.integrate.Integrated;
import com.bimoku.integrate.PubIntegrated;

@Component("parserChinapub")
public class ParserChinapub extends Parser{
	
	@Autowired
	PubIntegrated pubIntegrated;
	
	@Override
	protected Integrated getIntegratedDao() {
		if(pubIntegrated == null)
			throw new RuntimeException("spring bean 实例化出错");
		return pubIntegrated;
	}

	@Override
	protected BookDetail fieldFilter(Map<String, String> map) {
		BookPub bookPub = new BookPub();
		
		//String author = map.get(PropertyUtil.AUTHOR);
		String authorIntro = map.get(PropertyUtil.AUTHOR_INTRO);
		String bookName = map.get(PropertyUtil.BOOKNAME);
		String catelog = map.get(PropertyUtil.CLASSFY);
		String cover_pic = map.get(PropertyUtil.COVER_PIC);
		String directory = map.get(PropertyUtil.DIRECTORY);
		String bookdes = map.get(PropertyUtil.BOOK_DESCIPTION);
		//String press = map.get(PropertyUtil.PRESS);
		String price = map.get(PropertyUtil.PRICE);
		String publicprice = map.get(PropertyUtil.PUBLISHED_PRICE);
		//String translator = map.get(PropertyUtil.TRANSLATOR);
		//String version = map.get(PropertyUtil.VERSION);
		//String extract =  map.get(PropertyUtil.EXTRACT);
		String intro_clearfix = map.get(PropertyUtil.intro_clearfix);
		//System.out.println(intro_clearfix);
		//TODO 这个过程多处理
		String author = new String();
		String isbn = null;
		String press = null;
		String translator = null;
		String version = null;
		Double pric = 0.0;
		Double pubpric = 0.0;
		String[] infoparam = Patternmatch_Pub.patternmatchContent(intro_clearfix);
		
		//基本信息模块用模式匹配
		author = infoparam[0];
		//translator = infoparam[1];
		press = infoparam[1];
		version = infoparam[3];
		
		
		try{
	    isbn = infoparam[2].trim();
		}catch(Exception e){
			isbn = "";
		}
	    try {
	    	
	    	price = price.replaceAll("￥", "").replaceAll("\\([0-9]*折\\)", "").replaceAll("普通会员价", "");
	    	
	    	pric = Double.parseDouble(price.trim());
	    	
	    }catch(Exception e){
	    	pric = 0.0;
	    }
	    try {
	    	
	    	publicprice = publicprice.replaceAll("￥", "").replaceAll("\\([0-9]*折\\)", "");
	    	
	    	pubpric = Double.parseDouble(publicprice.trim());
	    	
	    }catch(Exception e){
	    	pubpric = 0.0;
	    }
	    //控制字段长度
	
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
		
		try{
			author = author.substring(0, author.length()>45?44:author.length());
			}catch(Exception e){
				author = "";
			}
		
		
		
		bookPub.setAuthor(author);
		bookPub.setAuthorIntro(authorIntro);
		bookPub.setBookName(bookName);
		bookPub.setCatelog(catelog);
		bookPub.setCover_pic(cover_pic);
		bookPub.setDirectory(directory);
		bookPub.setIsbn(isbn);
		bookPub.setPress(press);
		bookPub.setPrice(pric);
		bookPub.setPub_price(pubpric);
		bookPub.setTranslator(translator);
		bookPub.setVersion(version);
		bookPub.setOutLine(bookdes);
		
		
		
		return bookPub;
	}

	@Override
	protected Map<String, String> getElementsInfo(String html,boolean isFile) throws Exception{
		Document doc ;
		Map<String, String> map = new HashedMap();
		if(isFile){
			File input = new File(html);
			 doc = Jsoup.parse(input, "UTF-8");
		}else{
		 doc = Jsoup.parse(html);
		}
		
		//System.out.println(doc.select(PropertyUtil.readProperty(PropertyUtil.BOOKNAME)).first().text());
         if(doc.select(PropertyUtil.readProperty(PropertyUtil.BOOKNAME)).first()!=null)
		{
        	 map.put(PropertyUtil.BOOKNAME,doc.select(PropertyUtil.readProperty(PropertyUtil.BOOKNAME)).first().text());
		}else if(doc.select(PropertyUtil.readProperty(PropertyUtil.BOOKNAME2)).first()!=null){
			 map.put(PropertyUtil.BOOKNAME,doc.select(PropertyUtil.readProperty(PropertyUtil.BOOKNAME2)).first().text());
		}else{
			 map.put(PropertyUtil.BOOKNAME,doc.select(PropertyUtil.readProperty(PropertyUtil.BOOKNAME3)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.BOOKNAME3)).first().text());
		}
		//map.put(PropertyUtil.AUTHOR, doc.select(PropertyUtil.readProperty(PropertyUtil.AUTHOR)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.AUTHOR)).first().text());
		//map.put(PropertyUtil.TRANSLATOR, doc.select(PropertyUtil.readProperty(PropertyUtil.TRANSLATOR)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.TRANSLATOR)).first().text());
		//map.put(PropertyUtil.PRESS, doc.select(PropertyUtil.readProperty(PropertyUtil.PRESS)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.PRESS)).first().text());
		//map.put(PropertyUtil.VERSION, doc.select(PropertyUtil.readProperty(PropertyUtil.VERSION)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.VERSION)).first().text());
		//map.put(PropertyUtil.ITEM_ID, doc.select(PropertyUtil.readProperty(PropertyUtil.ITEM_ID)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.ITEM_ID)).first().text());
		//map.put(PropertyUtil.ISBN, doc.select(PropertyUtil.readProperty(PropertyUtil.ISBN)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.ISBN)).first().text());
		if(doc.select(PropertyUtil.readProperty(PropertyUtil.intro_clearfix)).first()!=null)
				{
			map.put(PropertyUtil.intro_clearfix, doc.select(PropertyUtil.readProperty(PropertyUtil.intro_clearfix)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.intro_clearfix)).last().text());
				}else{
					map.put(PropertyUtil.intro_clearfix, doc.select(PropertyUtil.readProperty(PropertyUtil.intro_clearfix2)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.intro_clearfix2)).first().text());
				}
		
		if(doc.select(PropertyUtil.readProperty(PropertyUtil.PRICE)).first()!=null){
			map.put(PropertyUtil.PRICE, doc.select(PropertyUtil.readProperty(PropertyUtil.PRICE)).first().text());	
		}else if(doc.select(PropertyUtil.readProperty(PropertyUtil.PRICE2)).first()!=null){
			map.put(PropertyUtil.PRICE, doc.select(PropertyUtil.readProperty(PropertyUtil.PRICE2)).first().text());
		}else{
			map.put(PropertyUtil.PRICE, doc.select(PropertyUtil.readProperty(PropertyUtil.PRICE3)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.PRICE3)).first().text());
		}
		
		map.put(PropertyUtil.PUBLISHED_PRICE, doc.select(PropertyUtil.readProperty(PropertyUtil.PUBLISHED_PRICE)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.PUBLISHED_PRICE)).first().text());

		Elements linksElements = doc.select(PropertyUtil.readProperty(PropertyUtil.CLASSFY));
		String CLASSFY = "";
		for (Element ele : linksElements) {
			CLASSFY += ele.text() + ">";
		}
		map.put(PropertyUtil.CLASSFY, CLASSFY);
		
		map.put(PropertyUtil.COVER_PIC, doc.select(PropertyUtil.readProperty(PropertyUtil.COVER_PIC)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.COVER_PIC)).first().attr("src"));
//		map.put(PropertyUtil.EDITOR_CHOICE, doc.select(PropertyUtil.readProperty(PropertyUtil.EDITOR_CHOICE)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.EDITOR_CHOICE)).first().text());
//		map.put(PropertyUtil.CONTENT_CHOICE, doc.select(PropertyUtil.readProperty(PropertyUtil.CONTENT_CHOICE)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.CONTENT_CHOICE)).first().text());
		//map.put(PropertyUtil.AUTHOR_INTRO, doc.select(PropertyUtil.readProperty(PropertyUtil.AUTHOR_INTRO)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.AUTHOR_INTRO)).first().text());
		//map.put(PropertyUtil.DIRECTORY, doc.select(PropertyUtil.readProperty(PropertyUtil.DIRECTORY)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.DIRECTORY)).first().text());

//		map.put(PropertyUtil.MEDIA_REVIEWS, doc.select(PropertyUtil.readProperty(PropertyUtil.MEDIA_REVIEWS)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.MEDIA_REVIEWS)).first().text());
		//map.put(PropertyUtil.EXTRACT, doc.select(PropertyUtil.readProperty(PropertyUtil.EXTRACT)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.EXTRACT)).first().text());
		//map.put(PropertyUtil.ATTACH_IMAGE_SHOW, doc.select(PropertyUtil.readProperty(PropertyUtil.ATTACH_IMAGE_SHOW)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.ATTACH_IMAGE_SHOW)).first().text());
		//map.put(PropertyUtil.COMMENTURL, doc.select(PropertyUtil.readProperty(PropertyUtil.COMMENTURL)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.COMMENTURL)).first().text());
		String[] param = getProductdescriptionAll(doc,
				PropertyUtil.readProperty(PropertyUtil.PRODUCTDESCRIPTION_KEY),
				PropertyUtil
						.readProperty(PropertyUtil.PRODUCTDESCRIPTION_VALUE));
		map.put(PropertyUtil.BOOK_DESCIPTION, param[0]);
		map.put(PropertyUtil.AUTHOR_INTRO, param[1]);
		map.put(PropertyUtil.DIRECTORY, param[2]);
		
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
			//e.printStackTrace();
		}

		try {
			Elements linksElementvalue = doc.select(productdescription_value);
			for (Element ele : linksElementvalue) {
				prodes_value.add(ele.text());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			int index = prodes_key.indexOf("内容简介");
			param[0] = prodes_value.get(index).replaceAll("内容简介", "").trim();
		    param[0] = param[0].trim();
		} catch (Exception e) {
			param[0] = "";
		}
		try {
			int index = prodes_key.indexOf("作译者");
			param[1] = prodes_value.get(index).replaceAll("作译者", "").trim();
		    param[1] = param[1].trim();
		} catch (Exception e) {
			param[1] = "";
		}
		try {
			int index = prodes_key.indexOf("目录");
			param[2] = prodes_value.get(index).replaceAll("↓展开全部内容", "").replaceAll("目录", "").trim();
			param[2] = param[2].trim();
		} catch (Exception e) {
			param[2] = "";
			
		}
		return param;
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
		result = result.replace("<", "").replace(">", "");
		return result;
	}
}