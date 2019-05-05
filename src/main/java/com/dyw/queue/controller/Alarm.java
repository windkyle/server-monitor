package com.dyw.queue.controller;

import com.dyw.queue.HCNetSDK;
import com.dyw.queue.service.LoginService;
import com.sun.jna.NativeLong;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Alarm {
    public static void main(String[] args) {
        Alarm alarm = new Alarm();
        System.out.println(alarm.getClass().getResource("../../"));
        System.out.println(alarm.getClass().getResource("/"));
        test();
        if (1 > 0) {
            return;
        }
        System.out.println(System.getProperty("user.dir") + "/target/egci");
        System.out.println(alarm.getClass().getClassLoader().getResource(""));
    }

    public static void test() {
        System.out.println(Alarm.class.getClassLoader().getResource("/"));//null
        return;
    }
}
