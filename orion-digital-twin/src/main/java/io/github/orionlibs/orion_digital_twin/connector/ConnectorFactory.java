package io.github.orionlibs.orion_digital_twin.connector;

public class ConnectorFactory
{
    public static HttpApiConnector newHttpApiConnector(String apiUrl, String apiKey, String method)
    {
        return new HttpApiConnector(apiUrl, apiKey, method);
    }


    public static MqttConnector newMqttConnector(String brokerUrl, String topic, String clientId, String username, String password)
    {
        return new MqttConnector(brokerUrl, topic, clientId, username, password);
    }
}
