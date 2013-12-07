package com.bimoku.integrate;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bimoku.common.bean.Book;
import com.bimoku.common.bean.BookAmazon;
import com.bimoku.common.bean.BookDB;
import com.bimoku.common.bean.BookDD;
import com.bimoku.common.bean.BookDetail;
import com.bimoku.common.bean.BookJD;
import com.bimoku.common.bean.BookPub;
import com.bimoku.repository.dao.BookAmazonDao;
import com.bimoku.repository.dao.BookDBDao;
import com.bimoku.repository.dao.BookDDDao;
import com.bimoku.repository.dao.BookDao;
import com.bimoku.repository.dao.BookJDDao;
import com.bimoku.repository.dao.BookPubDao;

/**
 * @Intro 系统集成模块
 * @author LPM
 * @date 2013-9-2
 * 批量集成
 *
 */
//@Component("butchIntegrated")
public class ButchIntegrated {
	
	@Autowired
	static Integrated integrated;
	@Autowired
	static BookDao bookDao;
	
	@Autowired 
	static BookAmazonDao bookAmazonDao;
	@Autowired 
	static BookDBDao bookDBDao;
	@Autowired 
	static BookDDDao bookDDDao;
	@Autowired 
	static BookJDDao bookJDDao;
	@Autowired
	static BookPubDao bookPubDao;
	
	
	private final static int amCount = bookAmazonDao.getCount("",null);
	private final static int dbCount = bookDBDao.getCount("",null);
	private final static int ddCount = bookDDDao.getCount("",null);
	private final static int jdCount = bookJDDao.getCount("",null);
	private final static int pubCount = bookPubDao.getCount("",null);
	
	private static int am = 0;
	private static int db = 0;
	private static int dd = 0;
	private static int jd = 0;
	private static int pub = 0;
	
	private final static int size = 1000;
	
	/**
	 * 批量集成：
	 * 从明细表中去取出数据，做逐一的集成操作
	 * [1000]条一次
	 */
	public static void butchIntegrated() throws Exception{
		while (isEnd()) {
			//1、读取数据
			List<BookDetail> details = butchQuery();
			//依次做各大分库数据
			
			List<Book> books = new ArrayList<Book>();
			for(BookDetail detail : details){
				//2、遍历数据，并处理每一则数据集成
				books.add(integrated.integration(detail));
			}
			//批量插入到集成表
			bookDao.batchSave(books);
		}
	}
	
	/**
	 * 批量取得各分库的数据
	 * TODO : 有待优化，但是，也可以使用！!
	 * @return
	 */
	public static List<BookDetail> butchQuery(){
		List<BookDetail> details = new ArrayList<BookDetail>();
		//处理amazon的数据
		if(am < amCount){
			List<BookAmazon> bs = bookAmazonDao.butchQuery(am, size);
			am += details.size();
			details.clear();
			for(BookAmazon ba : bs){
				details.add(ba);
			}
			return details;
		}
		if(db < dbCount){
			List<BookDB> bs = bookDBDao.butchQuery(am, size);
			db += details.size();
			details.clear();
			for(BookDB ba : bs){
				details.add(ba);
			}
			return details;
		}
		
		if(dd < ddCount){
			List<BookDD> bs = bookDDDao.butchQuery(am, size);
			dd += details.size();
			details.clear();
			for(BookDD ba : bs){
				details.add(ba);
			}
			return details;
		}
		
		if(jd < jdCount){
			List<BookJD> bs = bookJDDao.butchQuery(am, size);
			jd += details.size();
			details.clear();
			for(BookJD ba : bs){
				details.add(ba);
			}
			return details;
		}
		
		if(pub < pubCount){
			List<BookPub> bs = bookPubDao.butchQuery(am, size);
			pub += details.size();
			details.clear();
			for(BookPub ba : bs){
				details.add(ba);
			}
			return details;
		}
		return details;
	}

	private static boolean isEnd(){
		if(am >= amCount && jd >=jdCount && dd >= ddCount && pub >= pubCount && db >= dbCount){
			return true;
		}
		return false;
	}
}
