package io.github.orionlibs.orion_digital_twin.broker.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import io.github.orionlibs.orion_digital_twin.ATest;
import io.github.orionlibs.orion_digital_twin.Utils;
import io.github.orionlibs.orion_digital_twin.broker.server.MQTTBrokerServer;
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
    private Mqtt5AsyncClient testPublisherClient;
    private Mqtt5AsyncClient testSubscriberClient;
    private Mqtt5AsyncClient testUnsubscriberClient;
    //private Mqtt5RxClient testClient;
    //private Mqtt5BlockingClient testClient;
    private String clientID = "testClientId";


    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException, URISyntaxException
    {
        resetAndSeedDatabase();
        brokerServer = new MQTTBrokerServer();
        brokerServer.startBroker(false, false);
        Utils.nonblockingDelay(3);
    }


    @AfterEach
    void teardown()
    {
        if(testPublisherClient != null && testPublisherClient.getConfig().getState().isConnectedOrReconnect())
        {
            testPublisherClient.disconnect();
        }
        if(testSubscriberClient != null && testSubscriberClient.getConfig().getState().isConnectedOrReconnect())
        {
            testSubscriberClient.disconnect();
        }
        if(testUnsubscriberClient != null && testUnsubscriberClient.getConfig().getState().isConnectedOrReconnect())
        {
            testUnsubscriberClient.disconnect();
        }
        brokerServer.stopBroker();
    }


    /*@Test
    void testBrokerStartup()
    {
        assertTrue(brokerServer.isRunning(), "Broker should be running after startup");
    }*/


    @Test
    void testPublishAndSubscribeAndUnsubscribeAndPersistenceAfterMQTTServerShutdown() throws InterruptedException
    {
        assertEquals(0, TopicSubscribersDAO.getNumberOfRecords());
        //assertEquals(0, DataPacketsDAO.getNumberOfRecords());
        startSubscriberClient("test/topic1", MqttQos.EXACTLY_ONCE, clientID);
        Utils.nonblockingDelay(4);
        assertEquals(1, TopicSubscribersDAO.getNumberOfRecords());
        startPublisherClient("test/topic1", "somePayload1", "testPublisherId");
        Utils.nonblockingDelay(2);
        startPublisherClient("test/topic1", "somePayload2", "testPublisherId");
        Utils.nonblockingDelay(2);
        startPublisherClient("test/topic1", "somePayload3", "testPublisherId");
        Utils.nonblockingDelay(6);
        assertEquals(3, DataPacketsDAO.getNumberOfRecords());
        Utils.nonblockingDelay(2);
        startUnsubscriberClient("test/topic1", clientID);
        Utils.nonblockingDelay(2);
        assertEquals(0, TopicSubscribersDAO.getNumberOfRecords());
        assertEquals(0, DataPacketsDAO.getNumberOfRecords());
    }


    private void startPublisherClient(String topic, String payload, String clientId)
    {
        //this.testPublisherClient = new ConnectorFactory().newAsynchronousMQTTConnectorForPublisher("broker.hivemq.com", 1883, topic, payload, clientId);
        this.testPublisherClient = new ConnectorFactory().newAsynchronousMQTTConnectorForPublisher("0.0.0.0", 1883, topic, payload, clientId).getClient();
    }


    private void startSubscriberClient(String topic, MqttQos qualityOfServiceLevel, String clientId)
    {
        //this.testSubscriberClient = new ConnectorFactory().newAsynchronousMQTTConnectorForSubscriber("broker.hivemq.com", 1883, topic, clientId);
        this.testSubscriberClient = new ConnectorFactory().newAsynchronousMQTTConnectorForSubscriber("0.0.0.0", 1883, topic, qualityOfServiceLevel, clientId).getClient();
    }


    private void startUnsubscriberClient(String topic, String clientId)
    {
        //this.testSubscriberClient = new ConnectorFactory().newAsynchronousMQTTConnectorForSubscriber("broker.hivemq.com", 1883, topic, clientId);
        this.testUnsubscriberClient = new ConnectorFactory().newAsynchronousMQTTConnectorForUnsubscriber("0.0.0.0", 1883, topic, clientId).getClient();
    }
}
