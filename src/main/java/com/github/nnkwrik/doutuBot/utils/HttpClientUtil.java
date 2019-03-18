package com.github.nnkwrik.doutuBot.utils;

import com.github.nnkwrik.doutuBot.model.Doutula;
import com.github.nnkwrik.doutuBot.model.EmoInfo;
import io.github.biezhi.wechat.utils.StringUtils;
import io.github.biezhi.wechat.utils.WeChatUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:junruPan
 * @email:junrupan@sf-express.com
 */
public class HttpClientUtil {


    private static CloseableHttpClient getClient() {
        return HttpClients.createDefault();
    }


    public static CloseableHttpClient getIgnoeSSLClient() throws Exception {


        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectTimeout(2000)
                .setConnectionRequestTimeout(2000)
                .setStaleConnectionCheckEnabled(true)
                .build();
      /*  SSLContext sslContext = SSLContext.getInstance("TLS");*/
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        }).build();
        //创建httpClient
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).setSSLContext(sslContext).setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        return client;
    }


    private static void installMap(URIBuilder builder, Map<String, String> param) {
        if (param != null) {
            for (String key : param.keySet()) {
                builder.addParameter(key, param.get(key));
            }
        }
    }

    private static void closeClient(CloseableHttpResponse response, CloseableHttpClient httpclient) throws Exception {
        if (response != null) {
            response.close();
        }
        httpclient.close();
    }

    private static void installParamList(HttpPost httpPost, Map<String, String> param) throws Exception {
        if (param != null) {
            List<NameValuePair> paramList = new ArrayList<>();
            for (String key : param.keySet()) {
                paramList.add(new BasicNameValuePair(key, param.get(key)));
            }
            // 模拟表单
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
            httpPost.setEntity(entity);
        }

    }


    /**
     * @param url
     * @param param
     * @return
     */
    public static String doGet(String url, Map<String, String> param, String charset, boolean ignoreSSL) throws Exception {
        // 创建Httpclient对象
        CloseableHttpClient httpclient;
        if (ignoreSSL) {
            httpclient = getIgnoeSSLClient();
        } else {
            httpclient = getClient();
        }
        CloseableHttpResponse response = null;
        String result = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            installMap(builder, param);
            URI uri = builder.build();
            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);
            // 执行请求
            response = httpclient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                // 判断返回状态是否为200
                result = EntityUtils.toString(response.getEntity(), StringUtils.isEmpty(charset) ? "UTF-8" : charset);
            }
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            closeClient(response, httpclient);
        }

    }

    public static String doGet(String url) throws Exception {
        return doGet(url, null, null,false);
    }

    public static String doPost(String url) throws Exception {
        return doPost(url, null, null);
    }

    /**
     * @param url
     * @param bytes
     * @param contentType
     * @param charSet
     * @return
     * @throws Exception
     */
    public static String sPostRaw(String url, byte[] bytes, String contentType, String charSet) throws Exception {
        //CloseableHttpClient httpClient = getClient(); // 创建Httpclient对象
        CloseableHttpClient httpClient = getIgnoeSSLClient();
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            ByteArrayEntity be = new ByteArrayEntity(bytes);
            be.setContentEncoding("UTF-8");
            /*    be.setContentType("application/json");*/
            httpPost.setEntity(be);
            if (!StringUtils.isEmpty(contentType)) {
                httpPost.setHeader("Content-type", contentType);
            }
            response = httpClient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity(), StringUtils.isEmpty(charSet) ? "utf-8" : charSet);
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            closeClient(response, httpClient);
        }
    }

    public static String sPostRaw(String url, byte[] bytes, String contentType, String Accept, String charSet) throws Exception {
        //CloseableHttpClient httpClient = getClient(); // 创建Httpclient对象
        CloseableHttpClient httpClient = getIgnoeSSLClient();
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            ByteArrayEntity be = new ByteArrayEntity(bytes);
            be.setContentEncoding("UTF-8");
            /* be.setContentType("application/json");*/
            httpPost.setEntity(be);
            if (!StringUtils.isEmpty(Accept)) {
                httpPost.setHeader("Accept", Accept);
            }
            if (!StringUtils.isEmpty(contentType)) {
                httpPost.setHeader("Content-type", contentType);
            }
            response = httpClient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity(), StringUtils.isEmpty(charSet) ? "utf-8" : charSet);
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            closeClient(response, httpClient);
        }
    }


    /**
     * @param url
     * @param bytes
     * @param contentType
     * @return
     * @throws IOException
     */
    public static byte[] bPostRaw(String url, byte[] bytes, String contentType) throws Exception {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = getClient();
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new ByteArrayEntity(bytes));
            if (!StringUtils.isEmpty(contentType)) {
                httpPost.setHeader("Content-type", contentType);
            }
            response = httpClient.execute(httpPost);
            HttpEntity entityResponse = response.getEntity();
            int contentLength = (int) entityResponse.getContentLength();
            if (contentLength <= 0) {
                throw new IOException("No response");
            }
            byte[] respBuffer = new byte[contentLength];
            if (entityResponse.getContent().read(respBuffer) != respBuffer.length) {
                throw new IOException("Read response buffer error");
            }
            return respBuffer;
        } catch (Exception e) {
            throw e;
        } finally {
            closeClient(response, httpClient);
        }
    }

    /**
     * @param url
     * @param bytes
     * @return
     * @throws Exception
     */
    public static byte[] getBPostRaw(String url, byte[] bytes) throws Exception {
        return bPostRaw(url, bytes, null);
    }

    /**
     * @param url
     * @param bytes
     * @return
     * @throws Exception
     */
    public static String getSPostRaw(String url, byte[] bytes) throws Exception {
        return sPostRaw(url, bytes, null, null);
    }

    /**
     * @param url
     * @param param
     * @return
     */
    public static String doPost(String url, Map<String, String> param, String charSet) throws Exception {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = getClient();
        CloseableHttpResponse response = null;
        String result;
        try {// 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建参数列表
            installParamList(httpPost, param);
            response = httpClient.execute(httpPost);
            result = EntityUtils.toString(response.getEntity(), StringUtils.isEmpty(charSet) ? "utf-8" : charSet);
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            closeClient(response, httpClient);
        }
    }

    /**
     * @param url
     * @param json
     * @return
     */
    public static String doPostJson(String url, String json, String charset) throws Exception {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = getClient();
        CloseableHttpResponse response = null;
        String result;
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            result = EntityUtils.toString(response.getEntity(), StringUtils.isEmpty(charset) ? "utf-8" : charset);
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            closeClient(response, httpClient);
        }
    }

    /**
     * @param request
     * @param len
     * @return
     * @throws IOException
     */
    public static byte[] getServletBytes(HttpServletRequest request, Integer len) throws IOException {
        //获取长度
        int length = len == null ? request.getContentLength() : len;
        //定义数组，长度为请求参数的长度
        byte[] bytes = new byte[length];
        //获取请求内容，转成数据输入流
        DataInputStream dis = new DataInputStream(request.getInputStream());
        //定义输入流读取数
        int readcount = 0;
        while (readcount < length) {
            //读取输入流，放入bytes数组，返回每次读取的数量
            int index = dis.read(bytes, readcount, length);
            //下一次的读取开始从readcount开始
            readcount = index + readcount;
        }
        return bytes;
    }


    public static void main(String[] args) throws Exception {
        String url = "http://www.doutula.com/api/search";
        Map params = new HashMap();
        params.put("keyword", "哈");

        String back = HttpClientUtil.doGet(url, params, null,true);
        Doutula resultJson = Utils.fromJson(back, Doutula.class);
        List<EmoInfo> infoList = resultJson.getData().getList();
        String emoUrl;
        while (true) {
            //靠前的比较准
            emoUrl = infoList.get(WeChatUtils.random(0, 10)).getImage_url();
            if (emoUrl.startsWith("https:")) {
                break;
            }

        }
        System.out.println("图片:" + emoUrl);
    }


}