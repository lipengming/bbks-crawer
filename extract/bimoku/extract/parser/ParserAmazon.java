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
import bimoku.extract.parsercomment.ParsercommentAmazon;
import bimoku.extract.parsercomment.ParsercommentDD;

import com.bimoku.common.bean.BookAmazon;
import com.bimoku.common.bean.BookDetail;
import com.bimoku.common.bean.Comment;
import com.bimoku.integrate.AmazonIntegrated;
import com.bimoku.integrate.Integrated;
import com.bimoku.repository.dao.CommentDao;

@Component("parserAmazon")

public class ParserAmazon extends Parser{
	static CommentDao dao;
	static{
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/beans.xml");
		dao = ctx.getBean("commentDao",CommentDao.class);
	}
	@Autowired
	AmazonIntegrated amazonIntegrated;
	
	@Override
	protected Integrated getIntegratedDao() {
		if(amazonIntegrated == null)
			throw new RuntimeException("spring bean 实例化出错");
		return amazonIntegrated;
	}
	
	@Override
	protected BookDetail fieldFilter(Map<String, String> map) {
		BookAmazon bookamazon = new BookAmazon();
		
		String author_trans = map.get(PropertyUtil.AUTHOR_TRANSLATOR);
		//System.out.println(author_trans);
		String authorIntro = map.get(PropertyUtil.AUTHOR_INTRO);
		String bookName = map.get(PropertyUtil.BOOKNAME);
		String cover_pic = map.get(PropertyUtil.COVER_PIC);
		String directory = map.get(PropertyUtil.DIRECTORY);
		//String isbn = map.get(PropertyUtil.ISBN);
		//String press = map.get(PropertyUtil.PRESS);
		String price = map.get(PropertyUtil.PRICE);
		String PUBLISHED_PRICE = map.get(PropertyUtil.PUBLISHED_PRICE);
		//String translator = map.get(PropertyUtil.TRANSLATOR);
		//String version = map.get(PropertyUtil.VERSION);
		String classfy = map.get(PropertyUtil.CLASSFY);
		String intro_clearfix = map.get(PropertyUtil.intro_clearfix);
		String book_desciption = map.get(PropertyUtil.BOOK_DESCIPTION);
		String commenturl = map.get(PropertyUtil.COMMENTURL);
		//System.out.println(commenturl);
		//String productdescription_url = map.get(PropertyUtil.PRODUCTDESCRIPTION_URL);
		//TODO 这个过程多处理
		//TODO 从commenturl抽取评论
		
		String author = "";
		String isbn = "";
		String press = "";
		String translator= "";
		String version = "";
		Double pric = 0.0;
		Double pub_pric = 0.0;
		
		String[] infoparam = Patternmatch_Amazon.patternmatchContent(intro_clearfix);
	   
	    try{
	    	press = infoparam[0];
			   
	    	press = press.substring(0, press.length()>45?44:press.length());
		 }catch(Exception e){
			 press = "";
			}
	 try{
		   version = infoparam[1];
		   
		   version = version.substring(0, version.length()>20?19:version.length());
	 }catch(Exception e){
		 version = "";
		}
	    try{
		    isbn = infoparam[3].trim();
			}catch(Exception e){
				isbn = "";
			}
	    try{
	    	pric = Double.valueOf(price.replaceAll("\\?|￥ ", ""));
	    }catch(Exception e){
	    	pric = 0.0;
	    }
	    try{
	    	pub_pric = Double.valueOf(PUBLISHED_PRICE.replaceAll("\\?|￥ ", ""));
	    }catch(Exception e){
	    	pub_pric = 0.0;
	    }
	    try{
	    	//System.out.println(cover_pic.length());
	    	//cover_pic = cover_pic.replaceAll("http://ec4.images-amazon.com/images/", "").trim();	
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
			classfy = classfy.substring(0, classfy.length()>50?49:classfy.length());
			
			}catch(Exception e){
				classfy = "";
			}

		//TODO
	
		String[] infoparam_author_trans = Patternmatch_Amazon.patternmatchAUT_TRANS(author_trans);
		author = infoparam_author_trans[0]==null?"": infoparam_author_trans[0].substring(0, infoparam_author_trans[0].length()>45?44:infoparam_author_trans[0].length());;
		translator= infoparam_author_trans[1]==null?"": infoparam_author_trans[1].substring(0, infoparam_author_trans[1].length()>45?44:infoparam_author_trans[1].length());;
		
		
		
		Long t1 = System.currentTimeMillis();
		System.out.println(isbn+"+++");
		if(!"".equals(isbn)&&isbn !=null)
		{
			try {
			List<Comment> comments = ParsercommentAmazon.parsercomment(isbn,commenturl);
			int[] rscount = dao.batchSave(comments);
			if(rscount.length > 0){
				//有评论进去了
				bookamazon.setHascomment(1);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
		System.out.println("TIMES: "+(System.currentTimeMillis() - t1));
		
		
		
		bookamazon.setAuthor(author);
		
		bookamazon.setBookName(bookName);
		bookamazon.setCover_pic(cover_pic);
		
		bookamazon.setIsbn(isbn);
		bookamazon.setPress(press);
		bookamazon.setPrice(pric);
		bookamazon.setTranslator(translator);
		bookamazon.setVersion(version);
		bookamazon.setPub_price(pub_pric);
		bookamazon.setOutLine(book_desciption);
		bookamazon.setCatelog(classfy);
		bookamazon.setAuthorIntro(authorIntro);
		bookamazon.setDirectory(directory);
		//System.out.println(bookamazon.toString());
		
		return bookamazon;
	}

	@Override
	protected Map<String, String>   getElementsInfo(String html,boolean isFile) throws Exception{
		Document doc ;
		Map<String, String> map = new HashedMap();
		if(isFile){
			File input = new File(html);
			 doc = Jsoup.parse(input, "UTF-8");
		}else{
			doc = Jsoup.parse(html);
		}
	    
			map.put(PropertyUtil.BOOKNAME, doc.select(PropertyUtil.readProperty(PropertyUtil.BOOKNAME)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.BOOKNAME)).first().text());
			map.put(PropertyUtil.AUTHOR_TRANSLATOR, doc.select(PropertyUtil.readProperty(PropertyUtil.AUTHOR_TRANSLATOR)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.AUTHOR_TRANSLATOR)).first().text());
			map.put(PropertyUtil.BOOK_DESCIPTION, doc.select(PropertyUtil.readProperty(PropertyUtil.BOOK_DESCIPTION)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.BOOK_DESCIPTION)).first().html());
			//map.put(PropertyUtil.PRESS, doc.select(PropertyUtil.readProperty(PropertyUtil.PRESS)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.PRESS)).first().text());
			//map.put(PropertyUtil.VERSION, doc.select(PropertyUtil.readProperty(PropertyUtil.VERSION)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.VERSION)).first().text());
		//TODO	map.put(PropertyUtil.ITEM_ID, doc.select(PropertyUtil.readProperty(PropertyUtil.ITEM_ID)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.ITEM_ID)).first().text());
			//map.put(PropertyUtil.ISBN, doc.select(PropertyUtil.readProperty(PropertyUtil.ISBN)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.ISBN)).first().text());
			map.put(PropertyUtil.intro_clearfix, doc.select(PropertyUtil.readProperty(PropertyUtil.intro_clearfix)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.intro_clearfix)).first().text());
			map.put(PropertyUtil.PRICE, doc.select(PropertyUtil.readProperty(PropertyUtil.PRICE)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.PRICE)).first().text());
			map.put(PropertyUtil.PUBLISHED_PRICE, doc.select(PropertyUtil.readProperty(PropertyUtil.PUBLISHED_PRICE)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.PUBLISHED_PRICE)).first().text());

