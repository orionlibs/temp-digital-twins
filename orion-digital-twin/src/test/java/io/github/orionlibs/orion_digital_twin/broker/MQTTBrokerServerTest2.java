package io.github.orionlibs.orion_digital_twin.broker;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.orionlibs.orion_digital_twin.ATest;
import io.github.orionlibs.orion_digital_twin.remote_data.DataPacketsDAO;
import io.github.orionlibs.orion_digital_twin.remote_data.TopicSubscribersDAO;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
//@Execution(ExecutionMode.CONCURRENT)
public class MQTTBrokerServerTest2 extends ATest
{
    private MQTTBrokerServer brokerServer;
    private String clientID = "testClientId";


    @BeforeEach
    void setUp()
    {
        resetAndSeedDatabase();
        BrokerConnectionConfig config = new BrokerConnectionConfig();
        config.setHost("0.0.0.0");
        config.setPort(1883);
        brokerServer = new MQTTBrokerServer(config);
        brokerServer.startBroker();
    }


    @AfterEach
    void teardown()
    {
        brokerServer.stopBroker();
    }


    @Test
    void testBrokerStartup()
    {
        assertTrue(brokerServer.isRunning(), "Broker should be running after startup");
    }


    @Test
    void testPublishAndSubscribeAndUnsubscribeAndPersistenceAfterMQTTServerShutdown() throws MQTTServerNotRunningException
    {
        assertEquals(0, TopicSubscribersDAO.getNumberOfRecords());
        brokerServer.subscribe("/topic1/hello", clientID);
        brokerServer.subscribe("/topic2/hello", clientID);
        assertEquals(2, TopicSubscribersDAO.getNumberOfRecords());
        assertEquals(0, DataPacketsDAO.getNumberOfRecords());
        MqttProperties props = new MqttProperties();
        props.setMaximumQoS(2);
        MqttMessage message = new MqttMessage("Hello World!!".getBytes(UTF_8), 2, true, props);
        brokerServer.publish("/topic1/hello", message);
        brokerServer.publish("/topic2/hello", message);
        assertEquals(2, DataPacketsDAO.getNumberOfRecords());
        brokerServer.unsubscribe("/topic1/hello", clientID);
        assertEquals(1, TopicSubscribersDAO.getNumberOfRecords());
        assertEquals(1, DataPacketsDAO.getNumberOfRecords());
        brokerServer.stopBroker();
        assertEquals(1, TopicSubscribersDAO.getNumberOfRecords());
        assertEquals(1, DataPacketsDAO.getNumberOfRecords());
    }


    @Test
    void testUsingMQTTBrokerServerWhenIsNotRunning()
    {
        brokerServer.stopBroker();
        MQTTServerNotRunningException exception1 = assertThrows(MQTTServerNotRunningException.class, () -> {
            brokerServer.subscribe("/topic1/hello", clientID);
        });
        MqttProperties props = new MqttProperties();
        props.setMaximumQoS(2);
        MqttMessage message = new MqttMessage("Hello World!!".getBytes(UTF_8), 2, true, props);
        MQTTServerNotRunningException exception2 = assertThrows(MQTTServerNotRunningException.class, () -> {
            brokerServer.publish("/topic1/hello", message);
        });
        MQTTServerNotRunningException exception3 = assertThrows(MQTTServerNotRunningException.class, () -> {
            brokerServer.unsubscribe("/topic1/hello", clientID);
        });
    }
}
