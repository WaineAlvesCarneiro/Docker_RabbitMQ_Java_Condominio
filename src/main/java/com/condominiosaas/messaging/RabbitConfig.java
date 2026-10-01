package com.condominiosaas.messaging;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {

	private final RabbitProperties props;

	public RabbitConfig(RabbitProperties props) {
		this.props = props;
	}

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory cf = new CachingConnectionFactory(props.getHost(), props.getPort());
		cf.setUsername(props.getUsername());
		cf.setPassword(props.getPassword());
		return cf;
	}

	@Bean
	public Queue emailQueue() {
		Map<String, Object> args = new HashMap<>();
		args.put("x-dead-letter-exchange", props.getDeadLetterExchange());
		args.put("x-dead-letter-routing-key", props.getQueueName());
		return new Queue(props.getQueueName(), true, false, false, args);
	}

	@Bean
	public Queue deadLetterQueue() {
		return new Queue(props.getDeadLetterQueue(), true);
	}

	@Bean
	public DirectExchange deadLetterExchange() {
		return new DirectExchange(props.getDeadLetterExchange());
	}

	@Bean
	public Binding dlxBinding() {
		return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(props.getQueueName());
	}

	@Bean
	public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(jackson2JsonMessageConverter());
		return template;
	}

	@Bean
	public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMessageConverter(jackson2JsonMessageConverter());
		factory.setAcknowledgeMode(org.springframework.amqp.core.AcknowledgeMode.MANUAL);
		return factory;
	}
}
