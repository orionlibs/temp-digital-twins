package io.github.orionlibs.orion_digital_twin.broker.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import io.github.orionlibs.orion_digital_twin.ATest;
import io.github.orionlibs.orion_digital_twin.Utils;
import io.github.orionlibs.orion_digital_twin.broker.server.MQTTBrokerServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
//@Execution(ExecutionMode.CONCURRENT)
public class MQTTAuthenticationTest extends ATest
{
    private MQTTBrokerServer brokerServer;
    private Mqtt5AsyncClient testPublisherClient;
    private String clientID = "testClientId";


    @BeforeEach
    void setUp() throws Exception
    {
        resetAndSeedDatabase();
        brokerServer = new MQTTBrokerServer();
        brokerServer.startBroker(true, false);
        Utils.nonblockingDelay(3);
    }


    @AfterEach
    void teardown()
    {
        if(testPublisherClient != null && testPublisherClient.getConfig().getState().isConnectedOrReconnect())
        {
            testPublisherClient.disconnect();
        }
        brokerServer.stopBroker();
    }


    @Test
    void testClientAuthentication()
    {
        startPublisherClient("test/topic1", "somePayload1", clientID, "admin", "password");
        Utils.nonblockingDelay(2);
        assertTrue(testPublisherClient.getState().isConnectedOrReconnect());
        Utils.nonblockingDelay(2);
        startPublisherClient("test/topic1", "somePayload1", clientID, "admin", "wrongpassword");
        assertFalse(testPublisherClient.getState().isConnectedOrReconnect());
        Utils.nonblockingDelay(2);
        startPublisherClient("test/topic1", "somePayload1", clientID, "wronguser", "password");
        assertFalse(testPublisherClient.getState().isConnectedOrReconnect());
        Utils.nonblockingDelay(2);
        startPublisherClient("test/topic1", "somePayload1", clientID, "wronguser", "wrongpassword");
        assertFalse(testPublisherClient.getState().isConnectedOrReconnect());
    }


    private void startPublisherClient(String topic, String payload, String clientId, String username, String password)
    {
        this.testPublisherClient = new ConnectorFactory().newAsynchronousMQTTConnectorForPublisherWithCredentials("0.0.0.0", 1883, clientId, username, password).getClient();
    }
}
