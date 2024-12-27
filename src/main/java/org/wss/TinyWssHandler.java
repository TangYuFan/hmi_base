package org.wss;


import com.alibaba.fastjson.JSONObject;
import org.java_websocket.WebSocket;
import org.pmw.tinylog.Logger;
import org.util.ExecutorTools;
import org.util.ReflectionTools;
import org.wss.api.WssApiMethod;
import org.wss.api.WssApiService;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// 客户端链接 + 请求 + 回复处理
public class TinyWssHandler {



    // 函数缓冲
    public static Map<String, Method> apiMethod = new HashMap();

    public static void init(){
        Logger.info("-----------------");
        Logger.info("后端wss接口初始化：");
        Arrays.stream(WssApiService.class.getDeclaredMethods()).filter(n->n.isAnnotationPresent(WssApiMethod.class)).forEach(n->{
            Logger.info("Wss Api Cache："+n.getAnnotation(WssApiMethod.class).value().name());
            n.setAccessible(true);
            apiMethod.put(n.getAnnotation(WssApiMethod.class).value().name(),n);
        });
        Logger.info("-----------------");
    }

    // 只保留一个链接
    public static WebSocket onlyOneConn;

    // 上行数据
    public static void recv(String message){
        ExecutorTools.wssRecvRunner.submit(()->{
            // 反射调用
            if(onlyOneConn==null){
                Logger.info("wss 链接已断开..");
                return;
            }
            try {
                Logger.info("Wss Recv："+message);
                JSONObject params = JSONObject.parseObject(message);
                // api 和 data
                String api = params.getString("api");
                JSONObject data = params.getJSONObject("data");
                if(api==null||data==null){
                    Logger.info("wss 缺少 api 或 data");
                    return;
                }
                if(!apiMethod.containsKey(api)){
                    Logger.info("wss 不支持的api："+api);
                    return;
                }
                // 调用业务处理线程处理业务
                ExecutorTools.wssExecutor.submit(()->{
                    try {
                        ReflectionTools.invokeStaticMethodByMethod(apiMethod.get(api),new Object[]{data});
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        Logger.info("wss 调用错误："+e.getMessage()+"，"+e.getCause());
                    }
                });
            }
            catch (Exception e){
                Logger.info("wss 参数解析错误："+e.getMessage()+"，"+e.getCause());
            }
        });
    }

    // 下行数据
    public static void send(String api, JSONObject data){
        if(onlyOneConn==null){
            Logger.info("wss 链接已断开..");
            return;
        }
        ExecutorTools.wssSendRunner.submit(()->{
            JSONObject params = new JSONObject(true);
            params.put("api",api);
            params.put("data",data);
            Logger.info("Wss Send："+params);
            onlyOneConn.send(data.toString());
        });
    }


}
