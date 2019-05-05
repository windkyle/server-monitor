package com.dyw.queue.timer;

import com.dyw.queue.task.PingTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

public class PingTimer extends Thread {
    private Logger logger = LoggerFactory.getLogger(PingTimer.class);
    private String deviceIp;

    public PingTimer(String deviceIp) {
        this.deviceIp = deviceIp;
    }

    @Override
    public void run() {
        Timer timer = new Timer();
        PingTaskService pingTaskService = new PingTaskService(deviceIp);
        timer.schedule(pingTaskService, 1000, 10000);
        logger.info(deviceIp + ":启用自动更新设备网络状态");
    }
}
