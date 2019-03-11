package com.hwq.rabbit.workfair;

import com.hwq.rabbit.conn.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * Fair dispatch（公平分发）
 */
public class Receive2 {
    private final static String QUEUE_NAME = "test_queue_work_fair";
    public static void main(String[] args) throws Exception {
        // 获取到连接以及mq通道
        Connection connection = ConnectionUtils.getConnection();
        final Channel channel = connection.createChannel();
        // 声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.basicQos(1);//保证一次只分发一个
        //定义一个消息的消费者
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [2] Received '" + message + "'");
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    System.out.println(" [x] Done");
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        boolean autoAck = false; //关闭自动 确认
        channel.basicConsume(QUEUE_NAME, autoAck, consumer);
    }
}
