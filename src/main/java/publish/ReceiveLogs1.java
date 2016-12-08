package publish;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class ReceiveLogs1 {
	private static final String EXCHANGE_NAME="logs";
	
	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory=new ConnectionFactory();
		factory.setHost("172.25.255.72");
		Connection connection=factory.newConnection();
		Channel channel=connection.createChannel();
		
		// 申明了一个exchange，广播形式的
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		// 申明了一个临时队列
		String queueName=channel.queueDeclare().getQueue();
		// 将exchange与队列绑定
		channel.queueBind(queueName, EXCHANGE_NAME, "");
		
		System.out.println("[*] Waiting for message. To exit press CTRL+C");
		
		Consumer consumer=new DefaultConsumer(channel){

			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				String message=new String(body,"UTF-8");
				System.out.println("[x] Received '"+message+"'");
			}
			
		};
		channel.basicConsume(queueName, true,consumer);
	}
}
