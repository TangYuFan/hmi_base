package org.http.api;


import com.alibaba.fastjson.JSONObject;


// 所有请求入口
public class HttpApiService {


    @HttpApiMethod(HttpApiEnum.query1)
    public static JSONObject query1(JSONObject param){

        JSONObject data = new JSONObject();
        data.put("success",0);
        data.put("msg","调用成功!");

        return data;
    }


}
