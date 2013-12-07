package bimoku.crawler.spider;

import java.io.BufferedReader;

import java.io.ByteArrayOutputStream;

import java.io.IOException;

import java.io.InputStream;

import java.io.InputStreamReader;

import java.util.ArrayList;

import java.util.Collection;

import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.lang.StringUtils;

import org.apache.http.Header;

import org.apache.http.HttpEntity;

import org.apache.http.HttpEntityEnclosingRequest;

import org.apache.http.HttpHost;

import org.apache.http.HttpRequest;

import org.apache.http.HttpResponse;

import org.apache.http.HttpVersion;

import org.apache.http.NoHttpResponseException;

import org.apache.http.ParseException;

import org.apache.http.StatusLine;

import org.apache.http.client.ClientProtocolException;

import org.apache.http.client.HttpRequestRetryHandler;

import org.apache.http.client.entity.GzipDecompressingEntity;

import org.apache.http.client.methods.HttpGet;

import org.apache.http.client.params.ClientPNames;

import org.apache.http.client.params.CookiePolicy;

import org.apache.http.conn.params.ConnRoutePNames;

import org.apache.http.conn.routing.HttpRoute;

import org.apache.http.conn.scheme.PlainSocketFactory;

import org.apache.http.conn.scheme.Scheme;

import org.apache.http.conn.scheme.SchemeRegistry;

import org.apache.http.conn.ssl.SSLSocketFactory;



import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.impl.conn.PoolingClientConnectionManager;

import org.apache.http.message.BasicHeader;

import org.apache.http.params.BasicHttpParams;

import org.apache.http.params.CoreConnectionPNames;

import org.apache.http.params.CoreProtocolPNames;

import org.apache.http.params.HttpParams;

import org.apache.http.protocol.ExecutionContext;

import org.apache.http.protocol.HttpContext;

import org.apache.http.util.EntityUtils;


import bimoku.crawler.queue.IpProxy;
import bimoku.crawler.queue.IpProxylist;

/**
 * 
 * 
 * 
 * @author meiliang
 * 
 * @createtime 8/17, 2013
 * 
 * 
 * 
 * @note 
 */

public class HttpConnectionManager {

	/**
	 * 
	 * 
	 */

	public static final int MAX_TOTAL_CONNECTIONS = 100;

	/**
	 * 
	 * 
	 */

	public static final int MAX_ROUTE_CONNECTIONS = 50;

	/**
	 * 
	 * 
	 */

	public static final int CONNECT_TIMEOUT = 50000;

	/**
	 * 
	 * 
	 */

	public static final int SOCKET_TIMEOUT = 50000;

	/**
	 * 
	 * 
	 */

	public static final long CONN_MANAGER_TIMEOUT = 60000;

	/**
	 * 
	 * 
	 */

	private static HttpParams parentParams;

	/**
	 * 
	 * 
	 */

	private static PoolingClientConnectionManager cm;

	/**
	 * 
	 * 
	 */

	private static DefaultHttpClient httpClient;
	
	/**
	 * 
	 *
	 */

	private static final HttpHost DEFAULT_TARGETHOST = new HttpHost(
			"211.142.236.137",
			80);

	//private IpProxylist ipproxyqueue;
	private int index = 0;

	/**
	 * 
	 * 
	 */

	static {
		
		SchemeRegistry schemeRegistry = new SchemeRegistry();

		schemeRegistry.register(

		new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));

		schemeRegistry.register(

		new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

		cm = new PoolingClientConnectionManager(schemeRegistry);

		cm.setMaxTotal(MAX_TOTAL_CONNECTIONS);

		cm.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);

		cm.setMaxPerRoute(new HttpRoute(DEFAULT_TARGETHOST), 20); 

		parentParams = new BasicHttpParams();

		parentParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);

		parentParams
				.setParameter(ClientPNames.DEFAULT_HOST, DEFAULT_TARGETHOST); 

		parentParams.setParameter(ClientPNames.COOKIE_POLICY,
				CookiePolicy.BROWSER_COMPATIBILITY);

		parentParams.setParameter(ClientPNames.CONN_MANAGER_TIMEOUT,
				CONN_MANAGER_TIMEOUT);

		parentParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				CONNECT_TIMEOUT);

		parentParams.setParameter(CoreConnectionPNames.SO_TIMEOUT,
				SOCKET_TIMEOUT);

		parentParams.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

		parentParams.setParameter(ClientPNames.HANDLE_REDIRECTS, true);

		

		Collection collection = new ArrayList();

		collection
				.add(new BasicHeader("User-Agent",
						"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0)"));

		collection
				.add(new BasicHeader("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));

		collection.add(new BasicHeader("Accept-Language",
				"zh-cn,zh,en-US,en;q=0.5"));

		collection.add(new BasicHeader("Accept-Charset",
				"gbk,utf-8,gb2312;q=0.7,*;q=0.7"));

		collection.add(new BasicHeader("Accept-Encoding", "gzip, deflate"));

		parentParams.setParameter(ClientPNames.DEFAULT_HEADERS, collection);

		

		HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {

			public boolean retryRequest(IOException exception,
					int executionCount, HttpContext context) {

				if (executionCount >= 10) {

					

					return false;

				}

				if (exception instanceof NoHttpResponseException) {

					

					return true;

				}

				if (exception instanceof SSLHandshakeException) {

					

					return false;

				}

				HttpRequest request = (HttpRequest) context
						.getAttribute(ExecutionContext.HTTP_REQUEST);

				boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);

				if (idempotent) {

					

					return true;

				}

				return false;

			}

		};

		httpClient = new DefaultHttpClient(cm, parentParams);
		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "utf8");  
		httpClient.setHttpRequestRetryHandler(httpRequestRetryHandler);

	}

	/**
	 * 
	 * 
	 * 
	 * @param url
	 *            
	 * 
	 * @return 
	 * @throws InterruptedException
	 */

	/*public void setIpProxyQueue(IpProxylist ipproxyqueue) {

		this.ipproxyqueue = ipproxyqueue;
	}*/

	public String getHtml(String url) throws InterruptedException {

		IpProxy ipproxy = new IpProxy();
		ipproxy = IpProxylist.getAproxy(index);
		System.out.println(ipproxy.getUrl());
		HttpHost proxyHost = new HttpHost(ipproxy.getUrl(),
				ipproxy.getDuankou());// ����

	
		
		String html = getHtml(url, proxyHost);

		int count = 0;

		while (StringUtils.isEmpty(html)) {

			count++;

			if (count > 6) {

				System.out.println("网速太慢");
				Thread.sleep(60000);
				retrySet();

			} else if (count > 10) {
				System.out.println("抓取失败");
				break;
			}
			index++;
			if (index >= IpProxylist.size())
				index = 0;

			ipproxy = IpProxylist.getAproxy(index);
			System.out.println(ipproxy.getUrl());
			proxyHost = new HttpHost(ipproxy.getUrl(), ipproxy.getDuankou());// �����

			html = getHtml(url, proxyHost);

		}

		System.out.println(html.length());

		return html;

	}

	/**
	 * 
	 * 
	 * 
	 * @param url
	 *           
	 * 
	 * @return
	 */

	private static String getHtml(String url, HttpHost proxyHost) {

		String html = "";
         System.out.println(url+"+++++++");
		HttpGet httpGet = new HttpGet(url);
		httpGet.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "gbk"); 
		httpGet.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxyHost);

		HttpResponse httpResponse;

		HttpEntity httpEntity;

		try {

			httpResponse = httpClient.execute(httpGet);
			//System.out.println("AAAAAA"+httpResponse.getParams().getParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET));  
			StatusLine statusLine = httpResponse.getStatusLine();

			int statusCode = statusLine.getStatusCode();

			System.out.println(statusCode);

			if (200 != statusCode) {
                if(statusCode ==404){
                	return html="noexist";
                }
				return html;

			}

			httpEntity = httpResponse.getEntity();

			if (httpEntity != null) {

				html = readHtmlContentFromEntity(httpEntity);

			}

		} catch (ClientProtocolException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		} catch (IOException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		} finally {

			if (httpGet != null) {

				httpGet.releaseConnection();

			}

		}

		return html;

	}

	/**
	 * 
	 * 
	 * 
	 * @param httpEntity
	 *            
	 * 
	 * @return 
	 * 
	 * @throws ParseException
	 * 
	 * @throws IOException
	 */

	private static String readHtmlContentFromEntity(HttpEntity httpEntity)
			throws ParseException, IOException {

		String html = "";

		Header header = httpEntity.getContentEncoding();

		if (httpEntity.getContentLength() < 2147483647L) { // EntityUtils�޷�����ContentLength����2147483647L��Entity

			if (header != null && "gzip".equals(header.getValue())) {

				html = EntityUtils.toString(new GzipDecompressingEntity(
						httpEntity), "utf8");

			} else {

				html = EntityUtils.toString(httpEntity, "utf8");

			}

		} else {

			InputStream in = httpEntity.getContent();

			if (header != null && "gzip".equals(header.getValue())) {

				html = unZip(in, "utf8");

			} else {

				html = readInStreamToString(in, "utf8");

			}

			if (in != null) {

				in.close();

			}

		}

		return html;

	}



	public boolean isProxyUsable(HttpHost proxyHost, String url) {

		HttpGet httpGet = new HttpGet(url);

		httpGet.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxyHost);

		try {

			HttpResponse httpResponse = httpClient.execute(httpGet);

			StatusLine statusLine = httpResponse.getStatusLine();

			int statusCode = statusLine.getStatusCode();

			System.out.println(statusCode);

			if (200 != statusCode) {

				return false;

			}

			HttpEntity httpEntity = httpResponse.getEntity();

			if (httpEntity != null) {

				String html = readHtmlContentFromEntity(httpEntity);

				System.out.println(html.length());

				if (StringUtils.isEmpty(html)) {

					return false;

				}

			} else {

				return false;

			}

		} catch (ClientProtocolException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

			return false;

		} catch (IOException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

			return false;

		}

		return true;

	}

	

	private static String unZip(InputStream in, String charSet)
			throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		GZIPInputStream gis = null;

		try {

			gis = new GZIPInputStream(in);

			byte[] _byte = new byte[1024];

			int len = 0;

			while ((len = gis.read(_byte)) != -1) {

				baos.write(_byte, 0, len);

			}

			String unzipString = new String(baos.toByteArray(), charSet);

			return unzipString;

		} finally {

			if (gis != null) {

				gis.close();

			}

			if (baos != null) {

				baos.close();

			}

		}

	}

	

	private static String readInStreamToString(InputStream in, String charSet)
			throws IOException {

		StringBuilder str = new StringBuilder();

		String line;

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(in, charSet));

		while ((line = bufferedReader.readLine()) != null) {

			str.append(line);

			str.append("\n");

		}

		if (bufferedReader != null) {

			bufferedReader.close();

		}

		return str.toString();

	}

	public static void retrySet() {
		SchemeRegistry schemeRegistry = new SchemeRegistry();

		schemeRegistry.register(

		new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));

		schemeRegistry.register(

		new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

		cm = new PoolingClientConnectionManager(schemeRegistry);

		cm.setMaxTotal(MAX_TOTAL_CONNECTIONS);

		cm.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);

		cm.setMaxPerRoute(new HttpRoute(DEFAULT_TARGETHOST), 100); // ���ö�Ŀ����������������

		parentParams = new BasicHttpParams();

		parentParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);

		parentParams
				.setParameter(ClientPNames.DEFAULT_HOST, DEFAULT_TARGETHOST); // ����Ĭ��targetHost

		parentParams.setParameter(ClientPNames.COOKIE_POLICY,
				CookiePolicy.BROWSER_COMPATIBILITY);

		parentParams.setParameter(ClientPNames.CONN_MANAGER_TIMEOUT,
				CONN_MANAGER_TIMEOUT);

		parentParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				CONNECT_TIMEOUT);

		parentParams.setParameter(CoreConnectionPNames.SO_TIMEOUT,
				SOCKET_TIMEOUT);

		parentParams.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

		parentParams.setParameter(ClientPNames.HANDLE_REDIRECTS, true);

		// ����ͷ��Ϣ,ģ�������

		Collection collection = new ArrayList();

		collection
				.add(new BasicHeader("User-Agent",
						"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0)"));

		collection
				.add(new BasicHeader("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));

		collection.add(new BasicHeader("Accept-Language",
				"zh-cn,zh,en-US,en;q=0.5"));

		collection.add(new BasicHeader("Accept-Charset",
				"gbk,utf-8,gb2312;q=0.7,*;q=0.7"));

		collection.add(new BasicHeader("Accept-Encoding", "gzip, deflate"));

		parentParams.setParameter(ClientPNames.DEFAULT_HEADERS, collection);

		

		HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {

			public boolean retryRequest(IOException exception,
					int executionCount, HttpContext context) {

				if (executionCount >= 10) {

					

					return false;

				}

				if (exception instanceof NoHttpResponseException) {

					

					return true;

				}

				if (exception instanceof SSLHandshakeException) {

					

					return false;

				}

				HttpRequest request = (HttpRequest) context
						.getAttribute(ExecutionContext.HTTP_REQUEST);

				boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);

				if (idempotent) {

					

					return true;

				}

				return false;

			}

		};

		httpClient = new DefaultHttpClient(cm, parentParams);

		httpClient.setHttpRequestRetryHandler(httpRequestRetryHandler);

	}

}
