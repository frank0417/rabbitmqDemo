package publish;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLog {
	private static final String EXCHANGE_NAME="logs";
	
	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory=new ConnectionFactory();
		factory.setHost("172.25.255.72");
		Connection connection=factory.newConnection();
		Channel channel=connection.createChannel();
		
		// 申明exchange,广播形式的
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		
		// 分发消息
		for(int i=0;i<5;i++){
			String message="Hello World! "+i;
			// 这里发送消息，由exchange决定，发给谁
			channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
			System.out.println("[x] Sent '"+message+"'");
		}
		channel.close();
		connection.close();
	}
}
