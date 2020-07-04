package proj.checkIN.services;

import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.ConnectionFactory;

@Configuration
public class RabbitMQConnectionFactory {
	private static ConnectionFactory factory;
	
	public static ConnectionFactory create() {
		factory = new ConnectionFactory();
		factory.setHost("localhost");
		factory.setPort(5672);
		factory.setUsername("pushService");
		factory.setPassword("kfc0409");
		factory.setVirtualHost("pushHost");
		return factory;
	}
}
