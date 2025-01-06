package io.github.orionlibs.orion_digital_twin.broker.client;

import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

public class ConnectorFactory
{
    public static HTTPAPIConnector newHTTPAPIConnector(String apiUrl, String apiKey, String method)
    {
        return new HTTPAPIConnector(apiUrl, apiKey, method);
    }


    public static Mqtt5BlockingClient newBlockingMQTTConnector(String brokerUrl, int port, String clientId)
    {
        Mqtt5BlockingClient testClient = Mqtt5Client.builder()
                        .identifier(clientId)
                        .serverHost("0.0.0.0")
                        .serverPort(1883)
                        .buildBlocking();
        testClient.connect();
        testClient.toAsync();
        return testClient;
    }


    public static Mqtt5AsyncClient newAsynchronousMQTTConnector(String brokerUrl, int port, String clientId)
    {
        Mqtt5BlockingClient testClient = Mqtt5Client.builder()
                        .identifier(clientId)
                        .serverHost("0.0.0.0")
                        .serverPort(1883)
                        .buildBlocking();
        testClient.connect();
        return testClient.toAsync();
    }
}
