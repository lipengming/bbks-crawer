package bimoku.crawler.common;

import java.io.IOException;

import java.io.InputStream;

import java.util.Properties;
import java.util.Scanner;

public class DBUtils {


	public static Properties propertiesReader(String config) {

		Properties p = new Properties();

	

		DBUtils d = new DBUtils();

		InputStream i = d.getClass().getResourceAsStream(config);

		try {

			p.load(i);

		} catch (IOException e) {

			System.out.println("配置路径不存在" + e.getMessage());

		}

		return p;

	}

	}

	
	


