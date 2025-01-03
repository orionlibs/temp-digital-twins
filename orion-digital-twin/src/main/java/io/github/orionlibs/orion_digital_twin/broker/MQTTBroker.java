package io.github.orionlibs.orion_digital_twin.broker;

import io.github.orionlibs.orion_calendar.CalendarService;
import io.github.orionlibs.orion_digital_twin.remote_data.DataPacketModel;
import io.github.orionlibs.orion_digital_twin.remote_data.DataPacketsDAO;
import io.github.orionlibs.orion_digital_twin.remote_data.TopicSubscriberModel;
import io.github.orionlibs.orion_digital_twin.remote_data.TopicSubscribersDAO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.eclipse.paho.mqttv5.common.MqttMessage;

public class MQTTBroker
{
    private final Map<String, Set<String>> topicToSubscribersMapper;
    private final Map<String, BlockingQueue<MqttMessage>> subscriberMessageQueues;
    private final ExecutorService executorService;


    public MQTTBroker()
    {
        this.topicToSubscribersMapper = new ConcurrentHashMap<>();
        List<TopicSubscriberModel> allSubscribers = TopicSubscribersDAO.getAll();
        this.subscriberMessageQueues = new ConcurrentHashMap<>();
        Set<String> subscriberIDs = new HashSet<>();
        for(TopicSubscriberModel subscriber : allSubscribers)
        {
            topicToSubscribersMapper.computeIfAbsent(subscriber.getTopic(), k -> ConcurrentHashMap.newKeySet()).add(subscriber.getClientId());
            subscriberIDs.add(subscriber.getClientId());
        }
        for(String subscriberID : subscriberIDs)
        {
            retrievePendingMessagesFromDatabase(subscriberID);
        }
        this.executorService = Executors.newCachedThreadPool();
    }


    public void subscribe(String topic, String clientId)
    {
        topicToSubscribersMapper.computeIfAbsent(topic, k -> ConcurrentHashMap.newKeySet()).add(clientId);
        subscriberMessageQueues.computeIfAbsent(clientId, k -> new LinkedBlockingQueue<>());
        persistSubscription(topic, clientId);
    }


    public void unsubscribe(String topic, String clientId)
    {
        Set<String> subscribers = topicToSubscribersMapper.get(topic);
        if(subscribers != null)
        {
            subscribers.remove(clientId);
            if(subscribers.isEmpty())
            {
                topicToSubscribersMapper.remove(topic);
            }
            unpersistSubscription(topic, clientId);
        }
    }


    public void persistMessage(String topic, String clientId, MqttMessage message)
    {
        if(message.getQos() > 0)
        {
            DataPacketsDAO.save(DataPacketModel.builder()
                            .clientId(clientId)
                            .topic(topic)
                            .content(new String(message.getPayload()))
                            .qualityOfServiceLevel(message.getQos())
                            .isDeliveredToClient(Boolean.FALSE)
                            .publicationDateTime(CalendarService.getCurrentDatetimeAsSQLTimestamp())
                            .build());
        }
    }


    public void persistSubscription(String topic, String clientId)
    {
        TopicSubscribersDAO.save(TopicSubscriberModel.builder()
                        .clientId(clientId)
                        .topic(topic)
                        .subscriptionDateTime(CalendarService.getCurrentDatetimeAsSQLTimestamp())
                        .build());
    }


    public void unpersistSubscription(String topic, String clientId)
    {
        TopicSubscribersDAO.delete(topic, clientId);
    }


    public void publish(String topic, MqttMessage message)
    {
        Set<String> subscribers = topicToSubscribersMapper.get(topic);
        if(subscribers != null)
        {
            for(String clientId : subscribers)
            {
                BlockingQueue<MqttMessage> queue = subscriberMessageQueues.get(clientId);
                if(queue != null)
                {
                    queue.offer(message);
                }
                persistMessage(topic, clientId, message);
            }
        }
    }


    private void retrievePendingMessagesFromDatabase(String clientId)
    {
        List<DataPacketModel> pendingMessages = DataPacketsDAO.getAllUndeliveredForClientIdInChronologicalOrder(clientId);
        BlockingQueue<MqttMessage> queue = subscriberMessageQueues.get(clientId);
        if(queue != null)
        {
            for(DataPacketModel message : pendingMessages)
            {
                queue.offer(DataPacketToMQTTMessageConverter.convert(message));
            }
            for(DataPacketModel message : pendingMessages)
            {
                message.setIsDeliveredToClient(Boolean.TRUE);
                DataPacketsDAO.update(message);
            }
        }
    }


    public List<MqttMessage> getMessagesForClientId(String clientId)
    {
        BlockingQueue<MqttMessage> queue = subscriberMessageQueues.get(clientId);
        if(queue == null)
        {
            return Collections.emptyList();
        }
        List<MqttMessage> messages = new ArrayList<>();
        queue.drainTo(messages);
        return messages;
    }


    // Shut down the broker
    public void shutdown()
    {
        executorService.shutdown();
    }


    public void clientReconnected(String clientId)
    {
        retrievePendingMessagesFromDatabase(clientId);
    }
}
