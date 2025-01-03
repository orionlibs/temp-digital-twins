package io.github.orionlibs.orion_digital_twin.broker;

import io.github.orionlibs.orion_calendar.CalendarService;
import io.github.orionlibs.orion_digital_twin.remote_data.DataPacketModel;
import io.github.orionlibs.orion_digital_twin.remote_data.DataPacketsDAO;
import io.github.orionlibs.orion_digital_twin.remote_data.TopicSubscriberModel;
import io.github.orionlibs.orion_digital_twin.remote_data.TopicSubscribersDAO;
import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.MemoryConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
    private BrokerConnectionConfig config;
    private Server mqttServer;
    private boolean running;


    public MQTTBroker(BrokerConnectionConfig config)
    {
        this.config = config;
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


    public void startBroker()
    {
        // Use config to start the broker, for example:
        if(config.isSecure())
        {
            startSecureBroker(config.getHost(), config.getPort(), config.getUsername(), config.getPassword());
        }
        else
        {
            startInsecureBroker(config.getHost(), config.getPort());
        }
    }


    private void startSecureBroker(String host, int port, String username, String password)
    {
        try
        {
            // Create broker configuration
            Properties configProps = new Properties();
            configProps.setProperty("host", host);
            configProps.setProperty("port", String.valueOf(port));
            configProps.setProperty("password_file", "password.conf"); // File defining users/passwords
            configProps.setProperty("ssl_port", String.valueOf(port));
            configProps.setProperty("jks_path", "keystore.jks"); // Path to your Java keystore
            configProps.setProperty("key_store_password", "keystorePassword");
            configProps.setProperty("key_manager_password", "keyManagerPassword");
            IConfig brokerConfig = new MemoryConfig(configProps);
            // Start the broker
            Server mqttBroker = new Server();
            mqttBroker.startServer(brokerConfig);
            System.out.println("Secure MQTT Broker started on " + host + ":" + port);
            this.running = true;
        }
        catch(Exception e)
        {
            this.running = false;
            throw new RuntimeException("Failed to start secure MQTT broker: " + e.getMessage(), e);
        }
    }


    private void startInsecureBroker(String host, int port)
    {
        try
        {
            // Create broker configuration
            Properties configProps = new Properties();
            configProps.setProperty("host", host);
            configProps.setProperty("port", String.valueOf(port));
            IConfig brokerConfig = new MemoryConfig(configProps);
            // Start the broker
            Server mqttBroker = new Server();
            mqttBroker.startServer(brokerConfig);
            System.out.println("Insecure MQTT Broker started on " + host + ":" + port);
            this.running = true;
        }
        catch(Exception e)
        {
            this.running = false;
            throw new RuntimeException("Failed to start insecure MQTT broker: " + e.getMessage(), e);
        }
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


    public void stopBroker()
    {
        if(mqttServer != null)
        {
            try
            {
                mqttServer.stopServer();
                System.out.println("MQTT Broker stopped successfully.");
            }
            catch(Exception e)
            {
                System.err.println("Error stopping MQTT Broker: " + e.getMessage());
            }
        }
        else
        {
            System.out.println("MQTT Broker is not running.");
        }
        executorService.shutdown();
    }


    public void clientReconnected(String clientId)
    {
        retrievePendingMessagesFromDatabase(clientId);
    }


    public boolean isRunning()
    {
        return running;
    }
}
