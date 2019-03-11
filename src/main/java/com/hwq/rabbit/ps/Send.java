package com.hwq.rabbit.ps;

import com.hwq.rabbit.conn.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class Send {

    private final static String EXCHANGE_NAME = "test_exchange_fanout";

    public static void main(String[] args) throws Exception {
        //链接MQ 创建通道
        Connection connection = ConnectionUtils.getConnection();
        Channel channel = connection.createChannel();

        //声明exchange 交换器（转发器）
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");//fanout 分裂
        //消息内容
        String message = "Hello BB";
        channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");

        channel.close();
        connection.close();
    }
}
