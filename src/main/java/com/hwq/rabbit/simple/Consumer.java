package com.hwq.rabbit.simple;

import com.hwq.rabbit.conn.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;

public class Consumer {
    private static final String QUEUE_NAME = "QUEUE_simple";

    public static void main(String[] args) throws Exception {

        Connection connection = ConnectionUtils.getConnection();
        Channel channel = connection.createChannel();

        DefaultConsumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body,"utf-8");
                System.out.println(" [x] Received '" + message + "'");
            }
        };
        //监听队列
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
