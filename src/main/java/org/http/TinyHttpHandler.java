package org.http;


import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.conf.Constants;
import org.http.api.HttpApiMethod;
import org.http.api.HttpApiService;
import org.pmw.tinylog.Logger;
import org.util.ReflectionTools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.*;

/**
 *   @desc : 创建一个 http 服务器（后端 handler）
 *   @auth : tyf
 *   @date : 2024-12-20 10:53:25
*/
public class TinyHttpHandler implements HttpHandler {


    // 函数缓冲
    public static Map<String, Method> apiMethod = new HashMap();

    // 错误返回
    public static String errorMsg = "UNKNOW ERROR";

    // 缓存 ApiService 中所有接口
    public TinyHttpHandler(){
        Logger.info("-----------------");
        Logger.info("后端http接口初始化：");
        Arrays.stream(HttpApiService.class.getDeclaredMethods()).filter(n->n.isAnnotationPresent(HttpApiMethod.class)).forEach(n->{
            Logger.info("Http Api Cache："+n.getAnnotation(HttpApiMethod.class).value().name());
            n.setAccessible(true);
            apiMethod.put(n.getAnnotation(HttpApiMethod.class).value().name(),n);
        });
        Logger.info("-----------------");
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // 获取请求路径
        String requestURI = exchange.getRequestURI().toString();
        String api = requestURI.replace(Constants.httpApi1,"");
        String params = getRequestBody(exchange);

        // 只处理 post
        if(!checkOnlyPost(exchange)){
            Logger.info("仅仅支持 POST 请求："+api);
            return;
        }

        // 只处理 json 类型的 content-type
        if(!checkContentTypeOnlyJson(exchange)||!checkJsonParams(exchange,params)){
            Logger.info("仅仅支持 JSON 请求："+api);
            return;
        }

        // 不存在的接口
        if(!checkSupportApi(exchange,api)){
            Logger.info("不存在的接口："+api);
            return;
        }

        Logger.info("Http Api："+requestURI);
        Logger.info("Http Api："+api+"，Request："+params);

        // 调用业务接口
        try {
            JSONObject data = JSONObject.parseObject(params);
            Object result = ReflectionTools.invokeStaticMethodByMethod(apiMethod.get(api),new Object[]{data});
            response(exchange,api,result);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    // 只处理 post
    private boolean checkOnlyPost(HttpExchange exchange) throws IOException{
        boolean rt = "POST".equals(exchange.getRequestMethod());
        if(!rt){
            exchange.sendResponseHeaders(404, errorMsg.getBytes("UTF-8").length);
            OutputStream os = exchange.getResponseBody();
            os.write(errorMsg.getBytes("UTF-8"));
            os.close();
        }
        return rt;
    }

    // 只处理 content-type 为 json 的请求
    private boolean checkContentTypeOnlyJson(HttpExchange exchange) throws IOException {
        // 获取所有的请求头
        List<String> contentTypes = new ArrayList<>();
        List<String> c1 = exchange.getRequestHeaders().get("Content-Type");
        List<String> c2 = exchange.getRequestHeaders().get("content-type");
        if(c1!=null){
            contentTypes.addAll(c1);
        }
        if(c2!=null){
            contentTypes.addAll(c2);
        }
        // 如果没有 Content-Type 字段，或者它不是 application/json，则返回错误
        if (contentTypes == null || contentTypes.isEmpty()) {
            return false;
        }
        // 遍历 Content-Type，检查是否包含 "application/json"
        for (String contentType : contentTypes) {
            // 不区分大小写，检查 contentType 是否包含 "application/json"
            if (contentType.toLowerCase().contains("application/json")) {
                return true;
            }
        }
        // 如果没有匹配的 Content-Type
        exchange.sendResponseHeaders(404, errorMsg.getBytes("UTF-8").length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(errorMsg.getBytes("UTF-8"));
        }
        return false;
    }



    // 只处理有注解标注的处理接口
    private boolean checkSupportApi(HttpExchange exchange,String api) throws IOException{
        boolean rt = false;
        if(apiMethod.keySet().contains(api)){
            rt = true;
        }
        if(!rt){
            exchange.sendResponseHeaders(404, errorMsg.getBytes("UTF-8").length);
            OutputStream os = exchange.getResponseBody();
            os.write(errorMsg.getBytes("UTF-8"));
            os.close();
        }
        return rt;
    }

    // 从 HttpExchange 获取请求体
    private String getRequestBody(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        StringBuilder requestBody = new StringBuilder();
        int byteRead;
        while ((byteRead = inputStream.read()) != -1) {
            requestBody.append((char) byteRead);
        }
        return requestBody.toString();
    }


    // 校验是否为 json 参数
    private boolean checkJsonParams(HttpExchange exchange,String params) throws IOException{
        boolean rt = false;
        try {
            if(params!=null&&!"".equals(params)){
                JSONObject.parse(params);
                rt = true;
            }
        }
        catch (Exception e){}
        if(!rt){
            exchange.sendResponseHeaders(404, errorMsg.getBytes("UTF-8").length);
            OutputStream os = exchange.getResponseBody();
            os.write(errorMsg.getBytes("UTF-8"));
            os.close();
        }
        return rt;
    }



    // 回复
    private void response(HttpExchange exchange,String api,Object result) throws IOException{
        Logger.info("Api："+api+"，Response："+result);
        if(result==null){
            exchange.sendResponseHeaders(404, errorMsg.getBytes("UTF-8").length);
            OutputStream os = exchange.getResponseBody();
            os.write(errorMsg.getBytes("UTF-8"));
            os.close();
        }else{
            String rt = result.toString();
            exchange.sendResponseHeaders(200, rt.getBytes("UTF-8").length);
            OutputStream os = exchange.getResponseBody();
            os.write(rt.getBytes("UTF-8"));
            os.close();
        }
    }

}
