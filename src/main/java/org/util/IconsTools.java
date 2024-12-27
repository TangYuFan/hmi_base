package org.util;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;

/**
 *   @desc : icons 工具类
 *   @auth : tyf
 *   @date : 2024-12-24 14:44:08
*/
public class IconsTools {

    // 默认的图标
    public static Icon app = getIcon("/svg/app.svg");
    public static Icon debuger = getIcon("/svg/debuger.svg",15,15);
    public static Icon home = getIcon("/svg/home.svg",15,15);
    public static Icon reload = getIcon("/svg/reload.svg",15,15);

    public static Icon getIcon(String resourceName, int width, int height) {
        if (resourceName.endsWith(".svg")) {
            FlatSVGIcon c = new FlatSVGIcon(resourceName.substring(1), width, height);
            return c;
        }
        else {
            return new ImageIcon(IconsTools.class.getResource(resourceName));
        }
    }

    public static Icon getIcon(String resourceName) {
        return getIcon(resourceName, 36, 36);
    }

}
