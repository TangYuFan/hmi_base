package org.browser;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.callback.ShowContextMenuCallback;
import com.teamdev.jxbrowser.browser.callback.input.MoveMouseWheelCallback;
import com.teamdev.jxbrowser.browser.callback.input.PressKeyCallback;
import com.teamdev.jxbrowser.browser.callback.input.ReleaseKeyCallback;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.ui.KeyCode;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import com.teamdev.jxbrowser.zoom.ZoomLevel;
import org.conf.Constants;
import org.gui.MainToolMenu;
import org.pmw.tinylog.Logger;
import org.util.IconsTools;
import org.util.SwingTools;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *   @desc : jxbrowser-7.19 版，对应 Chrome/92.0.4515.159 需要使用 jdk8
 *   @auth : tyf
 *   @date : 2024-12-23 13:59:28
 */
public class JxEngine {


    // 引擎
    private static Engine engine;
    private static Browser browser;
    private static BrowserView view;

    // 获取引擎、浏览器、浏览器视图
    public static Engine getEngine(){
        return engine;
    }
    public static Browser getBrowser(){
        return browser;
    }
    public static BrowserView getBrowserView(){
        return view;
    }

    // 初始化窗体
    public static void init(int port){

        // 密钥
        String key = "1BNDIEOFAZ1Z8R8VNNG4W07HLC9173JJW3RT0P2G9Y28L9YFFIWDBRFNFLFDQBKXAHO9ZE";

        // 浏览器
        engine = Engine.newInstance(
                EngineOptions.newBuilder(RenderingMode.HARDWARE_ACCELERATED).
                        licenseKey(key).
                        remoteDebuggingPort(port). // 开启远程调试
                        build());

        // 浏览器
        browser = engine.newBrowser();

        // 浏览器视图
        view = BrowserView.newInstance(browser);

        // 打印 userAgent
        userAgent(browser);

        // 首页（阻塞等待）
        loadIndex(browser);

        // 缓存数据
        cache(browser);

        // 设置右键菜单监听
        setMenu(browser);

        // 设置ctrl+滚轮缩放页面
        setScala(browser);

    }


    // 打印 userAgent
    public static void userAgent(Browser browser){
        // 提取浏览器的 User-Agent 里面有 chrom 内核版本
        browser.mainFrame().ifPresent(frame -> {
            String userAgent = frame.executeJavaScript("navigator.userAgent").toString();
            Logger.info("User Agent: " + userAgent);
        });
    }

    // 加载首页
    public static void loadIndex(Browser browser){
        // 加载网页
        String url = "http://localhost:"+ Constants.httpPort+ Constants.httpApi2+"index.html";
        browser.navigation().loadUrlAndWait(url, Duration.ofSeconds(5));
    }


    // 后退
    public static void goBack(Browser browser){
        if(browser.navigation().canGoBack()){
            browser.navigation().goBack();
        }
    }

    // 前进
    public static void goForward(Browser browser){
        if(browser.navigation().canGoForward()){
            browser.navigation().goForward();
        }
    }

    // 刷新
    public static void reload(Browser browser){
        browser.navigation().reload();
    }


    // 弹出调试窗口
    public static void debug(Browser browser){
        // 调试 url
        browser.devTools().remoteDebuggingUrl().ifPresent(debugUrl->{
            // 屏幕大小
            double screenHeight = SwingTools.screenHeight();
            double screenWidth = SwingTools.screenWidth();
            // 创建新的窗口加载调试 url
            Browser debugBrowser = JxEngine.getEngine().newBrowser();
            BrowserView debugView = BrowserView.newInstance(debugBrowser);
            JFrame debugFrame = new JFrame();
            debugFrame.setTitle("调试窗口");
            debugFrame.setIconImage(((ImageIcon) IconsTools.debuger).getImage());
            debugFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            debugFrame.add(debugView, BorderLayout.CENTER);
            debugBrowser.navigation().loadUrl(debugUrl);
            debugFrame.setVisible(true);
            debugFrame.setSize(new Dimension((int)(screenWidth/2),(int)(screenHeight/2)));
            debugFrame.setLocation((int)((screenWidth-debugFrame.getWidth())/2), (int)((screenHeight-debugFrame.getHeight())/2));
        });
    }


    // 缓存数据到 sessionStorge 或者 localStorge 中
    public static void cache(Browser browser){

        String api = "http://localhost:" + Constants.httpPort + Constants.httpApi1;
//        String dist = "http://localhost:" + Constants.httpPort + Constants.httpApi2;
        String ws = "ws://localhost:"+ Constants.wssPort+"/";

        // 本地前后台地址缓存
        browser.mainFrame().ifPresent(n->{
//            n.sessionStorage().putItem("dist",dist);
            n.sessionStorage().putItem("api",api);
            n.sessionStorage().putItem("ws",ws);
        });

    }

    // 设置右键菜单监听
    public static void setMenu(Browser browser){
        // 监听右键菜单 event，实现自定义右键菜单。实现前进、后退、刷新、调试窗口
        browser.set(ShowContextMenuCallback.class, new MainToolMenu(view));
    }


    // 设置ctrl+滚轮缩放页面
    public static void setScala(Browser browser){
        // 监听 ctrl按下 + 鼠标滚轮 实现页面缩放
        AtomicBoolean ctrl = new AtomicBoolean(false);
        browser.set(MoveMouseWheelCallback.class, params -> {
            if(ctrl.get()){
                // params.event().deltaY()>0 是滚轮向上
                // browser.zoom().level().value() 获取当前显示比例
                double set = params.event().deltaY()>0 ?
                        Math.min(2,browser.zoom().level().value()+0.05) : // 最大值不超过  200%
                        Math.max(0.5,browser.zoom().level().value()-0.05); // 最小值不小于 50%
                browser.zoom().level(ZoomLevel.of(set));
            }
            return MoveMouseWheelCallback.Response.proceed();
        });
        browser.set(PressKeyCallback.class, params -> {
            if(params.event().keyCode() == KeyCode.KEY_CODE_LCONTROL){
                ctrl.set(true);
            }
            return PressKeyCallback.Response.proceed();
        });
        browser.set(ReleaseKeyCallback.class, params -> {
            if(params.event().keyCode() == KeyCode.KEY_CODE_LCONTROL){
                ctrl.set(false);
            }
            return ReleaseKeyCallback.Response.proceed();
        });
    }

    // 释放资源
    public static void release(){
        try {
            if(engine!=null){
                engine.browsers().stream().forEach(n->{
                    n.close();
                });
                engine.close();
            }
        }
        catch (Exception e){}
        Logger.info("Browser release");
    }


}
