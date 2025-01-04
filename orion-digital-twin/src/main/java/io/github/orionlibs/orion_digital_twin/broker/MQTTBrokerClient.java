package io.github.orionlibs.orion_digital_twin.broker;

import java.io.Closeable;
import java.io.IOException;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttException;

public class MQTTBrokerClient implements Closeable
{
    private MqttClient subscriberClient;
    private boolean isConnected;


    public MQTTBrokerClient(String mqttAddressToConnectTo, String mqttClientID) throws MqttException
    {
        subscriberClient = new MqttClient(mqttAddressToConnectTo, mqttClientID);
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setAutomaticReconnect(true);
        options.setCleanStart(true);
        options.setConnectionTimeout(10);
        subscriberClient.connect(options);
        isConnected = true;
    }


    public MQTTBrokerClient(String topicToSubscribeTo, int qualityOfServiceLevel, String mqttAddressToConnectTo, String mqttClientID) throws MqttException
    {
        subscriberClient = new MqttClient(mqttAddressToConnectTo, mqttClientID);
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setAutomaticReconnect(true);
        options.setCleanStart(true);
        options.setConnectionTimeout(10);
        subscriberClient.connect(options);
        isConnected = true;
        subscriberClient.subscribe(topicToSubscribeTo, qualityOfServiceLevel);
    }


    private void stopClient() throws MqttException
    {
        if(subscriberClient.isConnected())
        {
            subscriberClient.disconnect();
            subscriberClient.close();
            isConnected = false;
        }
    }


    @Override
    public void close() throws IOException
    {
        try
        {
            stopClient();
        }
        catch(MqttException e)
        {
            throw new IOException(e);
        }
    }


    public boolean isConnected()
    {
        return isConnected;
    }
}