//			Elements linksElements = doc.select(PropertyUtil.readProperty(PropertyUtil.CLASSFY));
//			String CLASSFY = "";
//			for (Element ele : linksElements) {
//				CLASSFY += ele.text() + ">";
//			}
			map.put(PropertyUtil.CLASSFY, doc.select(PropertyUtil.readProperty(PropertyUtil.CLASSFY))==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.CLASSFY)).text());
			
     		map.put(PropertyUtil.COVER_PIC, doc.select(PropertyUtil.readProperty(PropertyUtil.COVER_PIC)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.COVER_PIC)).first().attr("src"));
//			map.put(PropertyUtil.EDITOR_CHOICE, doc.select(PropertyUtil.readProperty(PropertyUtil.EDITOR_CHOICE)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.EDITOR_CHOICE)).first().text());
//			map.put(PropertyUtil.CONTENT_CHOICE, doc.select(PropertyUtil.readProperty(PropertyUtil.CONTENT_CHOICE)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.CONTENT_CHOICE)).first().text());
//			map.put(PropertyUtil.AUTHOR_INTRO, doc.select(PropertyUtil.readProperty(PropertyUtil.AUTHOR_INTRO)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.AUTHOR_INTRO)).first().text());
//			map.put(PropertyUtil.DIRECTORY, doc.select(PropertyUtil.readProperty(PropertyUtil.DIRECTORY)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.DIRECTORY)).first().text());

