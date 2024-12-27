package org.gui;


import com.teamdev.jxbrowser.view.swing.BrowserView;
import org.browser.JxEngine;
import org.util.IconsTools;
import org.util.SwingTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *   @desc : 主 jframe
 *   @auth : tyf
 *   @date : 2024-12-25 13:57:19
*/
public class MainFrame extends JFrame {


    // 输入窗体名称、窗体关闭时的回调
    public MainFrame(String name){
        // 屏幕大小
        double screenHeight = SwingTools.screenHeight();
        double screenWidth = SwingTools.screenWidth();
        this.setTitle(name);
        this.setIconImage(((ImageIcon) IconsTools.app).getImage());
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(new Dimension((int)(screenWidth/1.5),(int)(screenHeight/1.5)));
        this.setLocation((int)((screenWidth-this.getWidth())/2), (int)((screenHeight-this.getHeight())/2));
        // 等待页面
        this.add(MainWaitPanel.waitPanel, BorderLayout.CENTER);
        // 显示
        this.setVisible(true);
    }

    // 初始化
    private static MainFrame frame;
    public static void init(String name){
        if(frame==null){
            frame = new MainFrame(name);
        }
    }

    // 设置浏览器视图
    public static void setBrowserView(){
        if(frame!=null){
            // 等待页面替换为浏览器视图并重新渲染
            BrowserView view = JxEngine.getBrowserView();
//            frame.remove(MainWaitPanel.waitPanel);
            frame.add(view, BorderLayout.CENTER);
            frame.revalidate();
            frame.repaint();
        }
    }

    // 设置窗口关闭回调
    public static void setCloseCallback(Runnable close){
        if(frame!=null){
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    close.run();
                }
            });
        }
    }




}
