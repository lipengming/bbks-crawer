package bimoku.crawler.ipproxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import bimoku.crawler.queue.IpProxy;
import bimoku.crawler.queue.IpProxylist;

public class SetIpProxy {
	public static IpProxylist ipproxyqueue;

	public  void setIpProxy() {
		FileReader reader = null;
		try {
			String curdir = new String();
			File file = new File("");
			curdir = file.getAbsolutePath();
			curdir = curdir + "/proxy/ipproxy.txt";
			reader = new FileReader(curdir);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		BufferedReader br = new BufferedReader(reader);
		String str = null;
		try {
			while ((str = br.readLine()) != null) {
				String s[] = str.split(",");
				IpProxy ipproxy = new IpProxy();
				ipproxy.setUrl(s[0]);
				 //System.out.println(s[0]);
				ipproxy.setDuankou(Integer.parseInt(s[1]));
				// System.out.println(ipproxy.getDuankou());
				
				IpProxylist.addAproxy(ipproxy);

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(NullPointerException e){
			e.printStackTrace();
		}
	}

}
