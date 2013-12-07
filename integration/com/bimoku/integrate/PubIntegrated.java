package com.bimoku.integrate;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bimoku.common.bean.Book;
import com.bimoku.common.bean.BookDetail;
import com.bimoku.common.bean.BookPub;
import com.bimoku.repository.dao.BookPubDao;

@Service("pubIntegrated")
public class PubIntegrated extends Integrated{
	
	@Autowired
	BookPubDao bookPubDao;
	
	@Override
	protected BookDetail putDataIntoDetail(BookDetail detail) {
		BookPub dd = new BookPub();
		dd.setIsbn(detail.getIsbn());
		dd = bookPubDao.searchOne(dd);
		
		//判断是否存在
		if(dd != null){
			BookPub bdd = (BookPub) detail;
			bdd.setId(dd.getId());
			//跟新操作
			return bookPubDao.update(bdd);
		}
		return bookPubDao.save((BookPub)detail);
	}

	@Override
	protected Book filterFileds(Book book, BookDetail detail) {
		//TODO
		return book;
	}
	
	@Override
	protected int[] putDatasIntoDetail(List<BookDetail> details) {
		List<BookPub> list = new ArrayList<BookPub>();
		for(BookDetail bd : details){
			list.add((BookPub)bd);
		}
		return bookPubDao.batchSave(list);
	}
}
