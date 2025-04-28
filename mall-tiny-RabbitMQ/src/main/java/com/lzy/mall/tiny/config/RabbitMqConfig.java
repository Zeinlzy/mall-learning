package com.lzy.mall.tiny.config;

import com.lzy.mall.tiny.dto.QueueEnum;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 基于 TTL (Time-To-Live) 和死信交换机 (DLX) 的延迟消息处理机制
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 订单消息实际消费队列所绑定的交换机
     */
    /**
     * 1.ExchangeBuilder: 这是 Spring AMQP 提供的一个构建器类，用于方便地创建不同类型的交换机对象
     * 2.directExchange(...): 调用 directExchange() 方法，表示我们要创建一个直连交换机
     * 3.durable(true): 这个方法设置交换机的 持久化 (durable) 属性为 true。
     *   持久化意味着这个交换机将在 RabbitMQ 服务器重启后依然存在，不会丢失
     * 4.build(): 调用构建器的 build() 方法，根据之前的配置创建并返回一个 Exchange 对象。
     * @return 创建并返回一个 DirectExchange（直连交换机） 对象。
     */
    @Bean
    DirectExchange orderDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_ORDER_CANCEL.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 订单延迟队列队列所绑定的交换机
     * TTL：Time-To-Live（存活时间） 是一个通用技术术语，表示某个数据或资源在系统中允许存在的最长时间
     */
    @Bean
    DirectExchange orderTtlDirect() {
        return (DirectExchange) ExchangeBuilder //交换机构建器
                .directExchange(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getExchange()) //创建直连交换机，参数是交换机名称
                .durable(true) //交换机持久化
                .build(); //根据以上参数构建交换机
    }

    /**
     * 订单实际消费队列
     */
    @Bean
    public Queue orderQueue() {
        return new Queue(QueueEnum.QUEUE_ORDER_CANCEL.getName()); //根据队列名称构建队列
    }

    /**
     * 订单延迟队列（死信队列）
     * 消息变成死信的原因通常有：
     *   1.消息自身的 TTL (Time-To-Live) 到期。
     *   2.队列达到最大长度，最早进入队列的消息被淘汰。
     *   3.消息被消费者拒绝 (basic.reject 或 basic.nack)，并且设置了 requeue=false。
     * 当前队列指定了消息变为死信消息时指定的死信交换机以及死信路由键
     */
    @Bean
    public Queue orderTtlQueue() {
        return QueueBuilder
                //创建一个持久化 (durable) 的队列
                .durable(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getName())
                //在当前队列的定义中，通过名为 "x-dead-letter-exchange" 的参数，指定了当这个队列中的消息变成死信时，应该被发送到哪个交换机
                .withArgument("x-dead-letter-exchange", QueueEnum.QUEUE_ORDER_CANCEL.getExchange())
                //通过名为"x-dead-letter-routing-key"的参数，指定了当这个队列中的死信消息被发送到死信交换机时，所使用的路由键
                .withArgument("x-dead-letter-routing-key", QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey())
                //后续：死信交换机 根据 死信路由键  找到对应的死信队列 , 然后死信交换机把接收到的死信消息发送给死信队列
                .build();
    }

    /**
     * 将订单队列绑定到交换机
     */
    @Bean
    Binding orderBinding(DirectExchange orderDirect,Queue orderQueue){
        return BindingBuilder // 绑定构建器
                .bind(orderQueue) // 将哪个队列绑定
                .to(orderDirect) // 绑定到哪个交换机
                .with(QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey()); // 绑定时使用的路由键 (Binding Key)
    }

    /**
     * 将订单延迟队列绑定到交换机
     */
    @Bean
    Binding orderTtlBinding(DirectExchange orderTtlDirect,Queue orderTtlQueue){
        return BindingBuilder
                .bind(orderTtlQueue)
                .to(orderTtlDirect)
                .with(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getRouteKey());
    }

}