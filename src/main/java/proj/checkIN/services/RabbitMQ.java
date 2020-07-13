package proj.checkIN.services;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@Service
public class RabbitMQ {
	ConnectionFactory factory;
	
	private final static String QUEUE_NAME = "msg_queue";
	private final static String EXCHANGE_NAME = "amq.direct";
	
	public RabbitMQ(){
		factory = RabbitMQConnectionFactory.create();
	};

	public void connect(String agentID) throws IOException, TimeoutException, InterruptedException {
		try(Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
			channel.exchangeDeclare(EXCHANGE_NAME, "direct");
			channel.queueDeclare(QUEUE_NAME, false, false, true, null);
			channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, agentID);
			channel.close();
			connection.close();
		} catch(TimeoutException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean send(String agentID) {
		try(Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
			channel.queueDeclare(QUEUE_NAME, false, false, true, null);
			String message = "remote sign out";
			channel.basicPublish(EXCHANGE_NAME, agentID, null, message.getBytes());
			channel.close();
			connection.close();
		} catch(TimeoutException e) {
			e.printStackTrace();
			return false;
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void test() throws IOException, TimeoutException {
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		channel.queueDeclare(QUEUE_NAME, true, false, false, null);
		String message = "Hello World!";
		channel.basicPublish("",QUEUE_NAME, null, message.getBytes());
		channel.close();
		connection.close();
	}
}