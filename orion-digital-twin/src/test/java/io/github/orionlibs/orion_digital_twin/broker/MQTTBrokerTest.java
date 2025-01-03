package io.github.orionlibs.orion_digital_twin.broker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import io.github.orionlibs.orion_digital_twin.ATest;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
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
public class MQTTBrokerTest extends ATest
{
    private MQTTBroker broker;
    private IMqttClient testClient;


    @BeforeEach
    void setUp() throws MqttException
    {
        resetAndSeedDatabase();
        BrokerConnectionConfig config = new BrokerConnectionConfig();
        config.setHost("localhost");
        config.setPort(1883);
        broker = new MQTTBroker(config);
        broker.startBroker();
        testClient = new MqttClient("tcp://localhost:1883", "testClientId");
    }


    @AfterEach
    void teardown() throws Exception
    {
        if(testClient.isConnected())
        {
            testClient.disconnect();
        }
        broker.stopBroker();
    }


    @Test
    void testBrokerStartup()
    {
        assertTrue(broker.isRunning(), "Broker should be running after startup");
    }


    @Test
    void testClientConnection() throws Exception
    {
        testClient.connect();
        assertTrue(testClient.isConnected(), "Client should connect to the broker");
    }


    @Test
    void testMessagePublishingAndSubscription() throws Exception
    {
        testClient.connect();
        String topic = "test/topic";
        testClient.subscribe(topic, 2);
        String payload = "Hello, MQTT!";
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);
        testClient.publish(topic, message);
        //use a callback to capture the message
        testClient.setCallback(new MqttCallback()
        {
            @Override
            public void disconnected(MqttDisconnectResponse mqttDisconnectResponse)
            {
            }


            @Override
            public void mqttErrorOccurred(MqttException e)
            {
                fail("Connection lost unexpectedly");
            }


            @Override
            public void messageArrived(String topic, MqttMessage message)
            {
                assertEquals(payload, new String(message.getPayload()), "Payload should match");
                assertEquals(1, message.getQos(), "QoS should match");
            }


            @Override
            public void deliveryComplete(IMqttToken iMqttToken)
            {
            }


            @Override
            public void connectComplete(boolean b, String s)
            {
            }


            @Override
            public void authPacketArrived(int i, MqttProperties mqttProperties)
            {
            }
        });
    }


    @Test
    void testQoS2MessagePersistence() throws Exception
    {
        // Connect and publish with QoS 2
        testClient.connect();
        String topic = "qos2/topic";
        String payload = "Persistent Message";
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(2);
        testClient.publish(topic, message);
        // Simulate client disconnect and reconnect
        testClient.disconnect();
        testClient.connect();
        // Verify message is redelivered
        testClient.setCallback(new MqttCallback()
        {
            @Override
            public void disconnected(MqttDisconnectResponse mqttDisconnectResponse)
            {
            }


            @Override
            public void mqttErrorOccurred(MqttException e)
            {
                fail("Connection lost unexpectedly");
            }


            @Override
            public void messageArrived(String topic, MqttMessage message)
            {
                assertEquals(payload, new String(message.getPayload()), "Payload should match after reconnect");
            }


            @Override
            public void deliveryComplete(IMqttToken iMqttToken)
            {
            }


            @Override
            public void connectComplete(boolean b, String s)
            {
            }


            @Override
            public void authPacketArrived(int i, MqttProperties mqttProperties)
            {
            }
        });
        // Re-subscribe and confirm message redelivery
        testClient.subscribe(topic, 2);
    }
}
