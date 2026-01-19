package com.bank.alert_demo.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RabbitMQConfig {

	@Bean
	public Queue inboundQueue() {
		return new Queue("inbound_alerts", true);
	}

	@Bean
	public Queue notifyQueue() {
		return new Queue("notify_alerts", true);
	}

	@Bean
	public Jackson2JsonMessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	// @Bean
	// public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
	// 		Jackson2JsonMessageConverter jsonMessageConverter) {
	// 	SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
	// 	factory.setConnectionFactory(connectionFactory);
	// 	factory.setMessageConverter(jsonMessageConverter);
	// 	return factory;
	// }

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
			Jackson2JsonMessageConverter jsonMessageConverter) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(jsonMessageConverter);
		return template;
	}
}
