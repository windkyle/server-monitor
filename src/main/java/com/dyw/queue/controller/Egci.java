package com.dyw.queue.controller;

import com.dyw.queue.HCNetSDK;
import com.dyw.queue.entity.ConfigEntity;
import com.dyw.queue.form.SystemStatusForm;
import com.dyw.queue.handler.AlarmHandler;
import com.dyw.queue.service.*;
import com.dyw.queue.timer.*;
import com.dyw.queue.tool.Tool;
import com.sun.jna.NativeLong;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.sql.Statement;
import java.util.*;

public class Egci {
    public static SqlSession session;
    //配置文件
    public static ConfigEntity configEntity;
    //一体机变量
    public static short devicePort;
    public static String deviceName;
    public static String devicePass;
    //全局变量
    private static Logger Elogger;
    public static List<String> deviceIps0;//所有设备，不带“#”
    public static List<String> deviceIps1;//一核设备，不带“#”
    public static List<String> deviceIps2;//二核设备，不带“#”
    public static List<String> deviceIps3;//三核设备，不带“#”
    public static List<String> deviceIps0WithOctothorpe;//所有设备，带“#”
    public static List<String> deviceIps1WithOctothorpe;//一核设备，带“#”
    public static List<String> deviceIps2WithOctothorpe;//二核设备，带“#”
    public static List<String> deviceIps3WithOctothorpe;//三核设备，带“#”
    public static Set<String> deviceIpsOn;//在线设备
    public static List<String> deviceIpsOff;//离线设备
    public static Set<String> deviceIpsAlarmFail;//布防失败的设备
    public static Map<String, String> deviceIps0Map;//所有设备的信息，包含设备名称
    //队列的ip
    public static String queueIp;
    //初始化生产者数组
    public static List<ProducerService> producerServiceList;
    //初始化静态对象
    public static HCNetSDK hcNetSDK;
    //报警回调函数
    private static HCNetSDK.FMSGCallBack_V31 alarmHandler;
    //监控推送服务的生产者合集
    public static List<ProducerService> producerMonitorOneServices;//监听一核设备
    public static List<ProducerService> producerMonitorTwoServices;//监听二核设备
    public static List<ProducerService> producerMonitorThreeServices;//监听三核设备
    //采集设备的生产者合集
//    public static List<ProducerService> producerFaceCollectionServices;
    //采集设备IP合集，用来判断返回的消息来自采集设备还是一体机
    public static Set<String> deviceIpsFaceCollection;
    //采集设备和对应的生产者的map
    public static Map<String, ProducerService> faceCollectionIpWithProducer;
    //采集设备和对应的登陆信息的map
    public static Map<String, LoginService> faceCollectionIpWithLogin;
    //推送服务的生产者对象数组，用来解决异常推送问题
    public static Map<String, ProducerService> producerServiceMap;

