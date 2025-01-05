package io.github.orionlibs.orion_digital_twin.broker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5WillPublish;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscribe;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscription;
import com.hivemq.client.mqtt.mqtt5.message.unsubscribe.Mqtt5Unsubscribe;
import io.github.orionlibs.orion_digital_twin.ATest;
import io.github.orionlibs.orion_digital_twin.remote_data.DataPacketsDAO;
import io.github.orionlibs.orion_digital_twin.remote_data.TopicSubscribersDAO;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
//@Execution(ExecutionMode.CONCURRENT)
public class MQTTBrokerServerTest4 extends ATest
{
    private MQTTBrokerServer2 brokerServer;
    private Mqtt5BlockingClient testClient;
    private String clientID = "testClientId";


    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException
    {
        resetAndSeedDatabase();
        BrokerConnectionConfig config = new BrokerConnectionConfig();
        config.setHost("0.0.0.0");
        config.setPort(1883);
        brokerServer = new MQTTBrokerServer2(config);
        brokerServer.startBroker();
    }


    @AfterEach
    void teardown() throws IOException, ExecutionException, InterruptedException
    {
        if(testClient != null && testClient.getConfig().getState().isConnected())
        {
            testClient.disconnect();
        }
        brokerServer.stopBroker();
    }


    @Test
    //@Disabled
    void testBrokerStartup()
    {
        assertTrue(brokerServer.isRunning(), "Broker should be running after startup");
    }


    @Test
        //@Disabled
    void testPublishAndSubscribeAndUnsubscribeAndPersistenceAfterMQTTServerShutdown() throws ExecutionException, InterruptedException
    {
        testClient = Mqtt5Client.builder()
                        .identifier(clientID)
                        .serverHost("0.0.0.0")
                        .serverPort(1883)
                        .buildBlocking();
        Mqtt5ConnAck connectionAcknowledgement = testClient.connect();
        //testClient = getClient(clientID);
        assertEquals(0, TopicSubscribersDAO.getNumberOfRecords());
        testClient.subscribe(Mqtt5Subscribe.builder()
                        .addSubscription(Mqtt5Subscription.builder().topicFilter("test/topic1").qos(MqttQos.AT_MOST_ONCE).build())
                        .build());
        testClient.subscribe(Mqtt5Subscribe.builder()
                        .addSubscription(Mqtt5Subscription.builder().topicFilter("test/topic2").qos(MqttQos.AT_MOST_ONCE).build())
                        .build());
        assertEquals(2, TopicSubscribersDAO.getNumberOfRecords());
        assertEquals(0, DataPacketsDAO.getNumberOfRecords());
        testClient.publish(Mqtt5WillPublish.builder()
                        .topic("test/topic1")
                        .qos(MqttQos.AT_MOST_ONCE)
                        .payload("somePayload1".getBytes())
                        .build());
        assertEquals(1, DataPacketsDAO.getNumberOfRecords());
        testClient.unsubscribe(Mqtt5Unsubscribe.builder()
                        .topicFilter("test/topic1")
                        .build());
        assertEquals(1, TopicSubscribersDAO.getNumberOfRecords());




        /*testClient.subscribe("/topic1/hello");
        testClient.subscribe("/topic2/hello");
        assertEquals(2, TopicSubscribersDAO.getNumberOfRecords());
        assertEquals(0, DataPacketsDAO.getNumberOfRecords());
        MqttProperties props = new MqttProperties();
        props.setMaximumQoS(2);
        MqttMessage message = new MqttMessage("Hello World!!".getBytes(UTF_8), 2, true, props);
        testClient.publish("/topic1/hello", message);
        testClient.publish("/topic2/hello", message);
        assertEquals(2, DataPacketsDAO.getNumberOfRecords());
        testClient.unsubscribe("/topic1/hello");
        assertEquals(1, TopicSubscribersDAO.getNumberOfRecords());
        assertEquals(1, DataPacketsDAO.getNumberOfRecords());
        testClient.close();
        assertEquals(1, TopicSubscribersDAO.getNumberOfRecords());
        assertEquals(1, DataPacketsDAO.getNumberOfRecords());*/
    }


    /*@Test
    @Disabled
    void testUsingMQTTBrokerServerWhenIsNotRunning() throws MqttException
    {
        testClient = getClient("testClientId");
        brokerServer.stopBroker();
        MQTTClientNotRunningException exception1 = assertThrows(MQTTClientNotRunningException.class, () -> {
            testClient.subscribe("/topic1/hello");
        });
        MqttProperties props = new MqttProperties();
        props.setMaximumQoS(2);
        MqttMessage message = new MqttMessage("Hello World!!".getBytes(UTF_8), 2, true, props);
        MQTTClientNotRunningException exception2 = assertThrows(MQTTClientNotRunningException.class, () -> {
            testClient.publish("/topic1/hello", message);
        });
        MQTTClientNotRunningException exception3 = assertThrows(MQTTClientNotRunningException.class, () -> {
            testClient.unsubscribe("/topic1/hello");
        });
    }*/


    private static MQTTBrokerClient getClient(String clientId) throws MqttException
    {
        return new MQTTBrokerClient("tcp://0.0.0.0:1883", clientId);
    }
}
