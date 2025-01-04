package io.github.orionlibs.orion_digital_twin.broker;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.orionlibs.orion_digital_twin.ATest;
import io.github.orionlibs.orion_digital_twin.remote_data.DataPacketsDAO;
import io.github.orionlibs.orion_digital_twin.remote_data.TopicSubscribersDAO;
import java.io.IOException;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
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
    private MQTTBrokerClient testClient;
    private String clientID = "testClientId";


    @BeforeEach
    void setUp() throws MqttException
    {
        resetAndSeedDatabase();
        BrokerConnectionConfig config = new BrokerConnectionConfig();
        config.setHost("0.0.0.0");
        config.setPort(1883);
        brokerServer = new MQTTBrokerServer(config);
        brokerServer.startBroker();
    }


    /*@AfterEach
    void teardown() throws IOException
    {
        if(testClient != null && testClient.isConnected())
        {
            testClient.close();
        }
        brokerServer.stopBroker();
    }*/


    @Test
    void testBrokerStartup()
    {
        assertTrue(brokerServer.isRunning(), "Broker should be running after startup");
    }


    @Test
    void testPublishAndSubscribeAndUnsubscribeAndPersistenceAfterMQTTServerShutdown() throws MqttException, MQTTClientNotRunningException, IOException
    {
        testClient = getClient("testClientId");
        assertEquals(0, TopicSubscribersDAO.getNumberOfRecords());
        testClient.subscribe("/topic1/hello");
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
        assertEquals(1, DataPacketsDAO.getNumberOfRecords());
    }


    @Test
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
    }


    private static MQTTBrokerClient getClient(String clientId) throws MqttException
    {
        return new MQTTBrokerClient("tcp://0.0.0.0:1883", clientId);
    }
}
