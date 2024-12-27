package org.dao;


import org.pmw.tinylog.Logger;
import org.util.FileTools;
import org.util.TimeTools;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 *   @desc : 数据库操作
 *   @auth : tyf
 *   @date : 2024-12-20 11:22:47
*/
public class TinyDao {


    // 当前日志文件,按月生成
    private static String dbFile = null;
    private static Connection connection = null;
    private static Statement statement = null;



    // 数据库初始化。按月创建数据库
    public static void init(String userHomeData){
        TinyDao.dbFile = userHomeData + TimeTools.timeStempToTimeStr(System.currentTimeMillis(),0) +".db";
        TinyDao.createDataDbDirectory(userHomeData);
        TinyDao.dataDbInit();
    }

    private static void createDataDbDirectory(String directory){
        File d = new File(directory);
        if(!d.exists()){
            d.mkdirs();
        }
    }


    // 数据库初始化
    private static void dataDbInit(){

        String url = "jdbc:sqlite:"+dbFile;
        Logger.info("-----------------");
        Logger.info("数据库："+url);
        try {
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();
            // 判断是否有数据表
            checkAndInitTable();
        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(0);
        }

    }


    // 表、数据初始化
    public static void checkAndInitTable(){
        Logger.info("-----------------");
        Logger.info("表初始化：");
        // 获取资源目录下所有 SQL 文件
        Map<String,String> tables = FileTools.resourceDirectoryRead("table","sql");
        // 处理每个表的初始化
        if(tables.size()>=1){
            tables.entrySet().stream().forEach(entry->{
                // 表名称
                String table = entry.getKey();
                // 表结果和数据 sql 语句
                String[] sql = entry.getValue().split(";");
                // 验证表是否存在。不存在则执行所有 sql
                Logger.info(table);
//                Logger.info(entry.getValue());
            });
        }
    }


    // 释放资源
    public static void release(){
        try {
            if(connection!=null){
                connection.close();
                Logger.info("TinyDao release");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
