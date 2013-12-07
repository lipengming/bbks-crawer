package com.bimoku.repository.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bimoku.common.bean.Comment;


public class CommentDaoTest {
	CommentDao dao;
	@Before
	public void setUp() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/beans.xml");
		dao = ctx.getBean("commentDao",CommentDao.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAll() {
		//dao.get(1);
		java.util.List<Comment> cs = new ArrayList<Comment>();
		for(int a=0;a<100;a++){
			Comment c = new Comment();
			c.setIsbn(a+"isbn");
			cs.add(c);
		}
		dao.batchSave(cs);
	}

}
