package com.bimoku.common.bean;

import java.io.Serializable;

import javax.persistence.Table;

/**
 * @des:豆瓣网数据
 * @author:Lee
 *
 */
@Table(name="tb_bookdb")
@SuppressWarnings("serial")
public class BookDB extends BookDetail implements Serializable{

}
