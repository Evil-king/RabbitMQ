package com.hwq.rabbit.ps;

import com.hwq.rabbit.conn.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;

public class Recv2 {

    private final static String QUEUE_NAME = "test_queue_fanout_msg";
    private final static String EXCHANGE_NAME = "test_exchange_fanout";

    public static void main(String[] args) throws Exception {
        //链接MQ 创建通道
        Connection connection = ConnectionUtils.getConnection();
        final Channel channel = connection.createChannel();

        //声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // 绑定队列到交换机
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");

        // 同一时刻服务器只会发一条消息给消费者
        channel.basicQos(1);

        //定义一个消息的消费者
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "utf-8");
                System.out.println(" [2] Received '" + message + "'");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println(" [2] Done");
                    // 手动回执
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };

        //监听队列
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, consumer);
    }
}
