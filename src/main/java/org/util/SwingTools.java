package org.util;


import javax.swing.*;
import java.awt.*;

/**
 *   @desc : 屏幕工具
 *   @auth : tyf
 *   @date : 2024-12-25 10:57:26
*/
public class SwingTools {


    // 统一设置默认字体大小
    static {
        Font vFont = new Font("Dialog", Font.PLAIN, 13);
        UIManager.put("ToolTip.font", vFont);
        UIManager.put("Table.font", vFont);
        UIManager.put("TableHeader.font", vFont);
        UIManager.put("TextField.font", vFont);
        UIManager.put("ComboBox.font", vFont);
        UIManager.put("TextField.font", vFont);
        UIManager.put("PasswordField.font", vFont);
        UIManager.put("TextArea.font", vFont);
        UIManager.put("TextPane.font", vFont);
        UIManager.put("EditorPane.font", vFont);
        UIManager.put("FormattedTextField.font", vFont);
        UIManager.put("Button.font", vFont);
        UIManager.put("CheckBox.font", vFont);
        UIManager.put("RadioButton.font", vFont);
        UIManager.put("ToggleButton.font", vFont);
        UIManager.put("ProgressBar.font", vFont);
        UIManager.put("DesktopIcon.font", vFont);
        UIManager.put("TitledBorder.font", vFont);
        UIManager.put("Label.font", vFont);
        UIManager.put("List.font", vFont);
        UIManager.put("TabbedPane.font", vFont);
        UIManager.put("MenuBar.font", vFont);
        UIManager.put("Menu.font", vFont);
        UIManager.put("MenuItem.font", vFont);
        UIManager.put("PopupMenu.font", vFont);
        UIManager.put("CheckBoxMenuItem.font", vFont);
        UIManager.put("RadioButtonMenuItem.font", vFont);
        UIManager.put("Spinner.font", vFont);
        UIManager.put("Tree.font", vFont);
        UIManager.put("ToolBar.font", vFont);
        UIManager.put("OptionPane.messageFont", vFont);
        UIManager.put("OptionPane.buttonFont", vFont);
    }

    // 屏幕
    private static Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

    // 屏幕宽
    public static double screenWidth(){
        return screen.width;
    }

    // 屏幕高
    public static double screenHeight(){
        return screen.height;
    }



}
