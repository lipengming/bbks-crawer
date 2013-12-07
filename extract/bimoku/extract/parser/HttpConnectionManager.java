package bimoku.extract.parser;


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
import org.apache.http.entity.ContentType;
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







public class HttpConnectionManager {

    

   

    public static final int MAX_TOTAL_CONNECTIONS = 100;

    

   

    public static final int MAX_ROUTE_CONNECTIONS = 50;

    


    public static final int CONNECT_TIMEOUT = 50000;

    

   

    public static final int SOCKET_TIMEOUT = 50000;

    

    /**

     * 

     */

    public static final long CONN_MANAGER_TIMEOUT = 60000;

    

    /**

     * 

     */

    private static HttpParams parentParams;

    

    /**

     * http�̳߳ع�����

     */

    private static PoolingClientConnectionManager cm;

    

    /**

     * http�ͻ���

     */

    private static DefaultHttpClient httpClient;

    

    /**

     * Ĭ��Ŀ������

     */

    private static final HttpHost DEFAULT_TARGETHOST = new HttpHost("http://category.dangdang.com/all/?category_path=01.00.00.00.00.00", 80);

    

    /**

     * ��ʼ��http���ӳأ����ò���httpͷ�ȵ���Ϣ

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



        cm.setMaxPerRoute(new HttpRoute(DEFAULT_TARGETHOST), 20);        //���ö�Ŀ����������������

        

        parentParams = new BasicHttpParams(); 

        parentParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);



        parentParams.setParameter(ClientPNames.DEFAULT_HOST, DEFAULT_TARGETHOST);    //����Ĭ��targetHost

        

        parentParams.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);

        

        parentParams.setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, CONN_MANAGER_TIMEOUT);

        parentParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECT_TIMEOUT);

        parentParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, SOCKET_TIMEOUT);

        

        parentParams.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

        parentParams.setParameter(ClientPNames.HANDLE_REDIRECTS, true);

        

        //����ͷ��Ϣ,ģ�������

        Collection collection = new ArrayList();

        collection.add(new BasicHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0)"));

        collection.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));

        collection.add(new BasicHeader("Accept-Language", "zh-cn,zh,en-US,en;q=0.5"));

        collection.add(new BasicHeader("Accept-Charset", "ISO-8859-1,utf-8,gbk,gb2312;q=0.7,*;q=0.7"));

        collection.add(new BasicHeader("Accept-Encoding", "gzip, deflate"));

        

        parentParams.setParameter(ClientPNames.DEFAULT_HEADERS, collection);

        //�������Դ���

        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {

            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {

                if (executionCount >= 5) {

                    // ����������Դ�����ô�Ͳ�Ҫ������

                    return false;

                }

                if (exception instanceof NoHttpResponseException) {

                    // �����������������ӣ���ô������

                    return true;

                }

                if (exception instanceof SSLHandshakeException) {

                    // ��Ҫ����SSL�����쳣

                    return false;

                }

                HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);

                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);

                if (idempotent) {

                    // ���������Ϊ���ݵȵģ���ô������

                    return true;

                }

                return false;

            }

        };

        

        httpClient = new DefaultHttpClient(cm, parentParams);

        

        httpClient.setHttpRequestRetryHandler(httpRequestRetryHandler);

    }

    

    /**

     * ץȡҳ�����

     * @param url Ŀ��ҳ���url

     * @return ҳ�����

     */

    public static String getHtml(String url) {

        HttpHost proxyHost = new HttpHost("211.142.236.137", 8080);//����

        

        String html = getHtml(url, proxyHost);

        

        int count = 0;

        while(StringUtils.isEmpty(html)){

            proxyHost = new HttpHost("211.142.236.137", 80);//�����

            html = getHtml(url, proxyHost);

            count++;

            if(count > 3){

                System.out.println("ץȡʧ��");

                break;

            }

        }

        

System.out.println(html.length());

        return html;

    }

    

    /**

     * ץȡurl��ָ��ҳ�����

     * @param url Ŀ��ҳ���url

     * @return ҳ�����

     */

    private static String getHtml(String url, HttpHost proxyHost) {

        String html = "";

        HttpGet httpGet = new HttpGet(url);

        httpGet.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);//���ô���

        

        HttpResponse httpResponse;

        HttpEntity httpEntity;

