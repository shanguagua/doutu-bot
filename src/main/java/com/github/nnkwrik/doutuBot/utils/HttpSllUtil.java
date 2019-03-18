package com.github.nnkwrik.doutuBot.utils;

/**
 * Author:junru.pan
 * Email: junrupan@sf-express.com
 */


import com.github.nnkwrik.doutuBot.model.Doutula;
import com.github.nnkwrik.doutuBot.model.EmoInfo;
import com.github.nnkwrik.doutuBot.ssl.MySSLProtocolSocketFactory;
import io.github.biezhi.wechat.utils.WeChatUtils;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpSllUtil {
    /**
     * 发送HTTPS	POST请求
     *
     * @param
     * @return 返回响应值
     */
    public static String sendHttpsRequestByGet(String url, Map<String, String> params) throws Exception {
        String body = "";

        //采用绕过验证的方式处理https请求
        SSLContext sslcontext = createIgnoreVerifySSL();
        //设置协议http和https对应的处理socket链接工厂的对象
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslcontext))
                .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        HttpClients.custom().setConnectionManager(connManager);
        //创建自定义的httpclient对象
        CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();
        String result = null;
        try {
            URIBuilder builder = new URIBuilder(url);
            installMap(builder, params);
            URI uri = builder.build();
            SslUtils.ignoreSsl();

            HttpGet get = new HttpGet(uri);
            //指定报文头Content-type、User-Agent
            get.setHeader("Content-type", "application/x-www-form-urlencoded");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
            //执行请求操作，并拿到结果（同步阻塞）
            CloseableHttpResponse response = client.execute(get);
            //获取结果实体
            if (response.getStatusLine().getStatusCode() == 200) {
                // 判断返回状态是否为200
                result = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
            return result;
        } finally {
            client.close();
        }
    }

    public static String sendHttpsRequestByPost(String url, Map<String, String> params) throws Exception {
        //采用绕过验证的方式处理https请求
        SSLContext sslcontext = createIgnoreVerifySSL();

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslcontext))
                .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        HttpClients.custom().setConnectionManager(connManager);

        //创建自定义的httpclient对象
        CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();
        try {
            //创建post方式请求对象
            HttpPost httpPost = new HttpPost(url);
            //指定报文头Content-type、User-Agent
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
            //执行请求操作，并拿到结果（同步阻塞）
            installParamList(httpPost, params);
            CloseableHttpResponse response = client.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            client.close();
        }
    }

    /**
     * @param httpPost
     * @param param
     * @throws Exception
     */
    private static void installParamList(HttpPost httpPost, Map<String, String> param) throws Exception {
        if (null != param) {
            List<NameValuePair> paramList = new ArrayList<>();
            for (String key : param.keySet()) {
                paramList.add(new BasicNameValuePair(key, param.get(key)));
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
            httpPost.setEntity(entity);
        }
    }

    /**
     * 组装参数
     *
     * @param builder
     * @param param
     */
    private static void installMap(URIBuilder builder, Map<String, String> param) {
        if (param != null) {
            for (String key : param.keySet()) {
                builder.addParameter(key, param.get(key));
            }
        }
    }

    /**
     * 绕过证书
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");
        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }
            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }


    public static void main(String[] args) throws Exception {
        String url = "https://www.doutula.com/api/search";
        Map params = new HashMap();
        params.put("keyword", "哈");
        String back = sendHttpsRequestByGet(url, params);
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
