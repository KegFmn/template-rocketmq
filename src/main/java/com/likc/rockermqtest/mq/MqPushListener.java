package com.likc.rockermqtest.mq;

import com.likc.rockermqtest.consumer.MqPushConsumer;
import com.likc.rockermqtest.dto.MessageDTO;
import com.likc.rockermqtest.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author likc
 * @date 2022/7/14
 * @description
 */
@Slf4j
@Component
public class MqPushListener implements MessageListenerConcurrently {

    @Autowired
    MqPushConsumer mqPushConsumer;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        //1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
        // 0表示每次按照上面定义的时间依次递增,第一次为10s,第二次为30s...
        //-1表示直接发往死信队列,不经过重试队列.
        //>0表示每次重试的时间间隔,由我们用户自定义,1表示重试间隔为1s,2表示5s,3表示10秒,依次递增,重试次数由配置consumer.setMaxReconsumeTimes(10)决定
        //发送的默认重试队列topic名称为%RETRY%+消费者组名,发送的默认死信队列topic名称为%DLQ%+消费者组名
        context.setDelayLevelWhenNextConsume(1); //表示重试间隔为1s
        for (MessageExt msg : msgs) {
            try {
                //System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msg);
                //String msgBody = new String(msg.getBody(), StandardCharsets.UTF_8);
                //if ("测试1".equals(msgBody)) {
                //    System.out.println("====失败消息开始=====");
                //    System.out.println("msg:" + msg);
                //    System.out.println("msgBody:" + msgBody);
                //    System.out.println("====失败消息结束=====");
                //    int i = 1/0;
                //    System.out.println(i);
                //}
                // 默认是在这里使用
                mqPushConsumer.onMessage(msg);
            } catch (Exception e) {
                //log.warn("consume message failed. messageExt:{}", msg, e);
                //System.out.println("------------------最大重试次数为:" + msgs.get(0).getReconsumeTimes() + "次!--------------------");
                //System.out.println("-------延迟级别设置:" + context.getDelayLevelWhenNextConsume());
                //long d = System.currentTimeMillis();
                //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //System.out.println("当前时间:" + sdf.format(d));
                //if (msgs.get(0).getReconsumeTimes() > 3) {
                //    context.setDelayLevelWhenNextConsume(-1); //重试大于3次直接发往死信队列
                //}
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
