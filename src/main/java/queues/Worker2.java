package queues;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * 工人（消费者C1和C2）
 * @author chen_miao
 *
 */
public class Worker2 {
	private static final String TASK_QUEUE_NAME="task_queue";
	
	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory=new ConnectionFactory();
		factory.setHost("172.25.255.72");
		final Connection connection=factory.newConnection();
		final Channel channel=connection.createChannel();
		channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
		System.out.println("Worker2 [*] Waiting for messages. To exit press CTRL+C");
		// 每次从队列中获取数量
		channel.basicQos(1);
		
		final Consumer consumer=new DefaultConsumer(channel){

			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				String message=new String(body,"UTF-8");
				System.out.println("Worker2 [x] Received '" + message + "'");
				
				try {
					doWork(message);
				} finally {
					System.out.println("Worker2 [x] Done");
					// 消息处理完成确认
					// 收到的标签，true确认全部消息到达包括提供的分发标记、false只是确认提供的分发标记
					channel.basicAck(envelope.getDeliveryTag(), false);
				}
			}
			
		};
		// 消息消费完成确认
		// 队列名称，自动应答，回调
		channel.basicConsume(TASK_QUEUE_NAME, false,consumer);
	}
	
	/**
	 * 任务处理
	 * @param task
	 */
	private static void doWork(String task){
		try {
			Thread.sleep(1000); // 暂停1秒钟
		} catch (InterruptedException _ignored) {
			Thread.currentThread().interrupt();
		}
	}
}
