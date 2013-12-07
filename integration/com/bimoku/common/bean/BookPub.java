package com.bimoku.common.bean;

import java.io.Serializable;

import javax.persistence.Table;

@Table(name="tb_bookpub")
@SuppressWarnings("serial")
public class BookPub extends BookDetail implements Serializable{

}
