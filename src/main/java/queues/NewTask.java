package queues;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

/**
 * 工厂任务安排者（生产者P）
 * @author chen_miao
 *
 */
public class NewTask {
	private static final String TASK_QUEUE_NAME="task_queue";
	
	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory=new ConnectionFactory();
		factory.setHost("172.25.255.72");
		Connection connection=factory.newConnection();
		Channel channel=connection.createChannel();
		//队列名称、是否持久化、独占队列、自动删除、其它参数
		channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
		// 分发消息
		for(int i=0;i<5;i++){
			String message="Hello World! "+i;
			//交换发布消息、路由key、消息的其他属性 - 路由头等、消息体
			channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
			System.out.println("[x] Send '"+message+"'");
		}
		channel.close();
		connection.close();
	}
}
