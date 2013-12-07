package bimoku.extract.parser;


import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;


import bimoku.extract.common.exception.ExtractException;

import com.bimoku.common.bean.BookDetail;
import com.bimoku.common.bean.RealTimeType;
import com.bimoku.integrate.Integrated;

/**
 * 抽取主程序
 * 
 * @author 梅良--
 * @author LPM
 * 
 */
public abstract class Parser {
	Logger logger = Logger.getLogger(getClass());
	/**
	 * 抽取主方法：抽取分为这样几个步骤 1、定位抽取，，，，这个部分主要就是根据抽取规则得到一个大概的字段数据 比如，我要抽取标题：Element el
	 * = doc.select(PropertyUtil.get("title"));
	 * 
	 * 2、字段过滤 比如抽取出来的title：<b>我爱你中国【特价书籍】</b> 过滤之后应该得到：我爱你中国特价书籍 或者 我爱你中国【特价书籍】
	 * 
	 * 3、数据持久化 调用集成接口
	 * 
	 */
	public void parser(String html){
		try{
			BookDetail bd = parserTo(html, true);
			getIntegratedDao().integrated(bd);
		}catch(ExtractException e){
			logger.error("抽取失败:"+html);
		}
	}
	
	public void parser(String html,String url) throws ExtractException {
		try{
			BookDetail bd = parserTo(html, false,url);
			getIntegratedDao().integrated(bd);
		}catch(ExtractException e){
			logger.error("抽取失败:"+url);
		}
	}

	public void parserForRealTime(String html,RealTimeType type,String url) throws ExtractException {
		try{
			BookDetail bd = parserTo(html, false,url);
			getIntegratedDao().realTimeIntegrated(bd,type);
		}catch(ExtractException e){
			logger.error("抽取失败:"+url);
		}
	}

	public BookDetail parserTo(String html, boolean isFile,String url) throws ExtractException{
		BookDetail bookDetail = parserTo(html,isFile);
		bookDetail.setUrl(url);
		return bookDetail;
	}

	public BookDetail parserTo(String html, boolean isFile) throws ExtractException{
		// 抽取
		Map<String, String> map = new HashedMap();
		try {
			map = getElementsInfo(html, isFile);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		// //没有抓到书名或者isbn的，都不再进行后续操作
		/*
		 * if(map.get(PropertyUtil.BOOKNAME) == null ||
		 * map.get(PropertyUtil.ISBN) == null){ throw new ExtractException(); }
		 */
		// 过滤，精确抽取
		BookDetail bookDetail = fieldFilter(map);
		if (bookDetail.getBookName() == null 
				|| bookDetail.getIsbn() == null
				|| bookDetail.getBookName() == "" 
				|| bookDetail.getIsbn() == "") {
			throw new ExtractException();
		}
		// 数据持久化
		bookDetail = trim(bookDetail);
		return bookDetail;
	}

	/**
	 * 字段有效性检验
	 * 
	 * @param bookDetail
	 * @return
	 */
	private BookDetail trim(BookDetail bookDetail) {
		if (bookDetail.getAuthorIntro() == null
				|| "".equals(bookDetail.getAuthorIntro().trim()))
			// 手动设置成空
			bookDetail.setAuthorIntro(null);
		if (bookDetail.getAuthor() == null
				|| "".equals(bookDetail.getAuthor().trim()))
			bookDetail.setAuthor(null);
		if (bookDetail.getCatelog() == null
				|| "".equals(bookDetail.getCatelog().trim()))
			bookDetail.setCatelog(null);
		if (bookDetail.getCover_pic() == null
				|| "".equals(bookDetail.getCover_pic().trim()))
			bookDetail.setCover_pic(null);
		if (bookDetail.getDirectory() == null
				|| "".equals(bookDetail.getDirectory().trim()))
			bookDetail.setDirectory(null);
		if (bookDetail.getOutLine() == null
				|| "".equals(bookDetail.getOutLine().trim()))
			bookDetail.setOutLine(null);
		if (bookDetail.getPress() == null
				|| "".equals(bookDetail.getPress().trim()))
			bookDetail.setPress(null);
		if (bookDetail.getPrice() == 0.0)
			bookDetail.setPrice(null);
		if (bookDetail.getPub_price() == 0.0)
			bookDetail.setPub_price(null);
		if (bookDetail.getTranslator() == null
				|| "".equals(bookDetail.getTranslator().trim()))
			bookDetail.setTranslator(null);
		if (bookDetail.getVersion() == null
				|| "".equals(bookDetail.getVersion().trim()))
			bookDetail.setVersion(null);
		
		// TODO others attribute...

		return bookDetail;
	}

	/**
	 * 从子类传递一个集成接口给父类
	 * 
	 * @return
	 */
	protected abstract Integrated getIntegratedDao();

	/**
	 * 字段信息过滤
	 * 
	 * @param map
	 * @return
	 */
	protected abstract BookDetail fieldFilter(Map<String, String> map);

	/**
	 * 抽取
	 * 
	 * @return
	 */
	protected abstract Map<String, String> getElementsInfo(String bookhtml,
			boolean isFile) throws Exception;

}