//			map.put(PropertyUtil.MEDIA_REVIEWS, doc.select(PropertyUtil.readProperty(PropertyUtil.MEDIA_REVIEWS)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.MEDIA_REVIEWS)).first().text());
			//map.put(PropertyUtil.EXTRACT, doc.select(PropertyUtil.readProperty(PropertyUtil.EXTRACT)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.EXTRACT)).first().text());
			//map.put(PropertyUtil.ATTACH_IMAGE_SHOW, doc.select(PropertyUtil.readProperty(PropertyUtil.ATTACH_IMAGE_SHOW)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.ATTACH_IMAGE_SHOW)).first().text());
			map.put(PropertyUtil.COMMENTURL, doc.select(PropertyUtil.readProperty(PropertyUtil.COMMENTURL)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.COMMENTURL)).first().attr("href"));
			//map.put(PropertyUtil.PRODUCTDESCRIPTION_URL, doc.select(PropertyUtil.readProperty(PropertyUtil.PRODUCTDESCRIPTION_URL)).first()==null?"":doc.select(PropertyUtil.readProperty(PropertyUtil.PRODUCTDESCRIPTION_URL)).first().attr("href"));


			//抽取商品描述部分 如果存在商品描述页，作者简介和目录就从商品描述页抽取，如果不存在直接在商品介绍页抽取
			if(doc.select(PropertyUtil.readProperty(PropertyUtil.PRODUCTDESCRIPTION_URL)).first()!=null)
			{
				String productdescription_url = doc.select(PropertyUtil.readProperty(PropertyUtil.PRODUCTDESCRIPTION_URL)).first().attr("href");
				String[] param = getProductdescriptionAll(productdescription_url,PropertyUtil.readProperty(PropertyUtil.PRODUCTDESCRIPTION_KEY),PropertyUtil.readProperty(PropertyUtil.PRODUCTDESCRIPTION_VALUE));
				map.put(PropertyUtil.AUTHOR_INTRO, param[0]);
				map.put(PropertyUtil.DIRECTORY, param[1]);
				
			}else{
				String[] param = getProductdescriptionAll(doc,PropertyUtil.readProperty(PropertyUtil.PRODUCTDESCRIPTION_KEY),PropertyUtil.readProperty(PropertyUtil.PRODUCTDESCRIPTION_VALUE));
				map.put(PropertyUtil.AUTHOR_INTRO, param[0]);
				map.put(PropertyUtil.DIRECTORY, param[1]);
			}
			
		return map;
	}
	
	protected String[] getProductdescriptionAll(String productdescription_url,String productdescription_key,String productdescription_value){
		String[] param = new String[2];
		HttpConnectionManager httpConnectionManager = new HttpConnectionManager();
		String html = httpConnectionManager.getHtml(productdescription_url);
		Document doc = Jsoup.parse(html);
		ArrayList<String> prodes_key =new ArrayList<String>();
		ArrayList<String> prodes_value = new ArrayList<String>();
		Elements linksElementkey = doc.select(productdescription_key);
		 for (Element ele : linksElementkey) {
			 //System.out.println(ele.text());
			 prodes_key.add(ele.text());
		 }
		 Elements linksElementvalue = doc.select(productdescription_value);
		 for (Element ele : linksElementvalue) {
			 prodes_value.add(ele.html());
			// System.out.println(ele.text());
		 }
		try{
			int index = prodes_key.indexOf("作者简介");
			 param[0]= prodes_value.get(index);
			// System.out.println("++++++++++"+param[0]);
		}catch(IndexOutOfBoundsException e){
			param[0] = "";
		}
		try{
			int index = prodes_key.indexOf("目录");
			 param[1]= prodes_value.get(index);
		}catch(IndexOutOfBoundsException e){
			param[1] = "";
		}
		return param; 
	}
	protected String[] getProductdescriptionAll(Document doc,String productdescription_key,String productdescription_value){
		String[] param = new String[2];	
		ArrayList<String> prodes_key =new ArrayList<String>();
		ArrayList<String> prodes_value = new ArrayList<String>();
	try{	Elements linksElementkey = doc.select(productdescription_key);
		 for (Element ele : linksElementkey) {
			 prodes_key.add(ele.text());
		 }
	}catch(Exception e){
		e.printStackTrace();
	}
		
	try {Elements linksElementvalue = doc.select(productdescription_value);
		 for (Element ele : linksElementvalue) {
			 prodes_value.add(ele.html());
		 }
	}catch(Exception e)	 {
		e.printStackTrace();
	}
		 
		 
		try{
			int index = prodes_key.indexOf("作者简介");
			 param[0]= prodes_value.get(index);
		}catch(IndexOutOfBoundsException e){
			param[0] = "";
		}
		try{
			int index = prodes_key.indexOf("目录");
			 param[1]= prodes_value.get(index);
		}catch(IndexOutOfBoundsException e){
			param[1] = "";
		}
		return param; 
	}
}