package com.dyw.queue.timer;

import com.dyw.queue.task.QueueTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

public class QueueTimer {
    private static Logger logger = LoggerFactory.getLogger(QueueTimer.class);

    public static void open() {
        Timer timer = new Timer();
        QueueTaskService queueTaskService = new QueueTaskService();
        timer.schedule(queueTaskService, 60000, 10000);
        logger.info("启用队列重连功能");
    }
}
