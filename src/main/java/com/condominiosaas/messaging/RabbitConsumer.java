package com.condominiosaas.messaging;

import com.condominiosaas.service.EmailSenderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class RabbitConsumer {
	private static final Logger logger = LoggerFactory.getLogger(RabbitConsumer.class);
	private final ObjectMapper objectMapper;
	private final EmailSenderService emailSenderService;
	private final RabbitProperties props;

	public RabbitConsumer(ObjectMapper objectMapper, EmailSenderService emailSenderService, RabbitProperties props) {
		this.objectMapper = objectMapper;
		this.emailSenderService = emailSenderService;
		this.props = props;
	}

	@RabbitListener(queues = "#{@rabbitProperties.getQueueName()}", containerFactory = "rabbitListenerContainerFactory")
	public void receive(Message message, Channel channel) throws IOException {
		long deliveryTag = message.getMessageProperties().getDeliveryTag();
		String body = new String(message.getBody());

		EnvioEmailRequest req;
		try {
			req = objectMapper.readValue(body, EnvioEmailRequest.class);
		} catch (Exception ex) {
			logger.warn("[Worker] Mensagem inválida JSON. Movendo para DLQ. Body={}", body);
			channel.basicNack(deliveryTag, false, false);
			return;
		}

		if (!validar(req)) {
			logger.warn("[Worker] Dados de e-mail inválidos. Movendo para DLQ. Req={}", req);
			channel.basicNack(deliveryTag, false, false);
			return;
		}

		boolean sucesso = false;
		try {
			sucesso = emailSenderService.enviarSmtpAsync(req.getPara(), req.getAssunto(), req.getCorpo(), req.getEmpresaId());
		} catch (Exception ex) {
			logger.error("[Worker] Erro ao enviar e-mail", ex);
		}

		if (sucesso) {
			channel.basicAck(deliveryTag, false);
			logger.info("[Worker] E-mail processado com sucesso para {}", req.getPara());
		} else {
			// verificar retry count via x-death header
			Object xDeath = message.getMessageProperties().getHeaders().get("x-death");
			int retryCount = 0;
			if (xDeath instanceof List) retryCount = ((List<?>) xDeath).size();

			if (retryCount < props.getMaxRetryCount()) {
				logger.warn("[Worker] Falha no envio. Tentativa {}. Re-enfileirando via DLX...", retryCount + 1);
				channel.basicNack(deliveryTag, false, false);
			} else {
				logger.error("[Worker] Limite de tentativas atingido. Removendo da fila principal.");
				channel.basicNack(deliveryTag, false, false);
			}
		}
	}

	private boolean validar(EnvioEmailRequest req) {
		return req != null && req.getPara() != null && !req.getPara().isEmpty() && req.getEmpresaId() != null && req.getEmpresaId() != 0L;
	}
}
