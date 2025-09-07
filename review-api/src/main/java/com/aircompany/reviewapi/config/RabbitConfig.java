package com.aircompany.reviewapi.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "reviews.exchange";
    public static final String NEW_REVIEW_QUEUE = "reviews.new.queue";
    public static final String NEW_REVIEW_ROUTING_KEY = "review.new";

    @Bean
    public DirectExchange reviewExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue newReviewQueue() {
        return QueueBuilder.durable(NEW_REVIEW_QUEUE).build();
    }

    @Bean
    public Binding newReviewBinding(Queue newReviewQueue, DirectExchange reviewExchange) {
        return BindingBuilder.bind(newReviewQueue).to(reviewExchange).with(NEW_REVIEW_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
