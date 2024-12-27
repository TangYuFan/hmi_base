package org.wss.api;


import com.alibaba.fastjson.JSONObject;
import org.wss.TinyWssHandler;

// 所有 请求入口
public class WssApiService {

    @WssApiMethod(WssApiEnum.query1)
    public static void query1(JSONObject param){

        // 将接收的数据进行返回：
        TinyWssHandler.send(WssApiEnum.query1.name(),param);

    }


}
