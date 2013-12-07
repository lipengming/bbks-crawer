package com.bimoku.common.bean;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Table(name="t_comment")
public class Comment implements Serializable{
	private Integer id;//主键
	private String isbn;//isbn
	private String com_user;
	private String com_avatar;
	private String com_content;
	private String create_at;
	private String com_zhandian ;
	
	
	public Comment(){}
	public Comment(int id, String isbn, String com_user, String com_avatar, String com_content,String create_at,String com_zhandian) {
		this.id = id;
		this.isbn = isbn;  //图书的ISBN
		this.com_user = com_user;  //评论的用户名
		this.com_avatar = com_avatar;  //用户的图像
		this.com_content = com_content; //评论的内容
		this.create_at = create_at;  //评论的时间
		this.com_zhandian = com_zhandian; //评论来自的站点
	}
	
	
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCom_zhandian() {
		return com_zhandian;
	}
	public void setCom_zhandian(String com_zhandian) {
		this.com_zhandian = com_zhandian;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public String getCom_user() {
		return com_user;
	}
	public void setCom_user(String com_user) {
		this.com_user = com_user;
	}
	public String getCom_avatar() {
		return com_avatar;
	}
	public void setCom_avatar(String com_avatar) {
		this.com_avatar = com_avatar;
	}
	public String getCom_content() {
		return com_content;
	}
	public void setCom_content(String com_content) {
		this.com_content = com_content;
	}
	public String getCreate_at() {
		return create_at;
	}
	public void setCreate_at(String create_at) {
		this.create_at = create_at;
	}
	
	
	@Override
	public String toString() {
		return "Comment [id=" + id + ", isbn=" + isbn + ", com_user="
				+ com_user + ", com_avatar=" + com_avatar + ", com_content="
				+ com_content + ", create_at=" + create_at + ", com_zhandian="
				+ com_zhandian + "]";
	}

	
	
}
