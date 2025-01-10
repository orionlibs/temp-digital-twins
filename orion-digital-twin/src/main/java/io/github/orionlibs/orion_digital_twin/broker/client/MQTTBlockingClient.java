package io.github.orionlibs.orion_digital_twin.broker.client;

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

public class MQTTBlockingClient
{
    private Mqtt5BlockingClient client;


    public MQTTBlockingClient(String brokerUrl, int port, String clientId)
    {
        Mqtt5BlockingClient client = Mqtt5Client.builder()
                        .identifier(clientId)
                        .serverHost(brokerUrl)
                        .serverPort(port)
                        .buildBlocking();
        client.connect();
    }


    public Mqtt5BlockingClient getClient()
    {
        return client;
    }
}
