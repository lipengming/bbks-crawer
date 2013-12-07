package daobook;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.bimoku.common.bean.Book;
import com.bimoku.repository.dao.BookDao;


/**
 * @author meiliang
 * @serialData @
 * @param args
 */

public class DaoBook {
	private static BookDao dao;
	private final static Long butch_num = 1000L;
	private static AtomicInteger has_handle = new AtomicInteger(0);
	private static int count = 0;

	static {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"classpath:/beans.xml");
		dao = ctx.getBean("bookDao", BookDao.class);
	}

	public static void main(String[] args) throws Exception {
		Long t1 = System.currentTimeMillis();

		daoBook();

		System.out.println("TIMES: " + (System.currentTimeMillis() - t1));
	}

	private static void daoBook() throws Exception {
		
		count = dao.getCount("", null);

		while (has_handle.get() != count) {
			java.util.List<Object> values = new ArrayList<Object>();
			values.add(has_handle.get());
			values.add(butch_num);
			Long t1 = System.currentTimeMillis();
			List<Book> list = dao.butchQuery(values);// dao.search(sql, values);
			System.out.print("QUERY TIMES: " + (System.currentTimeMillis() - t1));

			
			String sql = "insert into crawl_task(cra_id,cra_keyword,cra_orig_time,cra_gap,cra_status,cra_is_end,start_time,end_time) values (?,?,?,?,?,?,?,?)";
			List<List<Object>> vals = new ArrayList<List<Object>>();
			for (Book b : list) {
				List<Object> objs = new ArrayList<Object>();
				objs.add(b.getId());
				//。。。
				objs.add(b.getBookname());
				objs.add("2013-07-01 09:39:49");
				objs.add(20);
				objs.add(3);
				objs.add(1);
				objs.add("2013-07-01 00:00:00");
				objs.add("2013-09-17 17:00:00");
				
				vals.add(objs);
				has_handle.incrementAndGet();
			}
			Long t2 = System.currentTimeMillis();
			dao.batchAdd(sql, vals);
			System.out.println(" BUTCH ADD TIMES: " + (System.currentTimeMillis() - t2));
			// 执行批量更新
			if (list.size() < butch_num)
				break;
		}
	}

}
