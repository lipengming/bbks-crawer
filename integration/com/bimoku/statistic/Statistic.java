package com.bimoku.statistic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.codehaus.jettison.json.JSONException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


import com.bimoku.common.bean.Book;
import com.bimoku.repository.dao.BookDao;
import com.bimoku.util.RelationMapper;

/**
 * 统计数据
 * @author LPM
 *
 */
public class Statistic {
	
	private static AtomicInteger ONE_NUM = new AtomicInteger();
	private static AtomicInteger TWO_NUM = new AtomicInteger();
	private static AtomicInteger THREE_NUM = new AtomicInteger();
	private static AtomicInteger FOUR_NUM = new AtomicInteger();
	private static AtomicInteger FIVE_NUM = new AtomicInteger();
	
	private static AtomicInteger EXCEPTION_NUM = new AtomicInteger();
	
	private static BookDao dao;
	private final static Long butch_num = 1000L;
	private static AtomicInteger has_handle = new AtomicInteger(0);
	private static int count = 0;
	
	public static void main(String[] args) {
		Long t1 = System.currentTimeMillis();
		
		statistic();
		
		System.out.println("TIMES: "+(System.currentTimeMillis() - t1));
	}
	
	private static void statistic(){
		StringBuffer sb = new StringBuffer();
		String pix = "=================================\n";
		//启动sqring容器
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/beans.xml");
		dao = ctx.getBean("bookDao", BookDao.class);
		
		//获取记录总数
		count = dao.getCount("", null);
		
		sb.append(pix);
		sb.append("TOTAL:"+count);
		sb.append("\n"+pix);
		
		while (has_handle.get() != count) {
			List param = new ArrayList();
			param.add(has_handle.get());
			param.add(butch_num);
			List<String> relations = dao.getStrList("SELECT RELATIONSHIP from t_book where ID>=(SELECT ID from t_book ORDER BY ID LIMIT ?,1) LIMIT ?",param);
			for(String relation : relations){
				try {
					statistic_num(relation);
					has_handle.incrementAndGet();
				} catch (Exception e) {
					EXCEPTION_NUM.incrementAndGet();
					continue;
				}
			}
			System.out.println(param);
			if(relations.size() < butch_num) break;
		}
		sb.append("ONE:"+ONE_NUM.get());
		sb.append("\n");
		sb.append("TWO:"+TWO_NUM.get());
		sb.append("\n");
		sb.append("THREE:"+THREE_NUM.get());
		sb.append("\n");
		sb.append("FOUR:"+FOUR_NUM.get());
		sb.append("\n");
		sb.append("FIVE:"+FIVE_NUM.get());
		sb.append("\n");
		sb.append(pix);
		System.out.println(sb.toString());
	}
	
	private static void statistic_num(String relation) throws JSONException{
		
		RelationMapper mapper = RelationMapper.str2Bean(relation);
		int a = 0;
		
		assert(mapper != null);
		
		if( mapper.getAM() != null && !"".equals(mapper.getAM()) ){
			a++;
		}
		if( mapper.getDB() != null && !"".equals(mapper.getDB()) ){
			a++;
		}
		if( mapper.getDD() != null && !"".equals(mapper.getDD()) ){
			a++;
		}
		if( mapper.getJD() != null && !"".equals(mapper.getJD()) ){
			a++;
		}
		if( mapper.getPUB() != null && !"".equals(mapper.getPUB()) ){
			a++;
		}
		
		switch (a) {
			case 1:ONE_NUM.incrementAndGet();break;
			case 2:TWO_NUM.incrementAndGet();break;
			case 3:THREE_NUM.incrementAndGet();break;
			case 4:FOUR_NUM.incrementAndGet();break;
			case 5:FIVE_NUM.incrementAndGet();break;
			default:
				throw new IllegalArgumentException("没有找到数据来自的站点，这个错误被认为是不可能出现的错误！！！");
		}
	}
	
}	
