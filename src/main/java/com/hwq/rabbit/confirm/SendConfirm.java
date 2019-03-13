package com.hwq.rabbit.confirm;

import com.hwq.rabbit.conn.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class SendConfirm {

    private final static String QUEUE_NAME = "QUEUE_simple_confirm";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        //链接MQ 创建channel
        Connection connection = ConnectionUtils.getConnection();
        Channel channel = connection.createChannel();

        //创建队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        //生产者通过调用channel的confirmSelect方法将channel设置为confirm模式
        channel.confirmSelect();

        String message = "Hello simple";

        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());

        if(!channel.waitForConfirms()){
            System.out.println("send message failed.");
        }else{
            System.out.println(" send messgae ok ...");
        }
        channel.close();
        connection.close();
    }
}
