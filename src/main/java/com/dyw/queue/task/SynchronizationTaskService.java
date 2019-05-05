package com.dyw.queue.task;

import com.dyw.queue.controller.Egci;
import com.dyw.queue.service.DatabaseService;
import com.dyw.queue.service.LoginService;
import com.dyw.queue.service.SynchronizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;


public class SynchronizationTaskService extends TimerTask {
    private Logger logger = LoggerFactory.getLogger(SynchronizationTaskService.class);
    private List<String> dataBaseCards = new ArrayList<String>();//数据库人员卡号信息

    public void run() {
        try {
            dataBaseCards.clear();
            dataBaseCards = Egci.session.selectList("mapping.staffMapper.getAllStaffCard");
            //1：单台启用同步；2：全部启用同步
            if (Egci.configEntity.getSynchronization().equals("1")) {
                Egci.deviceIps0 = Collections.singletonList(Egci.configEntity.getTestIp());
            }
            for (String deviceIp : Egci.deviceIps0) {
                LoginService loginService = new LoginService();
                loginService.login(deviceIp, Egci.configEntity.getDevicePort(), Egci.configEntity.getDeviceName(), Egci.configEntity.getDevicePass());
                if (loginService.getlUserID().longValue() > -1) {
                    SynchronizationService synchronizationService = new SynchronizationService(deviceIp, loginService, Egci.configEntity, dataBaseCards);
                    synchronizationService.start();
                    Thread.sleep(Egci.configEntity.getDataBaseTime());//避免同时查询数据库
                } else {
                    logger.info(deviceIp + "：同步失败：设备不在线或网络异常");
                }
            }
        } catch (Exception e) {
            logger.error("同步出错", e);
        }
    }
}