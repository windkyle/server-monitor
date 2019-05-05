package com.dyw.queue.task;

import com.dyw.queue.controller.Egci;
import com.dyw.queue.service.AlarmService;
import com.dyw.queue.service.LoginService;
import com.dyw.queue.service.NetStateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class AlarmTaskService extends TimerTask {
    private Logger logger = LoggerFactory.getLogger(AlarmTaskService.class);

    @Override
    public void run() {
        for (String deviceIp : Egci.deviceIpsAlarmFail) {
            logger.info(deviceIp + "：正在重新布防");
            LoginService loginService = new LoginService();
            if (loginService.login(deviceIp, Egci.configEntity.getDevicePort(), Egci.configEntity.getDeviceName(), Egci.configEntity.getDevicePass())) {
                AlarmService alarmService = new AlarmService();
                if (alarmService.setupAlarmChan(loginService.getlUserID())) {
                    if (Egci.deviceIpsAlarmFail.contains(deviceIp)) {
                        Egci.deviceIpsAlarmFail.remove(deviceIp);
                    }
                } else {
                    loginService.logout();
                }
            }
        }
    }
}
