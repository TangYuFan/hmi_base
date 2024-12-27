package org.gui;

import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.browser.callback.ShowContextMenuCallback;
import com.teamdev.jxbrowser.browser.internal.BrowserImpl;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.internal.rpc.stream.Interceptor;
import com.teamdev.jxbrowser.ui.Point;
import com.teamdev.jxbrowser.ui.event.internal.rpc.MoveMouseWheel;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import org.browser.JxEngine;
import org.conf.Constants;
import org.pmw.tinylog.Logger;
import org.util.IconsTools;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.io.File;

import static javax.swing.SwingUtilities.invokeLater;

/**
 *   @desc : 自定义右键弹出窗工具栏
 *   @auth : tyf
 *   @date : 2024-12-25 14:53:46
*/
public class MainToolMenu implements ShowContextMenuCallback{

    // 浏览器试图
    private final BrowserView parent;
    public MainToolMenu(BrowserView parent) {
        this.parent = parent;
    }

    @Override
    public void on(ShowContextMenuCallback.Params params, ShowContextMenuCallback.Action action) {
        new SwingContextMenu(parent, params, action) {
            @Override
            protected void initialize(JPopupMenu contextMenu) {

                Browser browser = parent.getBrowser();

                // 首页
                JMenuItem homeMenuItem = new JMenuItem("首页");
                homeMenuItem.addActionListener(e->{
                    JxEngine.loadIndex(browser);
                });


                // 后退
                JMenuItem goBackMenuItem = new JMenuItem("后退");
                goBackMenuItem.addActionListener(e->{
                    JxEngine.goBack(browser);
                });

                // 前进
                JMenuItem goForwardMenuItem = new JMenuItem("前进");
                goForwardMenuItem.addActionListener(e->{
                    JxEngine.goForward(browser);
                });

                // 刷新
                JMenuItem reloadMenuItem = new JMenuItem("重新加载");
                reloadMenuItem.addActionListener(e->{
                    JxEngine.reload(browser);
                });

                // 弹出调试窗
                JMenuItem debugMenuItem = new JMenuItem("调试窗口");
                debugMenuItem.addActionListener(e->{
                    JxEngine.debug(browser);
                });

                // 打开日志
                JMenuItem logMenuItem = new JMenuItem("日志");
                logMenuItem.addActionListener(e->{
                    // 创建 File 对象
                    File directory = new File(Constants.userHomeLog);
                    // 检查是否支持 Desktop 类
                    if (Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        try {
                            // 打开文件目录
                            desktop.open(directory);
                        } catch (Exception ee) {
                            Logger.info("无法打开文件目录: " + ee.getMessage());
                        }
                    } else {
                        Logger.info("当前平台不支持 Desktop 类");
                    }
                });


                homeMenuItem.setIcon(IconsTools.home);
                reloadMenuItem.setIcon(IconsTools.reload);
                debugMenuItem.setIcon(IconsTools.debuger);

                contextMenu.add(homeMenuItem);
                contextMenu.add(goForwardMenuItem);
                contextMenu.add(goBackMenuItem);
                contextMenu.add(reloadMenuItem);
                contextMenu.addSeparator();
                contextMenu.add(debugMenuItem);
                contextMenu.add(logMenuItem);
            }
        }.show();
    }

    private static abstract class SwingContextMenu {
        private final BrowserView parent;
        private final BrowserImpl browser;
        protected final ShowContextMenuCallback.Params params;
        protected final ShowContextMenuCallback.Action callback;

        SwingContextMenu(BrowserView parent, ShowContextMenuCallback.Params params, ShowContextMenuCallback.Action callback) {
            this.parent = parent;
            this.browser = (BrowserImpl) parent.getBrowser();
            this.params = params;
            this.callback = callback;
        }

        protected abstract void initialize(JPopupMenu contextMenu);

        public final void show() {
            JPopupMenu popupMenu = new JPopupMenu();
            popupMenu.setLightWeightPopupEnabled(false);
            popupMenu.addPopupMenuListener(new SwingContextMenu.PopupMenuListenerImpl());
            initialize(popupMenu);
            registerMouseCallbackInterceptors(popupMenu);
            // 鼠标点击的位置相比父组件的位置
            Point location = params.location();
            int x = location.x();
            int y = location.y();
            // browserView 的 parent是 jframe
//            invokeLater(() -> popupMenu.show(parent, x,y));
            invokeLater(() -> popupMenu.show(parent.getParent(), x,y));
        }
        private void notifyContextMenuClosed() {
            unregisterMouseCallbackInterceptors();
            if (!callback.isClosed()) {
                callback.close();
            }

        }
        private void registerMouseCallbackInterceptors(JPopupMenu popupMenu) {
            if (isHardwareAccelerated()) {
                browser.setCallbackInterceptor(MoveMouseWheel.Request.class, (params) -> {
                    hide(popupMenu);
                    return Interceptor.Action.PROCEED;
                });
            }

        }

        private void unregisterMouseCallbackInterceptors() {
            if (isHardwareAccelerated()) {
                browser.setCallbackInterceptor(MoveMouseWheel.Request.class,
                        (params) -> Interceptor.Action.PROCEED);
            }

        }

        private void hide(JPopupMenu popupMenu) {
            invokeLater(() -> {
                if (popupMenu.isShowing()) {
                    popupMenu.setVisible(false);
                }
            });
        }

        private boolean isHardwareAccelerated() {
            return browser.engine().options().renderingMode()
                    == RenderingMode.HARDWARE_ACCELERATED;
        }

        private class PopupMenuListenerImpl implements PopupMenuListener {

            private PopupMenuListenerImpl() {
            }

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                invokeLater(SwingContextMenu.this::notifyContextMenuClosed);
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        }
    }


}