        try {

            httpResponse = httpClient.execute(httpGet);

            

            StatusLine statusLine = httpResponse.getStatusLine();

            int statusCode = statusLine.getStatusCode();

System.out.println(statusCode);

            if(200 != statusCode) {

                return html;

            }

            

            httpEntity = httpResponse.getEntity();

            if(httpEntity != null){

                html = readHtmlContentFromEntity(httpEntity);

            }

        } catch (ClientProtocolException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        } catch (IOException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        } finally {

            if(httpGet != null){

                httpGet.releaseConnection();

            }

        }

        

        return html;

    }

    

    /**

     * ��response���ص�ʵ���ж�ȡҳ�����

     * @param httpEntity Httpʵ��

     * @return ҳ�����

     * @throws ParseException

     * @throws IOException

     */

    private static String readHtmlContentFromEntity(HttpEntity httpEntity) throws ParseException, IOException {

        String html = "";

        Header header = httpEntity.getContentEncoding();

        if(httpEntity.getContentLength() < 2147483647L){            //EntityUtils�޷�����ContentLength����2147483647L��Entity

            if(header != null && "gzip".equals(header.getValue())){

                html = EntityUtils.toString(new GzipDecompressingEntity(httpEntity));

            } else {

                html = EntityUtils.toString(httpEntity);

            }

        } else {

            InputStream in = httpEntity.getContent();

            if(header != null && "gzip".equals(header.getValue())){

                html = unZip(in, ContentType.getOrDefault(httpEntity).getCharset().toString());

            } else {

                html = readInStreamToString(in, ContentType.getOrDefault(httpEntity).getCharset().toString());

            }

            if(in != null){

                in.close();

            }

        }

        return html;

    }

    

    /**

     * ���Դ����Ƿ���ã���ʵ��getHtml(String url, HttpHost proxyHost)�Ĵ����࣬Ϊ�˴ӹ����������ʱ����

     * @param httpHost ��װ�˴����ip��ַ�Ͷ˿�

     * @param url �������Ե�ҳ��

     * @return true ���� false ������

     */

    public boolean isProxyUsable(HttpHost proxyHost, String url) {

        HttpGet httpGet = new HttpGet(url);

        httpGet.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);

        try {

            HttpResponse httpResponse = httpClient.execute(httpGet);

            

            StatusLine statusLine = httpResponse.getStatusLine();

            int statusCode = statusLine.getStatusCode();

System.out.println(statusCode);

            if(200 != statusCode) {

                return false;

            }

            HttpEntity httpEntity = httpResponse.getEntity();

            if(httpEntity != null) {

                String html = readHtmlContentFromEntity(httpEntity);

System.out.println(html.length());

                if(StringUtils.isEmpty(html)){

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

    

    /**

     * ��ѹ���������ص�gzip��

     * @param in ץȡ���ص�InputStream��

     * @param charSet ҳ�����ݱ���

     * @return ҳ�����ݵ�String��ʽ

     * @throws IOException

     */

    private static String unZip(InputStream in, String charSet) throws IOException {

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

            if(baos != null){

                baos.close();

            }

        }

    }

    

    /**

     * ��ȡInputStream��

     * @param in InputStream��

     * @return �����ж�ȡ��String

     * @throws IOException

     */

    private static String readInStreamToString(InputStream in, String charSet) throws IOException {

        StringBuilder str = new StringBuilder();

        String line;

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, charSet));

        while((line = bufferedReader.readLine()) != null){

            str.append(line);

            str.append("\n");

        }

        if(bufferedReader != null) {

            bufferedReader.close();

        }

        return str.toString();

    }

    

    /**

     * for test

     * @author lidongyang

     * @createtime Oct 18, 2012 2:35:09 PM

     */

    public class Test implements Runnable {

        String url;

        int threadNum;

        

        public Test() {

            

        }

        

        public Test(String url, int threadNum) {

            this.url = url;

            this.threadNum = threadNum;

        }

        

        @Override

        public void run() {

            getHtml(url);

        }

    }

    

    

    /**

     * for test

     * @param args

     * @throws InterruptedException 

     */

   /* public static void main(String[] args) throws InterruptedException{

        HttpConnectionManager httpConnectionManager = new HttpConnectionManager();

        Date start = new Date();

        
        System.out.println(httpConnectionManager.getHtml("http://category.dangdang.com/all/?category_path=01.00.00.00.00.00"));

        Date end = new Date();

        System.out.println((end.getTime() - start.getTime())/1000.0 + " ��");

    }*/

}