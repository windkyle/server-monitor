package com.dyw.queue.task;

import com.dyw.queue.controller.Egci;
import com.dyw.queue.service.CustomerService;
import com.dyw.queue.service.ProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class QueueTaskService extends TimerTask {
    private Logger logger = LoggerFactory.getLogger(QueueTaskService.class);

    @Override
    public void run() {
        for (ProducerService producerService : Egci.producerServiceList) {
            if (producerService.getConsumerCount() < 1) {
                logger.info("队列：" + producerService.getQueueName() + " 异常");
                CustomerService customerService = new CustomerService(producerService.getQueueName(), producerService.getChannel());
                customerService.start();
            }
        }
    }
}
