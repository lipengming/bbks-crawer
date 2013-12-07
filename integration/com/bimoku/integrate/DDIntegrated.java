package com.bimoku.integrate;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bimoku.common.bean.Book;
import com.bimoku.common.bean.BookDD;
import com.bimoku.common.bean.BookDetail;
import com.bimoku.repository.dao.BookDDDao;

@Service("ddIntegrated")
public class DDIntegrated extends Integrated{
	
	@Autowired
	BookDDDao bookDDDao;
	
	@Override
	protected BookDetail putDataIntoDetail(BookDetail detail) {
		
		BookDD dd = new BookDD();
		dd.setIsbn(detail.getIsbn());
		dd = bookDDDao.searchOne(dd);
		//判断是否存在
		if(dd != null && dd.getId() != null && dd.getId() != 0){
			BookDD bdd = (BookDD) detail;
			bdd.setId(dd.getId());
			//System.out.println(dd);
			//跟新操作
			return bookDDDao.update(bdd);
		}
		return bookDDDao.save((BookDD)detail);
	}

	@Override
	protected Book filterFileds(Book book, BookDetail detail) {
		//TODO
		return book;
	}

	@Override
	protected int[] putDatasIntoDetail(List<BookDetail> details) {
		List<BookDD> list = new ArrayList<BookDD>();
		for(BookDetail bd : details){
			list.add((BookDD)bd);
		}
		return bookDDDao.batchSave(list);
	}

}
