package com.github.nnkwrik.doutuBot.emotion;

import io.github.biezhi.wechat.utils.WeChatUtils;
import com.github.nnkwrik.doutuBot.model.Doutula;
import com.github.nnkwrik.doutuBot.model.EmoInfo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.github.nnkwrik.doutuBot.utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DoutulaAPI {

    private String baseUrl = "https://www.doutula.com/api/search?keyword=";
    private WechatEmoAPI emoAPI;



    public DoutulaAPI(WechatEmoAPI emoAPI) {
        this.emoAPI = emoAPI;
    }

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public String getEmoByKeyword(String keyword) {



        try {
            //获取图片的url
            String emoUrl = Utils.getPicFromDoutula(keyword);
            return emoAPI.downloadEmoUrl(emoUrl, WechatEmoAPI.DOUTULA_EMO_DIR);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
