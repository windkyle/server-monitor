package com.dyw.queue.timer;

import com.dyw.queue.task.AlarmTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

public class AlarmTimer {
    private static Logger logger = LoggerFactory.getLogger(AlarmTimer.class);

    public static void open() {
        Timer timer = new Timer();
        AlarmTaskService alarmTimerService = new AlarmTaskService();
        timer.schedule(alarmTimerService, 60000, 20000);
        logger.info("启用布防自动重连功能");
    }
}
