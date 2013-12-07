package com.bimoku.persistence.dao.impl;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bimoku.common.bean.Book;
import com.bimoku.repository.dao.BookDao;

public class BaseDaoMysqlImplTest {
	
	BookDao dao;
	
	@Before
	public void setUp() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/beans.xml");
		dao = ctx.getBean("bookDao",BookDao.class);
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("tear down");
	}

	@Test
	public void testSearchStringListOfObject() {
//		String sql = "SELECT RELATIONSHIP from t_book where ID>=(SELECT ID from t_book ORDER BY ID LIMIT ?,1) LIMIT ?"+dao.getTableName()+" ";
//		
		java.util.List<Object> values = new ArrayList<Object>();
		values.add(0);
		values.add(1000);
		List<Book> list = dao.butchQuery(values);//dao.search(sql, values);
		for(Book b : list){
			System.out.println("name:"+b.getBookname());
		}
	
	}

	@Test
	public void testSearchMapOfStringObjectPageBeanOfT() {
		fail("Not yet implemented");
	}

	@Test
	public void testSearchStringListOfObjectClassOfE() {
		fail("Not yet implemented");
	}

}
