package com.dyw.queue.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class ProcessTaskService extends TimerTask {
    private Logger logger = LoggerFactory.getLogger(ProcessTaskService.class);

    @Override
    public void run() {
        System.exit(0);
        logger.info("服务程序已经关闭");
    }
}
