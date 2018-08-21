import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.impl.consumer.PullResultExt;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.*;
public class OrderedConsumer {
    private static final Map<MessageQueue, Long> offsetTable = new HashMap<MessageQueue, Long>();
    public static void main(String[] args) throws Exception {
        // initialize a pull consumer
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("pullConsumer");
        consumer.setNamesrvAddr("127.0.0.1:9876");
        consumer.start();

        while (true) {
            try {
                // bulid the PriorityQueue and define the cmp function
                Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues("TopicTest1");
                PriorityQueue<MessageExt> queue= new PriorityQueue<MessageExt>(mqs.size(),new Comparator<MessageExt>(){
                    @Override
                    public int compare(MessageExt o1,MessageExt o2){
                        if (o1.getBornTimestamp()<o2.getBornTimestamp())
                            return -1;
                        else if (o1.getBornTimestamp()==o2.getBornTimestamp())
                            return 0;
                        else
                            return 1;
                    }
                });
                
                // process different types of the message
                for(MessageQueue mq:mqs) {
                    System.out.print("Consume from the queue: " + mq + " ");
                    PullResultExt pullResult =(PullResultExt)consumer.pullBlockIfNotFound(mq, null, getMessageQueueOffset(mq), 32);
                    putMessageQueueOffset(mq, pullResult.getNextBeginOffset());
                    switch (pullResult.getPullStatus()) {
                        case FOUND:
                            System.out.println("FOUND");
                            List<MessageExt> messageExtList = pullResult.getMsgFoundList();
                            for (MessageExt m : messageExtList) {
                                queue.add(m);
                            }
                            break;
                        case NO_MATCHED_MSG:
                            System.out.println("NO_MATCHED_MSG");
                            break;
                        case NO_NEW_MSG:
                            System.out.println("NO_NEW_MSG");
                            break ;
                        case OFFSET_ILLEGAL:
                            System.out.println("OFFSET_ILLEGAL");
                            break;
                        default:
                            break;
                    }
                }
                for (MessageExt m : queue) {
                    System.out.print(new String(m.getBody()));
                    System.out.println(new String(m.getBornTimestamp() + ""));
                }
            } catch (MQClientException e) {
                e.printStackTrace();
            }
        }

    }
    
    // push the message into the MessageQueue
    private static void putMessageQueueOffset(MessageQueue mq, long offset) {
        offsetTable.put(mq, offset);
    }
    
    // get the offset of the MessageQueue
    private static long getMessageQueueOffset(MessageQueue mq) {
        Long offset = offsetTable.get(mq);
        if (offset != null)
            return offset;
        return 0;
    }
}
