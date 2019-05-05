package com.dyw.queue.service;

import com.dyw.queue.controller.Egci;
import com.dyw.queue.entity.EquipmentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class EquipmentService {
    private static Logger logger = LoggerFactory.getLogger(EquipmentService.class);

    public static void initEquipmentInfo() {
        try {
            //获取设备ip列表
            List<EquipmentEntity> equipmentEntityList = Egci.session.selectList("mapping.equipmentMapper.getAllEquipment");
            Egci.deviceIps0 = new ArrayList<String>();
            Egci.deviceIps1 = new ArrayList<String>();
            Egci.deviceIps2 = new ArrayList<String>();
            Egci.deviceIps3 = new ArrayList<String>();
            Egci.deviceIps0WithOctothorpe = new ArrayList<String>();
            Egci.deviceIps1WithOctothorpe = new ArrayList<String>();
            Egci.deviceIps2WithOctothorpe = new ArrayList<String>();
            Egci.deviceIps3WithOctothorpe = new ArrayList<String>();
            Egci.deviceIps0Map = new HashMap<String, String>();
            Egci.deviceIpsAlarmFail = new HashSet<String>();
            for (EquipmentEntity equipmentEntity : equipmentEntityList) {
                //如果对象中有数据，就会循环打印出来
                Egci.deviceIps0Map.put(equipmentEntity.getIP(), equipmentEntity.getName());
                Egci.deviceIps0.add(equipmentEntity.getIP());
                Egci.deviceIps0WithOctothorpe.add("#" + equipmentEntity.getIP());
                if (equipmentEntity.getGroupId() == 2) {
                    Egci.deviceIps1.add(equipmentEntity.getIP());
                    Egci.deviceIps1WithOctothorpe.add("#" + equipmentEntity.getIP());
                } else if (equipmentEntity.getGroupId() == 3) {
                    Egci.deviceIps2.add(equipmentEntity.getIP());
                    Egci.deviceIps2WithOctothorpe.add("#" + equipmentEntity.getIP());
                } else if (equipmentEntity.getGroupId() == 4) {
                    Egci.deviceIps3.add(equipmentEntity.getIP());
                    Egci.deviceIps3WithOctothorpe.add("#" + equipmentEntity.getIP());
                }
            }
            logger.info("所有设备ip：" + String.valueOf(Egci.deviceIps0WithOctothorpe));
            logger.info("一核设备ip：" + String.valueOf(Egci.deviceIps1WithOctothorpe));
            logger.info("二核设备ip：" + String.valueOf(Egci.deviceIps2WithOctothorpe));
            logger.info("三核设备ip：" + String.valueOf(Egci.deviceIps3WithOctothorpe));
        } catch (Exception e) {
            logger.error("连接数据库和获取全部设备IP失败：", e);
        }
    }

    /*
     * 对所有一体机设备进行布防
     * */
    public static void initEquipmentAlarm() {
        for (String deviceIp : Egci.deviceIpsOn) {
            LoginService loginService = new LoginService();
            loginService.login(deviceIp, Egci.configEntity.getDevicePort(), Egci.configEntity.getDeviceName(), Egci.configEntity.getDevicePass());
            AlarmService alarmService = new AlarmService();
            if (!alarmService.setupAlarmChan(loginService.getlUserID())) {
                logger.info(deviceIp + "：第一次布防失败");
                Egci.deviceIpsAlarmFail.add(deviceIp);
                loginService.logout();
            }
            try {
                //每布防一台设备后暂停时间，用来防止数据量瞬间过大导致程序出错
                Thread.sleep(Egci.configEntity.getAlarmTime());
            } catch (InterruptedException e) {
                logger.error(deviceIp + "布防延迟失败", e);
            }
        }
    }
}
