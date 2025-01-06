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
import io.github.orionlibs.orion_digital_twin.broker.server.MQTTBrokerServer;
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
public class MQTTBrokerServerTest extends ATest
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
        startClient(clientID);
        assertEquals(0, TopicSubscribersDAO.getNumberOfRecords());
        testClient.subscribe(Mqtt5Subscribe.builder()
                        .addSubscription(Mqtt5Subscription.builder().topicFilter("test/topic1").qos(MqttQos.EXACTLY_ONCE).build())
                        .build());
        testClient.subscribe(Mqtt5Subscribe.builder()
                        .addSubscription(Mqtt5Subscription.builder().topicFilter("test/topic2").qos(MqttQos.EXACTLY_ONCE).build())
                        .build());
        assertEquals(2, TopicSubscribersDAO.getNumberOfRecords());
        assertEquals(0, DataPacketsDAO.getNumberOfRecords());
        testClient.publish(Mqtt5WillPublish.builder()
                        .topic("test/topic1")
                        .qos(MqttQos.EXACTLY_ONCE)
                        .payload("somePayload1".getBytes())
                        .build());
        assertEquals(1, DataPacketsDAO.getNumberOfRecords());
        testClient.unsubscribe(Mqtt5Unsubscribe.builder()
                        .topicFilter("test/topic1")
                        .build());
        assertEquals(1, TopicSubscribersDAO.getNumberOfRecords());
    }


    private void startClient(String clientId)
    {
        this.testClient = ConnectorFactory.newMqttConnector("0.0.0.0", 1883, clientId);
    }
}
