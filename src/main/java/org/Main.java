package org;


import org.conf.Constants;
import org.dao.TinyDao;
import org.gui.MainFrame;
import org.http.TinyHttpServer;
import org.log.TinyLog;
import org.browser.JxEngine;
import org.wss.TinyWssServer;

/**
*   @desc : 系统主入口
*   @auth : tyf
*   @date : 2023-07-21  15:35:42
*/
public class Main {

    public static void main(String[] args) throws Exception{

        // log
        TinyLog.init(Constants.userHomeLog);

        // 初始化窗口
        MainFrame.init("软件示例");

        // 初始化数据库
        TinyDao.init(Constants.userHomeData);

        // 初始化http服务器
        TinyHttpServer.init(
                Constants.httpPort,  // http 端口
                Constants.httpApi1, // 后端访问地址
                Constants.httpApi2, // 前端访问地址
                Constants.distPath // 前端静态文件路径
        );

        // 初始化ws服务器
        TinyWssServer.init(Constants.wssPort);

        // 浏览器初始化
        JxEngine.init(Constants.debugPort);

        // 设置浏览器视图
        MainFrame.setBrowserView();

        // 设置窗口关闭时回调
        MainFrame.setCloseCallback(()->{
            TinyDao.release();
            TinyHttpServer.release();
            TinyWssServer.release();
            JxEngine.release();
            System.exit(0);
        });

    }



}