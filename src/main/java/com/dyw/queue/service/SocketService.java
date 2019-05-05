package com.dyw.queue.service;

import com.alibaba.fastjson.JSON;
import com.dyw.queue.controller.Egci;
import com.dyw.queue.entity.StaffEntity;
import com.dyw.queue.entity.StatusEntity;
import com.dyw.queue.entity.TemporaryStaffEntity;
import net.iharder.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketService extends Thread {
    private Logger logger = LoggerFactory.getLogger(SocketService.class);
    private Socket socketInfo;
    private ModeService modeService;
    private StatusService statusService;

    public SocketService(Socket socket) {
        //初始化设备状态
        statusService = new StatusService();
        //更改设备模式
        modeService = new ModeService();
        this.socketInfo = socket;
    }

    /*
     * 数据处理
     * */
    @Override
    public void run() {
        //读取客户端发送来的信息
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(socketInfo.getInputStream()));
        } catch (IOException e) {
            logger.error("获取客户端消息失败：", e);
        }
        try {
            String mess = br.readLine();
            logger.info("客户端发来的消息：" + mess);
            String staffInfo = "";//结构体信息
            String operationCode = mess.substring(0, 1);
            switch (Integer.parseInt(operationCode)) {
                case 3://获取设备状态
                    List<StatusEntity> deviceStatus = new ArrayList<StatusEntity>();
                    if (mess.substring(2).equals("0")) {
                        deviceStatus = getStatus(Egci.deviceIps0WithOctothorpe);
                    } else if (mess.substring(2).equals("1")) {
                        deviceStatus = getStatus(Egci.deviceIps1WithOctothorpe);
                    } else if (mess.substring(2).equals("2")) {
                        deviceStatus = getStatus(Egci.deviceIps2WithOctothorpe);
                    } else if (mess.substring(2).equals("3")) {
                        deviceStatus = getStatus(Egci.deviceIps3WithOctothorpe);
                    }
                    //返回消息给客户端
                    sendToClient(socketInfo, br, JSON.toJSONString(deviceStatus));
                    break;
                case 4://设置一体机的通行模式
                    LoginService loginService = new LoginService();
                    String[] info = mess.split("#");
                    loginService.login(info[1], Egci.devicePort, Egci.deviceName, Egci.devicePass);
                    //卡+人脸
                    if (info[2].equals("0")) {
                        modeService.changeMode(loginService.getlUserID(), (byte) 13);
                        //返回消息给客户端
                        sendToClient(socketInfo, br, "success");
                    }
                    //人脸
                    if (info[2].equals("1")) {
                        modeService.changeMode(loginService.getlUserID(), (byte) 14);
                        //返回消息给客户端
                        sendToClient(socketInfo, br, "success");
                    }
                    loginService.logout();
                    break;
                case 5://设置切换器模式:0是关闭人脸识别，1是开启人脸识别
                    sendToClient(socketInfo, br, "error");
                    break;
                case 8://实时监控消息推送
                    String[] info8 = mess.split("#");
                    //判断客户端IP地址是否布防过：如果有，则先清除布防信息，防止多次推送消息
                    if (Egci.producerServiceMap.get(socketInfo.getInetAddress().getHostAddress()) != null) {
                        Egci.producerMonitorOneServices.remove(Egci.producerServiceMap.get(socketInfo.getInetAddress().getHostAddress()));
                        Egci.producerMonitorTwoServices.remove(Egci.producerServiceMap.get(socketInfo.getInetAddress().getHostAddress()));
                        Egci.producerMonitorThreeServices.remove(Egci.producerServiceMap.get(socketInfo.getInetAddress().getHostAddress()));
                        Egci.producerServiceMap.get(socketInfo.getInetAddress().getHostAddress()).deleteQueue();
                        Thread.sleep(3000);
                    }
                    ProducerService producerService8 = new ProducerService("push:" + socketInfo.getInetAddress().getHostAddress(), Egci.configEntity.getQueueIp());
                    CustomerMonitorService customerMonitorService8 = new CustomerMonitorService("push:" + socketInfo.getInetAddress().getHostAddress(), producerService8.getChannel(), socketInfo);
                    customerMonitorService8.start();
                    if (info8[1].equals("1")) {
                        Egci.producerMonitorOneServices.add(producerService8);
                    }
                    if (info8[2].equals("1")) {
                        Egci.producerMonitorTwoServices.add(producerService8);
                    }
                    if (info8[3].equals("1")) {
                        Egci.producerMonitorThreeServices.add(producerService8);
                    }
                    Egci.producerServiceMap.put(socketInfo.getInetAddress().getHostAddress(), producerService8);
                    break;
                case 9://同步单台设备
                    String[] info9 = mess.split("#");
                    Thread thread = new ImportStaffToSingleEquipmentService(info9[1]);
                    thread.start();
                    sendToClient(socketInfo, br, "success");
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.error("socket数据处理出错：", e);
            sendToClient(socketInfo, br, "error");
        }
    }

    /*
     *返回消息到客户端
     * */
    private void sendToClient(Socket socket, BufferedReader br, String message) {
        try {
            OutputStream os = socket.getOutputStream();
            os.write((message + "\r\n").getBytes());
            os.flush();
            br.close();
            os.close();
            socket.close();
        } catch (IOException e) {
            logger.error("返回消息到客户端出错：" + e);
        }
    }

    /*
     * 获取设备状态
     * */
    private List<StatusEntity> getStatus(List<String> deviceIps) {
        List<StatusEntity> deviceStatus = new ArrayList<StatusEntity>();
        LoginService loginService = new LoginService();
        for (String deviceIp : deviceIps) {
            StatusEntity statusEntity = new StatusEntity();
            //判断是否在线
            loginService.login(deviceIp.substring(1), Egci.devicePort, Egci.deviceName, Egci.devicePass);
            if (loginService.getlUserID().longValue() > -1) {
                statusEntity = statusService.getWorkStatus(loginService.getlUserID());
                statusEntity.setIsLogin("1");
                statusEntity.setDeviceIp(deviceIp.substring(1));
            } else {
                statusEntity.setIsLogin("0");
                statusEntity.setDeviceIp(deviceIp.substring(1));
                statusEntity.setCardNumber("0");
                statusEntity.setPassMode("0");
            }
            deviceStatus.add(statusEntity);
        }
        loginService.logout();
        return deviceStatus;
    }
}
