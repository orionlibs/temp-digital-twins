package io.github.orionlibs.orion_digital_twin.connector;

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

public class ConnectorFactory
{
    public static HttpApiConnector newHttpApiConnector(String apiUrl, String apiKey, String method)
    {
        return new HttpApiConnector(apiUrl, apiKey, method);
    }


    public static Mqtt5BlockingClient newMqttConnector(String brokerUrl, int port, String clientId)
    {
        return Mqtt5Client.builder()
                        .identifier(clientId)
                        .serverHost(brokerUrl)
                        .serverPort(port)
                        .buildBlocking();
    }
}
