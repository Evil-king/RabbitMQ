package com.hwq.rabbit.amqp;

import com.hwq.rabbit.conn.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Send {

    private final static String QUEUE_NAME = "QUEUE_simple";

    public static void main(String[] args) throws IOException, TimeoutException {
        //链接MQ 创建channel
        Connection connection = ConnectionUtils.getConnection();
        Channel channel = connection.createChannel();

        //创建队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        String message = "Hello simple";

        try {
            channel.txSelect();
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            int result = 1 / 0;
            channel.txCommit();
        } catch (Exception e) {
            channel.txRollback();
            System.out.println("-----msg rollBack");
        } finally {
            System.out.println("---------send msg over:" + message);
        }
        channel.close();
        connection.close();
    }
}
