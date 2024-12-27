package org.wss;


import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.pmw.tinylog.Logger;
import org.util.ExecutorTools;

import java.net.InetSocketAddress;

/**
 *   @desc : ws 服务端
 *   @auth : tyf
 *   @date : 2024-12-20 15:15:24
*/
public class TinyWssServer {

    private static WebSocketServer webSocketServer;

    public static void init(int port){

        // 初始化 ws api
        TinyWssHandler.init();

        // 启动 WebSocket Server
        WebSocketServer webSocketServer = new WebSocketServer(new InetSocketAddress(port)) {

            // 服务启动成功
            @Override
            public void onStart() {
                ExecutorTools.wssRecvRunner.submit(()->{
                    Logger.info("TinyWssServer start..");
                    Logger.info("ws://localhost:"+ port+"/");
                });
            }

            // 创建链接
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                ExecutorTools.wssRecvRunner.submit(()->{
                    Logger.info("Ws Connection ..");
                    TinyWssHandler.onlyOneConn = conn;
                });
            }

            // 断开链接
            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                ExecutorTools.wssRecvRunner.submit(()->{
                    Logger.info("Ws Disconnection ..");
                    TinyWssHandler.onlyOneConn = null;
                });
            }

            // 链接错误
            @Override
            public void onError(WebSocket conn, Exception ex) {
                ExecutorTools.wssRecvRunner.submit(()->{
                    Logger.info("Ws Connection Error: "+conn.getRemoteSocketAddress()+"，ex："+ex.getMessage()+"，"+ex.getCause());
                    TinyWssHandler.onlyOneConn = null;
                    conn.close();
                });
            }

            // 处理上报的数据
            @Override
            public void onMessage(WebSocket conn, String message) {
                TinyWssHandler.recv(message);
            }


        };

        // 启动 WebSocket 服务
        webSocketServer.start();


    }

    // 释放资源
    public static void release(){
        // wws server 随 jvm 一起推出
        try {
            if(webSocketServer!=null){
                webSocketServer.stop();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Logger.info("TinyWssServer release");
    }

}
