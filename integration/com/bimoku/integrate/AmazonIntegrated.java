package com.bimoku.integrate;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bimoku.common.bean.Book;
import com.bimoku.common.bean.BookAmazon;
import com.bimoku.common.bean.BookDetail;
import com.bimoku.repository.dao.BookAmazonDao;

@Service("amazonIntegrated")
public class AmazonIntegrated extends Integrated{
	
	@Autowired
	BookAmazonDao bookAmazonDao;
	
	@Override
	protected BookDetail putDataIntoDetail(BookDetail detail) {
		BookAmazon am = new BookAmazon();
		am.setIsbn(detail.getIsbn());
		am = bookAmazonDao.searchOne(am);
		//判断是否存在
		if(am != null){
			BookAmazon bam = (BookAmazon) detail;
			bam.setId(am.getId());
			//跟新操作
			return bookAmazonDao.update(bam);
		}
		BookDetail deta = bookAmazonDao.save((BookAmazon)detail);
		return deta;
	}

	@Override
	protected Book filterFileds(Book book, BookDetail detail) {
		//TODO
		return book;
	}

	@Override
	protected int[] putDatasIntoDetail(List<BookDetail> details) {
		List<BookAmazon> list = new ArrayList<BookAmazon>();
		for(BookDetail bd : details){
			list.add((BookAmazon)bd);
		}
		return bookAmazonDao.batchSave(list);
	}

}
