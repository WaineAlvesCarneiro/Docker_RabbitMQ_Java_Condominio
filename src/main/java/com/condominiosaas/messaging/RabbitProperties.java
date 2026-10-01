package com.condominiosaas.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class RabbitProperties {
	private String host;
	private String username;
	private String password;
	private String queueName = "fila_emails";
	private String exchangeName = "email_exchange_";
	private String deadLetterExchange = "dlx_exchange";
	private String deadLetterQueue = "fila_emails_erro";
	private int port = 5672;
	private int maxRetryCount = 3;

	// getters & setters
	public String getHost() { return host; }
	public void setHost(String host) { this.host = host; }
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public String getQueueName() { return queueName; }
	public void setQueueName(String queueName) { this.queueName = queueName; }
	public String getExchangeName() { return exchangeName; }
	public void setExchangeName(String exchangeName) { this.exchangeName = exchangeName; }
	public String getDeadLetterExchange() { return deadLetterExchange; }
	public void setDeadLetterExchange(String deadLetterExchange) { this.deadLetterExchange = deadLetterExchange; }
	public String getDeadLetterQueue() { return deadLetterQueue; }
	public void setDeadLetterQueue(String deadLetterQueue) { this.deadLetterQueue = deadLetterQueue; }
	public int getPort() { return port; }
	public void setPort(int port) { this.port = port; }
	public int getMaxRetryCount() { return maxRetryCount; }
	public void setMaxRetryCount(int maxRetryCount) { this.maxRetryCount = maxRetryCount; }
}
