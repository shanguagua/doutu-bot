package com.github.nnkwrik.doutuBot.utils;

import cn.hutool.http.HttpUtil;
import com.github.nnkwrik.doutuBot.model.Doutula;
import com.github.nnkwrik.doutuBot.model.EmoInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.biezhi.wechat.utils.WeChatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author biezhi
 *         17/06/2017
 */
public final class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    private static final String  DOUTULA_URL = "https://www.doutula.com/api/search";

    private Utils() {
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable!=null){

            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isBlank(String str) {
        return null == str || "".equals(str.trim());
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str) && !"null".equalsIgnoreCase(str);
    }


    /**
     * obj转json
     */
    public static String toJson(Object o) {
        try {
            return gson.toJson(o);
        } catch (Exception e) {
            log.error("Json序列化失败", e);
        }
        return null;
    }

    /**
     * json转obj
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return gson.fromJson(json, classOfT);
        } catch (Exception e) {
            log.error("Json反序列化失败", e);
        }
        return null;
    }

    /**
     *
     * @param keyword
     * @return
     * @throws Exception
     */
    public  static String  getPicFromDoutula(String keyword)throws Exception{

        Map params = new HashMap();
        params.put("keyword",keyword);
        //获取图片的url
        String back =  HttpSllUtil.sendHttpsRequestByGet(DOUTULA_URL,params);
        //转json
        Doutula resultJson = Utils.fromJson(back, Doutula.class);
        List<EmoInfo> infoList = resultJson.getData().getList();
        String emoUrl;
        while (true) {
            //靠前的比较准?
            emoUrl = infoList.get(WeChatUtils.random(0, 10)).getImage_url();
            if (emoUrl.startsWith("https:")) {
                break;
            }
        }
        return  emoUrl;
    }


}
