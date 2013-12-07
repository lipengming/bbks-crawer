package com.bimoku.repository.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.bimoku.common.bean.Book;
import com.bimoku.common.bean.Comment;
import com.bimoku.persistence.dao.BaseDao;
import com.bimoku.persistence.dao.impl.BaseDaoMysqlImpl;


/**
 * book对象基本操作
 * @date 2013-8-20
 * @version v0.1.2[last version]
 * @author LPM
 *
 */
public interface CommentDao extends BaseDao<Comment, Integer>{
	
}

@Repository("commentDao")
class CommentDaoImpl extends BaseDaoMysqlImpl<Comment, Integer> implements CommentDao{
	CommentDaoImpl(){
		super(Comment.class);
	}
}
