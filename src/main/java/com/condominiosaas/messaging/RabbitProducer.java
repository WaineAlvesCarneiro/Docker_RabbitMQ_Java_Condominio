package com.condominiosaas.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitProducer {
	private static final Logger logger = LoggerFactory.getLogger(RabbitProducer.class);
	private final RabbitTemplate template;
	private final RabbitProperties props;

	public RabbitProducer(RabbitTemplate template, RabbitProperties props) {
		this.template = template;
		this.props = props;
	}

	public void publicarMensagem(Object mensagem) {
		try {
			// envia para default exchange com routingKey = queueName
			template.convertAndSend(props.getQueueName(), mensagem);
			logger.info("[RabbitMQ] Mensagem enviada para fila {}", props.getQueueName());
		} catch (Exception ex) {
			logger.error("[RabbitMQ] Falha ao publicar mensagem", ex);
			throw ex;
		}
	}
}
