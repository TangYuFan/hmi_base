package org.log;


import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.util.TimeTools;

import java.io.File;
import java.util.Arrays;

/**
*   @desc : 日志工具类
*   @auth : tyf
*   @date : 2023-09-21  11:33:50
*/
public class TinyLog {


    // 当前日志文件
    private static String log_file = null;


    /**
    *   @desc : 创建日志目录
    *   @auth : tyf
    *   @date : 2023-09-21  11:43:49
    */
    private static void createLogDirectory(String directory){
        File d = new File(directory);
        if(!d.exists()){
            d.mkdirs();
        }
    }

    /**
    *   @desc : 创建日志文件
    *   @auth : tyf
    *   @date : 2023-09-21  11:34:16
    */
    private static void createLog(){
        File log = new File(log_file);
        if(log.exists()){
            log.delete();
        }
        // 创建文件
        try {
            log.createNewFile();
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("日志文件初始化失败");
            System.exit(0);
        }

        // 自动将 logging 输出到日志文件
        try {
            Configurator
                    // 设置为最低级别
                    .currentConfig().level(Level.TRACE)
                    .formatPattern("{date:yyyy-MM-dd HH:mm:ss} {level} [{thread}] {message}")
                    // writer 子类有文件、滚动文件、jdbc、等等
                    .writer(new org.pmw.tinylog.writers.FileWriter(log_file))
                    .addWriter(new ConsoleWriter(System.out))
                    .activate();
            Logger.info("日志初始化："+log_file);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
    *   @desc : 默认清除三天以外的日志文件
    *   @auth : tyf
    *   @date : 2023-09-22  14:03:00
    */
    private static void clearLog(String directory){

        // 仅保留3天
        long old = 5;
        long limit = System.currentTimeMillis() - old * 24 * 60 * 60 * 1000;

        try {
            File dir = new File(directory);
            if(dir.exists()&&dir.isDirectory()){
                File[] files = dir.listFiles();
                Arrays.stream(files).forEach(n->{
                    // 转为时间戳
                    String name = n.getName().replace(".log","");
                    long t = TimeTools.timeStrToTimeStemp(name,11);
                    // old 之前的日志文件进行清除
                    if(t<=limit){
                        n.delete();
                    }
                });
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    public static void init(String user_home_log){
        // 初始化日志文件,清除3天以外的日志文件
        TinyLog.log_file = user_home_log + TimeTools.timeStempToTimeStr(System.currentTimeMillis(),11) +".log";
        TinyLog.createLogDirectory(user_home_log);
        TinyLog.clearLog(user_home_log);
        TinyLog.createLog();
    }

}
