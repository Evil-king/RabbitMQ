package com.hwq.rabbit.work;

import com.hwq.rabbit.conn.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * Round-robin（轮询分发）
 */
public class SendMQ {

    private static final String QUEUE_NAME = "test_queue_work";

    public static void main(String[] args) throws Exception {
        //获取链接以及mq通道
        Connection connection = ConnectionUtils.getConnection();
        Channel channel = connection.createChannel();

        // 声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        for (int i = 0; i < 50; i++) {
            //发消息
            String msg = "." + i;
            channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
            System.out.println("--------Send ms:" + msg);
        }
        channel.close();
        connection.close();
    }
}
