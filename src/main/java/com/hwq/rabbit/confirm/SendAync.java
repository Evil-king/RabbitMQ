package com.hwq.rabbit.confirm;

import com.hwq.rabbit.conn.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;

public class SendAync {
    private static final String QUEUE_NAME = "QUEUE_simple_confirm_aync";
    public static void main(String[] args) throws IOException, TimeoutException {
        /* 获取一个连接 */
        Connection connection = ConnectionUtils.getConnection();
        /* 从连接中创建通道 */
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //生产者通过调用channel的confirmSelect方法将channel设置为confirm模式
        channel.confirmSelect();
        final SortedSet<Long> confirmSet = Collections.synchronizedSortedSet(new
                TreeSet<Long>());
        channel.addConfirmListener(new ConfirmListener() {
        //每回调一次handleAck方法，unconfirm集合删掉相应的一条（multiple=false）或多条（multiple=true）记录。
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws
                    IOException {
                if (multiple) {
                    System.out.println("--multiple--");
                    confirmSet.headSet(deliveryTag + 1).clear();//用一个SortedSet, 返回此有序集合中小于end的所有元素。
                } else {
                    System.out.println("--multiple false--");
                    confirmSet.remove(deliveryTag);
                }
            }
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws
                    IOException {
                System.out.println("Nack, SeqNo: " + deliveryTag + ", multiple:" + multiple);
                if (multiple) {
                    confirmSet.headSet(deliveryTag + 1).clear();
                } else {
                    confirmSet.remove(deliveryTag);
                }
            }
        });
        String msg = "Hello QUEUE !";
        while (true) {
            long nextSeqNo = channel.getNextPublishSeqNo();
            channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
            confirmSet.add(nextSeqNo);
        }
    }

}
