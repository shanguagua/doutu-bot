package com.github.nnkwrik.doutuBot.emotion;

import com.github.nnkwrik.doutuBot.utils.Utils;

public class DoutulaAPI {

    private WechatEmoAPI emoAPI;

    public DoutulaAPI(WechatEmoAPI emoAPI) {
        this.emoAPI = emoAPI;
    }

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