    /*
     * 初始化函数
     * */
    private static void initServer() {
        SystemStatusForm systemStatusForm = new SystemStatusForm();
        systemStatusForm.init();
        systemStatusForm.setSystemStatusLabel("监控系统正在启动中，请稍等...");

        System.out.println(System.getProperty("user.dir"));
        System.out.println("进程id：" + Tool.getProcessID());
        //初始化日志对象
        Elogger = LoggerFactory.getLogger(Egci.class);
        //初始化session对象
        try {
            SessionService sessionService = new SessionService();
            session = sessionService.createSession();
        } catch (Exception e) {
            Elogger.error("创建session对象失败", e);
        }
        //初始化SDK静态对象
        try {
            hcNetSDK = HCNetSDK.INSTANCE;
        } catch (Exception e) {
            Elogger.error("初始化SDK静态对象，失败", e);
        }
        //初始化SDK
        if (!hcNetSDK.NET_DVR_Init()) {
            Elogger.error("SDK初始化失败");
            return;
        }
        //读取配置文件
        configEntity = Tool.getConfig(System.getProperty("user.dir") + "/config/config.xml");
        //一体机参数配置
        devicePort = configEntity.getDevicePort();
        deviceName = configEntity.getDeviceName();
        devicePass = configEntity.getDevicePass();
        //将pid写入数据库
        session.update("mapping.processMapper.setProcessId", Tool.getProcessID());
        session.commit();
        //初始化设备信息
        EquipmentService.initEquipmentInfo();
        //获取一体机设备网络状态,并设置定时状态更新
        deviceIpsOn = new HashSet<String>();
        deviceIpsOff = new ArrayList<String>();
        for (String ip : deviceIps0) {
            try {
                NetStateService netStateService = new NetStateService();
                if (netStateService.ping(ip)) {
                    deviceIpsOn.add(ip);
                } else {
                    deviceIpsOff.add(ip);
                }
                PingTimer pingTimer = new PingTimer(ip);
                pingTimer.start();
            } catch (Exception e) {
                Elogger.error("获取在线/离线设备出错", e);
            }
        }
        //初始化监听生产者
        producerMonitorOneServices = new ArrayList<ProducerService>();
        producerMonitorTwoServices = new ArrayList<ProducerService>();
        producerMonitorThreeServices = new ArrayList<ProducerService>();
        //设置报警回调函数
        alarmHandler = new AlarmHandler();
        if (!HCNetSDK.INSTANCE.NET_DVR_SetDVRMessageCallBack_V31(alarmHandler, null)) {
            Elogger.info("设置回调函数失败，错误码：" + hcNetSDK.NET_DVR_GetLastError());
        }
        //对所有一体机设备进行布防
        EquipmentService.initEquipmentAlarm();
        //开启自动布防重连定时任务
        AlarmTimer.open();
        //用来处理通行信息推送的问题
        producerServiceMap = new HashMap<String, ProducerService>();
        //启动同步操作:0表示不启用；1表示单台；2表示全部
        if (!configEntity.getSynchronization().equals("0")) {
            SynchronizationTimer.open();
            Elogger.info("开启自动同步功能");
        } else {
            Elogger.info("关闭自动同步功能");
        }
        //启用程序定时关闭功能
        if (configEntity.getExitTimeStatus1() == 1) {
            ProcessTimer.exit1();
            Elogger.info("启用程序定时关闭");
        }
        if (configEntity.getExitTimeStatus2() == 1) {
            ProcessTimer.exit2();
            Elogger.info("启用程序定时关闭2");
        }
        //获取系统默认编码
        Elogger.info("系统默认编码：" + System.getProperty("file.encoding")); //查询结果GBK
        //系统默认字符编码
        Elogger.info("系统默认字符编码：" + Charset.defaultCharset()); //查询结果GBK
        //操作系统用户使用的语言
        Elogger.info("系统默认语言：" + System.getProperty("user.language")); //查询结果zh
        //启用socket服务
        try {
            Elogger.info("本机IP地址" + InetAddress.getLocalHost());
            ServerSocket serverSocket = new ServerSocket(configEntity.getSocketMonitorPort());
            serverSocket.setSoTimeout(0);
            serverSocket.setReuseAddress(true);
            systemStatusForm.setSystemStatusLabel("监控系统正在运行，请勿关闭该窗口!");
            Elogger.info("等待客户端连接..............................................................................");
            while (true) {
                Socket socket = serverSocket.accept();
                socket.setReuseAddress(true);
                SocketService socketService = new SocketService(socket);
                socketService.start();
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            Elogger.error("开启socket服务失败：", e);
        }
    }

    public static void main(String[] args) {
        try {
            Egci.initServer();
        } catch (Exception e) {
            Elogger.error("错误：", e);
        } finally {
            Elogger.error("人脸通行服务程序出现严重错误,需要被关闭");
            System.exit(0);//出现任何错误，关闭程序
        }
    }
}
