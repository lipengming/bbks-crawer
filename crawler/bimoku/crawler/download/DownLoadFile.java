package bimoku.crawler.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



public class DownLoadFile {
	public   String getFileNameByUrl(String url)
	{

	
	url= Md5(url)+".html";
	return url;
	}
	private   void saveToLocal(String html, String filePath)  {
		
		 FileOutputStream foutput = null;
		  try {
		   foutput = new FileOutputStream(filePath);
		   foutput.write(html.getBytes("gbk"));
		   
		  } catch(IOException ex) {
		   ex.printStackTrace();
		  }
		  finally {
		   try {
		    foutput.flush();
		    foutput.close();
		   } catch (IOException ex) {
		    ex.printStackTrace();
		   }
		  }
	}
		
	
	public  void downloadFile(String url,String html,String filePath) {
		File fileDir = new File(filePath );

				  if(!fileDir.exists()){
				   fileDir.mkdir();//如果文件路径不存在，创建一个文件
				  }

		filePath = filePath+"/"
		+ getFileNameByUrl(url);
		
		saveToLocal(html, filePath);
		
		}
	 private static String Md5(String plainText) {
		   String result = null;
		   try {
		    MessageDigest md = MessageDigest.getInstance("MD5");
		    md.update(plainText.getBytes());
		    byte b[] = md.digest();
		   // System.out.println(b);
		   /* for(int i=0;i<16;i++){
		    	System.out.println(b[i]) ;
		    }*/
		    int i;
		    StringBuffer buf = new StringBuffer("");
		    for (int offset = 0; offset < b.length; offset++) {
		     i = b[offset];
		     if (i < 0)
		      i += 256;
		     if (i < 16)
		      buf.append("0");
		     buf.append(Integer.toHexString(i));
		    }
		    // result = buf.toString();  //md5 32bit
		    // result = buf.toString().substring(8, 24))); //md5 16bit
		    result = buf.toString().substring(8, 24);
		  //  System.out.println("mdt 16bit: " + buf.toString().substring(8, 24));
		    //System.out.println("md5 32bit: " + buf.toString() );
		   } catch (NoSuchAlgorithmException e) {
		    e.printStackTrace();
		   }
		   return result;
		 }
	
	
}
