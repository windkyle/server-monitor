package com.dyw.queue.service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ProducerService {

    private String queueName;
    private Logger logger = LoggerFactory.getLogger(ProducerService.class);
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;

    public ProducerService(String queueName, String queueIp) {
        this.queueName = queueName;
        factory = new ConnectionFactory();
        factory.setAutomaticRecoveryEnabled(true);//断线重连
        factory.setHost(queueIp);
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
        } catch (IOException e) {
            logger.error("创建生产者失败", e);
        } catch (TimeoutException e) {
            logger.error("创建生产者失败", e);
        }
        try {
            channel.queueDeclare(queueName, true, false, false, null);
            channel.basicQos(0, 1, true);//每次从队列中获取指定的条数为：1
        } catch (IOException e) {
            logger.error("消费者创建队列错误：", e);
        }
    }

    public void sendToQueue(String body) {
        try {
            channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, body.trim().getBytes("GBK"));
        } catch (Exception e) {
            logger.error("error", e);
        }
    }

    public void deleteQueue() {
        try {
            channel.queueDelete(queueName);
            logger.info("删除队列成功");
        } catch (IOException e) {
            logger.error("删除队列失败", e);
        }
    }

    /*
     * 返回Channel
     * */
    public Channel getChannel() {
        return channel;
    }

    /*
     * 返回队列名称
     * */
    public String getQueueName() {
        return queueName;
    }

    /*
     * 获取消费者数量
     * */
    public long getConsumerCount() {
        try {
            return channel.consumerCount(queueName);
        } catch (IOException e) {
            logger.error("获取消费者数量出错", e);
            return 0;
        }
    }
}
