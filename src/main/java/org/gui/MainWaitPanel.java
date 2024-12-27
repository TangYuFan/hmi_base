package org.gui;

import javax.swing.*;
import java.awt.*;

/**
 *   @desc : 系统初始化时的默认等待页面
 *   @auth : tyf
 *   @date : 2024-12-26 15:00:45
 */
public class MainWaitPanel extends JPanel {

    // 默认等待页面
    public static MainWaitPanel waitPanel = new MainWaitPanel();

    public MainWaitPanel() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 创建一个加载图标
        Icon loadingIcon = new ImageIcon(getClass().getResource("/gif/loading.gif"));
        JLabel loadingLabel = new JLabel(loadingIcon, SwingConstants.CENTER);
        add(loadingLabel, BorderLayout.CENTER);
    }
}
