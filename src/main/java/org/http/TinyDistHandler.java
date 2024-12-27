package org.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.conf.Constants;
import org.pmw.tinylog.Logger;
import org.util.FileTools;

import java.io.*;
import java.net.URI;


/**
 *   @desc : 创建一个 http 服务器（前端 handler）
 *   @auth : tyf
 *   @date : 2024-12-20 10:53:25
 */
public class TinyDistHandler implements HttpHandler {

    String root;

    // dist 文件夹路径
    public TinyDistHandler(String root) {
        this.root = root;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        URI uri = exchange.getRequestURI();

        // 想要访问的文件路径
        String requestPath = uri.getPath();

        // 当前文件的 MIME type
        String mimeType = getMimeTypeByExtension(requestPath);
        Logger.info("Static Path："+requestPath+"，mimeType："+mimeType);

        // 访问 resources 下的文件，添加一个父路径
        byte[] content = FileTools.readBytes(Constants.distPath+requestPath);

        // 文件不存在返回 404
        if (content==null) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        // 返回静态文件内容
        try (OutputStream out = exchange.getResponseBody()) {
            exchange.getResponseHeaders().add("content-type", mimeType);
            exchange.sendResponseHeaders(200, content.length);
            out.write(content);
        }
    }


    // 根据文件扩展名获取 MIME 类型
    private String getMimeTypeByExtension(String file) {
        if (file.endsWith(".html")) {
            return "text/html";
        } else if (file.endsWith(".css")) {
            return "text/css";
        } else if (file.endsWith(".js")) {
            return "application/javascript";
        } else if (file.endsWith(".json")) {
            return "application/json";
        } else if (file.endsWith(".jpg") || file.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (file.endsWith(".png")) {
            return "image/png";
        } else if (file.endsWith(".gif")) {
            return "image/gif";
        } else if (file.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "application/octet-stream"; // 默认值
    }
}