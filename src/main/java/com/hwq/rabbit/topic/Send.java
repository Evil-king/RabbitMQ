package com.hwq.rabbit.topic;

import com.hwq.rabbit.conn.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Send {

    private final static String EXCHANGE_NAME = "test_exchange_topic";

    public static void main(String[] args) throws IOException, TimeoutException {
        //链接MQ，并且创建channel
        Connection connection = ConnectionUtils.getConnection();
        Channel channel = connection.createChannel();

        //声明echange交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        //消息内容
        String message = "id=1001";
        channel.basicPublish(EXCHANGE_NAME, "item.add", null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
        channel.close();
        connection.close();
    }
}
