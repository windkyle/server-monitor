package com.dyw.queue.form;

import javax.swing.*;
import java.awt.*;

public class SystemStatusForm {
    public JPanel systemStatus;
    public JLabel systemStatusLabel;

    public void init() {
        JFrame frame = new JFrame("SystemStatusForm");
        frame.setContentPane(this.systemStatus);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    /*
     * 设置提示信息
     * */
    public void setSystemStatusLabel(String info) {
        systemStatusLabel.setText(info);
    }
}
