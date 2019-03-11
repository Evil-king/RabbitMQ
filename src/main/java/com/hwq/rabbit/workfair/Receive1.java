package com.hwq.rabbit.workfair;

import com.hwq.rabbit.conn.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * Fair dispatch（公平分发）
 */
public class Receive1 {
    private final static String QUEUE_NAME = "test_queue_work_fair";

    public static void main(String[] args) throws Exception {
        // 获取到连接以及mq通道
        Connection connection = ConnectionUtils.getConnection();
        final Channel channel = connection.createChannel();
        // 声明队列，主要为了防止消息接收者先运行此程序，队列还不存在时创建队列。
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.basicQos(1);//保证一次只分发一个
        //定义一个消息的消费者
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [1] Received '" + message + "'");
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    System.out.println(" [1] Done");
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        //消息应答
        boolean autoAck = false; //手动确认消息
        channel.basicConsume(QUEUE_NAME, autoAck, consumer);
    }
}
