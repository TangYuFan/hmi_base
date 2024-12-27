package org.http;


import com.sun.net.httpserver.HttpServer;
import org.conf.Constants;
import org.pmw.tinylog.Logger;
import org.util.ExecutorTools;
import org.util.FileTools;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

/**
 *   @desc : http 后台
 *   @auth : tyf
 *   @date : 2024-12-20 11:09:34
*/
public class TinyHttpServer {

    private static HttpServer server;


    // 初始化 http server
    public static void init(int port, String api1,String api2,String dist) throws Exception{

        server = HttpServer.create(new InetSocketAddress(port), 0);

        // 自定义后端请求处理器（后端）
        server.createContext(api1, new TinyHttpHandler());

        // 自定义前端静态资源服务器（前端）
        server.createContext(api2, new TinyDistHandler(dist));

        server.setExecutor(ExecutorTools.httpExecutor);
        server.start();
        Logger.info("TinyHttpServer start..");
        Logger.info("后端：http://localhost:" + port + api1);
        Logger.info("前端：http://localhost:" + port + api2);
    }


    // 释放资源
    public static void release(){
        // http server 随 jvm 一起推出
        try {
            if(server!=null){
                server.stop(0);
            }
            Logger.info("TinyHttpServer release");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
