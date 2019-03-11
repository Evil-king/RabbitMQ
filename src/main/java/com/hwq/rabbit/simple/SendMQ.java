package com.hwq.rabbit.simple;

import com.hwq.rabbit.conn.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.junit.Test;

public class SendMQ {

    private static final String QUEUE_NAME="QUEUE_simple";

    @Test
    public void sendMg() throws Exception {
        /*获取一个链接*/
        Connection connection  = ConnectionUtils.getConnection();

        /*从链接中创建管道*/
       Channel channel =  connection.createChannel();

        //创建队列 (声明) 因为我们要往队列里面发送消息,这是后就得知道往哪个队列中发送,就好比在哪个管子里面放水
        Boolean durable = false; //消息持久化的 设置为true是开启消息持久化
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        String msg = "Hello Simple QUEUE !";

        //第一个参数是exchangeName(默认情况下代理服务器端是存在一个""名字的exchange的,
        //因此如果不创建exchange的话我们可以直接将该参数设置成"",如果创建了exchange的话
        //我们需要将该参数设置成创建的exchange的名字),第二个参数是路由键
        channel.basicPublish("",QUEUE_NAME,null,msg.getBytes());
        System.out.println("--------Send ms:" + msg);

        channel.close();
        connection.close();
    }
}
