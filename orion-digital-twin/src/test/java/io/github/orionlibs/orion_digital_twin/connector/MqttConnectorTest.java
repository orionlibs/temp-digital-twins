package io.github.orionlibs.orion_digital_twin.connector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.orionlibs.orion_digital_twin.ATest;
import java.util.List;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.ArgumentCaptor;

@TestInstance(Lifecycle.PER_CLASS)
//@Execution(ExecutionMode.CONCURRENT)
public class MqttConnectorTest extends ATest
{
    private MqttAsyncClient mockClient;
    private IMqttToken mockMqttToken;
    private MqttConnector connector;
    private final String brokerUrl = "tcp://mock-broker:1883";
    private final String topic = "test/topic";
    private final String clientId = "test-client";
    private final String username = "user";
    private final String password = "pass";


    @BeforeEach
    void setUp() throws MqttException
    {
        mockClient = mock(MqttAsyncClient.class);
        mockMqttToken = mock(IMqttToken.class);
        connector = new MqttConnector(brokerUrl, topic, clientId, username, password);
        mockClient.setCallback(new MqttCallback()
        {
            @Override
            public void disconnected(MqttDisconnectResponse mqttDisconnectResponse)
            {
                System.out.println("Disconnected from MQTT broker.");
            }


            @Override
            public void mqttErrorOccurred(MqttException e)
            {
                System.err.println("MQTT error occurred: " + e.getMessage());
            }


            @Override
            public void messageArrived(String topic, MqttMessage message)
            {
                String payload = new String(message.getPayload());
                connector.getDataQueue().add(new DataRecord(payload));
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
        connector.setClient(mockClient);
        when(mockClient.connect(any(MqttConnectionOptions.class))).thenReturn(mockMqttToken);
        doNothing().when(mockMqttToken).waitForCompletion();
    }


    @Test
    void testConnect() throws Exception
    {
        connector.connect();
        connector.disconnect();
        verify(mockClient, times(1)).connect(any(MqttConnectionOptions.class));
        verify(mockClient, times(1)).subscribe(eq(topic), eq(1));
        verify(mockClient, times(1)).close();
    }


    @Test
    void testGetData() throws Exception
    {
        MqttCallback callback = captureCallback();
        MqttMessage mockMessage = new MqttMessage("test message".getBytes());
        callback.messageArrived(topic, mockMessage);
        List<DataRecord> records = connector.getData();
        assertEquals(1, records.size());
        assertEquals("test message", records.get(0).getRawData());
    }


    @Test
    void testHandleMultipleMessages() throws Exception
    {
        MqttCallback callback = captureCallback();
        callback.messageArrived(topic, new MqttMessage("message 1".getBytes()));
        callback.messageArrived(topic, new MqttMessage("message 2".getBytes()));
        callback.messageArrived(topic, new MqttMessage("message 3".getBytes()));
        List<DataRecord> records = connector.getData();
        assertEquals(3, records.size());
        assertEquals("message 1", records.get(0).getRawData());
        assertEquals("message 2", records.get(1).getRawData());
        assertEquals("message 3", records.get(2).getRawData());
    }


    private MqttCallback captureCallback() throws Exception
    {
        ArgumentCaptor<MqttCallback> callbackCaptor = ArgumentCaptor.forClass(MqttCallback.class);
        verify(mockClient).setCallback(callbackCaptor.capture());
        doNothing().when(mockClient).setCallback(callbackCaptor.capture());
        connector.connect();
        return callbackCaptor.getValue();
    }
}
