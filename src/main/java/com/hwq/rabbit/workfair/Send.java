package com.hwq.rabbit.workfair;

import com.hwq.rabbit.conn.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * Fair dispatch（公平分发）
 * <p>
 * 我们使用 basicQos( prefetchCount = 1)方法，来限制 RabbitMQ 只发不超过 1 条的消息给同
 * 一个消费者。当消息处理完毕后，有了反馈 ack，才会进行第二次发送。(也就是说需要手动反馈给 Rabbitmq )
 */
public class Send {

    private final static String QUEUE_NAME = "test_queue_work_fair";

    public static void main(String[] argv) throws Exception {
        // 获取到连接以及mq通道
        Connection connection = ConnectionUtils.getConnection();
        // 创建一个频道
        Channel channel = connection.createChannel();
        // 指定一个队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        int prefetchCount = 1;

        //每个消费者发送确认信号之前，消息队列不发送下一个消息过来，一次只处理一个消息
        //限制发给同一个消费者不得超过1条消息
        channel.basicQos(prefetchCount);
        // 发送的消息
        for (int i = 0; i < 50; i++) {
            String message = "." + i;
            // 往队列中发出一条消息
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
            Thread.sleep(i * 10);
        }
        // 关闭频道和连接
        channel.close();
        connection.close();
    }
}
