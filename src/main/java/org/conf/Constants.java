package org.conf;


import org.util.PortTools;

/**
 *   @desc : 所有系统常量
 *   @auth : tyf
 *   @date : 2024-12-20 11:08:27
*/
public class Constants {


    public static String appName;// 应用名称
    public static String userHomeLog;// 日志路径
    public static String userHomeData;// 数据库路径
    public static String distPath;//dist 前端静态资源保存目录
    public static String httpApi1;//后端访问地址
    public static String httpApi2;// 前端访问地址
    public static int httpPort;// http 服务
    public static int wssPort;// wws 后台
    public static int debugPort;// debug 后台



    // 默认参数初始化
    static {

        appName = "HMI";
        userHomeLog = "C:\\ucchip\\"+appName+"\\log\\";
        userHomeData = "C:\\ucchip\\"+appName+"\\data\\";
        distPath = "dist";
        httpApi1 = "/api/";
        httpApi2 = "/";

        int ports[] = PortTools.getAvailablePorts(3);
        httpPort = ports[0];
        wssPort = ports[1];
        debugPort = ports[2];
    }



}
