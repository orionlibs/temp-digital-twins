package io.github.orionlibs.orion_digital_twin.broker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5WillPublish;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscribe;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscription;
import com.hivemq.client.mqtt.mqtt5.message.unsubscribe.Mqtt5Unsubscribe;
import io.github.orionlibs.orion_digital_twin.ATest;
import io.github.orionlibs.orion_digital_twin.connector.ConnectorFactory;
import io.github.orionlibs.orion_digital_twin.remote_data.DataPacketsDAO;
import io.github.orionlibs.orion_digital_twin.remote_data.TopicSubscribersDAO;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
//@Execution(ExecutionMode.CONCURRENT)
public class MQTTBrokerServerTest4 extends ATest
{
    private MQTTBrokerServer brokerServer;
    private Mqtt5BlockingClient testClient;
    private String clientID = "testClientId";


    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException, URISyntaxException
    {
        resetAndSeedDatabase();
        brokerServer = new MQTTBrokerServer();
        brokerServer.startBroker();
    }


    @AfterEach
    void teardown()
    {
        if(testClient != null && testClient.getConfig().getState().isConnectedOrReconnect())
        {
            testClient.disconnect();
        }
        brokerServer.stopBroker();
    }


    @Test
    void testBrokerStartup()
    {
        assertTrue(brokerServer.isRunning(), "Broker should be running after startup");
    }


    @Test
    void testPublishAndSubscribeAndUnsubscribeAndPersistenceAfterMQTTServerShutdown()
    {
        testClient = getClient(clientID);
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
    }


    /*@Test
    void testUsingMQTTBrokerServerWhenIsNotRunning()
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


    private static Mqtt5BlockingClient getClient(String clientId)
    {
        return ConnectorFactory.newMqttConnector("0.0.0.0", 1883, clientId);
    }
}
