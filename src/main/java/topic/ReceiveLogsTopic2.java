package topic;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class ReceiveLogsTopic2 {
	private static final String EXCHANGE_NAME="topic_logs";
	
	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory=new ConnectionFactory();
		factory.setHost("172.25.255.72");
		Connection connection=factory.newConnection();
		Channel channel=connection.createChannel();
		// 声明一个匹配模式的交换器
		channel.exchangeDeclare(EXCHANGE_NAME, "topic");
		String queueName=channel.queueDeclare().getQueue();
		// 路由关键字
		String[] routingKeys=new String[]{"*.*.rabbit","lazy.#"};
		// 绑定路由关键字
		for(String routingKey:routingKeys){
			channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
			System.out.println("ReceiveLogsTopic2 exchange:"+EXCHANGE_NAME+",queue:"+queueName+",BindRoutingKey:"+routingKey);
		}
		
		System.out.println("ReceiveLogsTopic2 [*] Waiting for message. To exit press CTRL+C");
		
		Consumer consumer=new DefaultConsumer(channel){

			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				String message=new String(body,"UTF-8");
				System.out.println("ReceiveLogsTopic1 [x] Received '"+envelope.getRoutingKey()+"':'"+message+"'");
			}
			
		};
		channel.basicConsume(queueName, true,consumer);
	}
}
