package org.util;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *   @desc : 所有线程池工具
 *   @auth : tyf
 *   @date : 2024-12-20 11:40:55
*/
public class ExecutorTools {


    // http 业务处理
    public static final ExecutorService httpExecutor = Executors.newFixedThreadPool(5);


    // wss 接收、发送、业务处理线程
    public static final ExecutorService wssRecvRunner = Executors.newSingleThreadExecutor();
    public static final ExecutorService wssSendRunner = Executors.newSingleThreadExecutor();
    public static final ExecutorService wssExecutor = Executors.newFixedThreadPool(5);



}
