package bimoku.extract.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {
	
	public final static String BOOKNAME = "BOOKNAME";
	public final static String BOOKNAME2 = "BOOKNAME2";
	public final static String BOOKNAME3 = "BOOKNAME3";
	public final static String AUTHOR="AUTHOR";
	public final static String TRANSLATOR="TRANSLATOR";
	public final static String PRESS="PRESS";
	public final static String VERSION="VERSION";
	public final static String ITEM_ID="ITEM_ID"; 
	public final static String ISBN="ISBN";
	public final static String intro_clearfix="intro_clearfix";
	public final static String intro_clearfix2="intro_clearfix2";
	public final static String PRICE = "PRICE";
	public final static String PRICE2 = "PRICE2";
	public final static String PRICE3 = "PRICE3";
	public final static String PUBLISHED_PRICE="PUBLISHED_PRICE";
	public final static String PUBLISHED_PRICE2="PUBLISHED_PRICE2";
	public final static String COVER_PIC = "COVER_PIC";
	public final static String CLASSFY="CLASSFY";
	public final static String CLASSFY2="CLASSFY2";
	public final static String EDITOR_CHOICE="EDITOR_"+"\\"+" CHOICE";
	public final static String CONTENT_CHOICE="CONTENT_CHOICE";
	public final static String AUTHOR_INTRO="AUTHOR_INTRO";
	public final static String DIRECTORY="DIRECTORY";
	public final static String MEDIA_REVIEWS = "MEDIA_"+"\\"+" REVIEWS";//媒体评论
	public final static String EXTRACT="EXTRACT"; //在线试读
	public final static String ATTACH_IMAGE_SHOW="ATTACH_IMAGE_SHOW";//书摘
	public final static String COMMENTURL="COMMENTURL";//评论链接
	public final static String BOOK_DESCIPTION="BOOK_DESCIPTION";  //Amazon图书描述
	public final static String AUTHOR_TRANSLATOR="AUTHOR_TRANSLATOR";  //Amazon作者信息
	public final static String PRODUCTDESCRIPTION_URL="PRODUCTDESCRIPTION_URL";  //Amazon商品描述页链接
	public final static String PRODUCTDESCRIPTION_KEY="PRODUCTDESCRIPTION_KEY";//Amaozn商品描述页抓取属性的名字（如 目录，图书简介）
	public final static String PRODUCTDESCRIPTION_VALUE="PRODUCTDESCRIPTION_VALUE";//Amaozn商品描述页抓取属性的内容（如 目录，图书简介）
	public final static String PRODUCTDESCRIPTION_KEY2="PRODUCTDESCRIPTION_KEY2";//Amaozn商品描述页抓取属性的名字（如 目录，图书简介）
	public final static String PRODUCTDESCRIPTION_VALUE2="PRODUCTDESCRIPTION_VALUE2";//Amaozn商品描述页抓取属性的内容（如 目录，图书简介）
	
	public static final String ISBN2 = "ISBN2";
	
	//新书控制抓取路径的属性
	public static final String starturl_newbook = "starturl_newbook";
	public static final String firstcategoryurl_newbook = "firstcategoryurl_newbook";
	public static final String bookurl_newbook = "bookurl_newbook";
	public static final String rankpage_newbook = "rankpage_newbook";
	public static final String secondcategoryurl_newbook = "secondcategoryurl_newbook";
	public static final String defaultStr_newbook = "defaultStr_newbook";
	//特价控制抓取路径属性
	public static final String starturl_promotion = "starturl_promotion";
	public static final String firstcategoryurl_promotion = "firstcategoryurl_promotion";
	public static final String bookurl_promotion = "bookurl_promotion";
	public static final String rankpage_promotion = "rankpage_promotion";
	public static final String secondcategoryurl_promotion = "secondcategoryurl_promotion";
	public static final String defaultStr_promotion = "defaultStr_promotion";
	//销售排行榜控制抓取路径属性
	public static final String starturl_onsale = "starturl_onsale";
	public static final String firstcategoryurl_onsale = "firstcategoryurl_onsale";
	public static final String bookurl_onsale = "bookurl_onsale";
	public static final String rankpage_onsale = "rankpage_onsale";
	public static final String secondcategoryurl_onsale = "secondcategoryurl_onsale";
	public static final String defaultStr_onsale = "defaultStr_onsale";
	public static final String defaultStr2_onsale = "defaultStr2_onsale";
	public static final String thirdcategoryurl_onsale = "thirdcategoryurl_onsale";
	
	//书评的相关属性
	public static final String com_item = "com_item";
	public static final String com_user = "com_user";
	public static final String com_avatar = "com_avatar";
	public static final String com_content = "com_content";
	public static final String create_at = "create_at";
	public static final String com_nextPage = "com_nextPage";
	
	
	//property
	private static Properties p;
	// 使用类路径来连接配置文件
	private static PropertyUtil install;
	//配置文件路径
	private static String defaultLoacal = "jdConfig.properties";
	private static String configPath = "";
	
	static {
		InputStream i = getPropertyUtill().getClass().getResourceAsStream(defaultLoacal);
		try {
			getProperty().load(i);
		} catch (IOException e) {
			System.out.println("配置文件路径不正确" + e.getMessage());
		}
	}
	
	public static Properties getProperty(){
		if(p == null){
			p = new Properties();
		}
		return p;
	}
	
	public static Properties getProperty(String configPath){
		//如果不是当前路径。则强制修改配置文件路径
		if(!PropertyUtil.configPath.equals(configPath)){
			PropertyUtil.configPath = configPath;
			p = null;
		}
		
		if(p == null){
			p = getProperty();
			InputStream i = getPropertyUtill().getClass().getResourceAsStream(configPath);
			try {
				p.load(i);
			} catch (IOException e) {
				System.out.println("配置文件路径不正确" + e.getMessage());
			}
		}
		return p;
	}
	
	public static PropertyUtil getPropertyUtill(){
		if(install == null){
			install = new PropertyUtil();
		}
		return install;
	}
	
	/**
	 * 读取键值数据
	 * @param key
	 * @return
	 */
	public static String readProperty(String key){
		return getProperty().getProperty(key);
	}
	/**
	 * 读取键值数据
	 * @param key
	 * @return
	 */
	public static Object readProperty(Object key){
		return getProperty().get(key);
	}
}
