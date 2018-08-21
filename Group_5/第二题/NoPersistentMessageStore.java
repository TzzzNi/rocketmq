package org.apache.rocketmq.store;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageExtBatch;
import org.apache.rocketmq.store.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NoPersistentMessageStore implements MessageStore {

    private volatile boolean shutdown = true;


    Map<String,Map<Integer, List<MessageExt>>> memStore;

    @Override
    public boolean load() {
        return false;
    }

    @Override
    public void start() throws Exception {
        this.shutdown = false;
        memStore = new ConcurrentHashMap<>();
    }

    @Override
    public void shutdown() {
        this.shutdown = true;
    }

    @Override
    public void destroy() {
        this.memStore = null;
    }

    @Override
    public PutMessageResult putMessage(MessageExtBrokerInner msg) {
        if (this.shutdown) {
            System.out.println("message store has shutdown, so putMessage is forbidden");
            return new PutMessageResult(PutMessageStatus.SERVICE_NOT_AVAILABLE, null);
        }
        if (memStore.get(msg.getTopic()) == null) {
            Map<Integer, List<MessageExt>> temp = new HashMap<>();
            memStore.put(msg.getTopic(),temp);
        }
        if (memStore.get(msg.getTopic()).get(msg.getQueueId()) == null) {
            List<MessageExt> temp = new ArrayList<>();
            memStore.get(msg.getTopic()).put(msg.getQueueId(), temp);
        }
        memStore.get(msg.getTopic()).get(msg.getQueueId()).add(msg);
        PutMessageResult result = new PutMessageResult(PutMessageStatus.PUT_OK,new AppendMessageResult(AppendMessageStatus.PUT_OK ));
        return result;
    }

    @Override
    public PutMessageResult putMessages(MessageExtBatch messageExtBatch) {
        return null;
    }

    @Override
    public GetMessageResult getMessage(String group, String topic, int queueId, long offset,
                                       int maxMsgNums, MessageFilter messageFilter) {
        memStore.get(topic).get(queueId).get((int)offset);
        GetMessageResult result = new GetMessageResult();
        return result;
    }

    @Override
    public long getMaxOffsetInQueue(String topic, int queueId) {
        return 0;
    }

    @Override
    public long getMinOffsetInQueue(String topic, int queueId) {
        return 0;
    }

    @Override
    public long getCommitLogOffsetInQueue(String topic, int queueId, long consumeQueueOffset) {
        return 0;
    }

    @Override
    public long getOffsetInQueueByTime(String topic, int queueId, long timestamp) {
        return 0;
    }

    @Override
    public MessageExt lookMessageByOffset(long commitLogOffset) {
        return null;
    }

    @Override
    public SelectMappedBufferResult selectOneMessageByOffset(long commitLogOffset) {
        return null;
    }

    @Override
    public SelectMappedBufferResult selectOneMessageByOffset(long commitLogOffset, int msgSize) {
        return null;
    }

    @Override
    public String getRunningDataInfo() {
        return null;
    }

    @Override
    public HashMap<String, String> getRuntimeInfo() {
        return null;
    }

    @Override
    public long getMaxPhyOffset() {
        return 0;
    }

    @Override
    public long getMinPhyOffset() {
        return 0;
    }

    @Override
    public long getEarliestMessageTime(String topic, int queueId) {
        return 0;
    }

    @Override
    public long getEarliestMessageTime() {
        return 0;
    }

    @Override
    public long getMessageStoreTimeStamp(String topic, int queueId, long consumeQueueOffset) {
        return 0;
    }

    @Override
    public long getMessageTotalInQueue(String topic, int queueId) {
        return 0;
    }

    @Override
    public SelectMappedBufferResult getCommitLogData(long offset) {
        return null;
    }

    @Override
    public boolean appendToCommitLog(long startOffset, byte[] data) {
        return false;
    }

    @Override
    public void executeDeleteFilesManually() {

    }

    @Override
    public QueryMessageResult queryMessage(String topic, String key, int maxNum, long begin,
                                           long end) {
        return null;
    }

    @Override
    public void updateHaMasterAddress(String newAddr) {

    }

    @Override
    public long slaveFallBehindMuch() {
        return 0;
    }

    @Override
    public long now() {
        return 0;
    }

    @Override
    public int cleanUnusedTopic(Set<String> topics) {
        return 0;
    }

    @Override
    public void cleanExpiredConsumerQueue() {

    }

    @Override
    public boolean checkInDiskByConsumeOffset(String topic, int queueId, long consumeOffset) {
        return false;
    }

    @Override
    public long dispatchBehindBytes() {
        return 0;
    }

    @Override
    public long flush() {
        return 0;
    }

    @Override
    public boolean resetWriteOffset(long phyOffset) {
        return false;
    }

    @Override
    public long getConfirmOffset() {
        return 0;
    }

    @Override
    public void setConfirmOffset(long phyOffset) {

    }

    @Override
    public boolean isOSPageCacheBusy() {
        return false;
    }

    @Override
    public long lockTimeMills() {
        return 0;
    }

    @Override
    public boolean isTransientStorePoolDeficient() {
        return false;
    }

    @Override
    public LinkedList<CommitLogDispatcher> getDispatcherList() {
        return null;
    }

    @Override
    public ConsumeQueue getConsumeQueue(String topic, int queueId) {
        return null;
    }
}