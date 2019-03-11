package com.hwq.rabbit.work;

import com.hwq.rabbit.conn.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;

/**
 *  Round-robin（轮询分发）
 */
public class Recv1 {

    private static final String QUEUE_NAME = "test_queue_work";

    public static void main(String[] args) throws Exception {
        //获取链接以及mq通道
        Connection connection = ConnectionUtils.getConnection();
        Channel channel = connection.createChannel();

        // 声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        //定义一个消息的消费者
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body,"utf-8");
                System.out.println(" [1] Received '" + message + "'");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println(" [1] Done");
                }
            }
        };

        //监听队列
        boolean autoAck = true;
        channel.basicConsume(QUEUE_NAME, autoAck, consumer);
    }
}
