package io.github.orionlibs.orion_digital_twin.broker;

import java.io.Closeable;
import java.io.IOException;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

public class MQTTBrokerClient implements Closeable
{
    private MqttAsyncClient subscriberClient;
    private boolean isConnected;


    public MQTTBrokerClient(String mqttAddressToConnectTo, String mqttClientID) throws MqttException
    {
        subscriberClient = new MqttAsyncClient(mqttAddressToConnectTo, mqttClientID, new MemoryPersistence());
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setAutomaticReconnect(true);
        //options.setCleanStart(true);
        options.setConnectionTimeout(10);
        IMqttToken connectionStatus = subscriberClient.connect(options);
        connectionStatus.waitForCompletion();
        System.out.println("-----------------" + connectionStatus.getClient().getClientId());
        isConnected = true;
        subscriberClient.setCallback(new MqttCallback()
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
                System.out.println("message: " + payload);
                //dataQueue.add(new DataRecord(payload));
            }


            @Override
            public void deliveryComplete(IMqttToken iMqttToken)
            {
                System.out.println("deliveryComplete");
            }


            @Override
            public void connectComplete(boolean b, String s)
            {
                System.out.println("connectComplete");
            }


            @Override
            public void authPacketArrived(int i, MqttProperties mqttProperties)
            {
                System.out.println("authPacketArrived");
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try
            {
                if(subscriberClient.isConnected())
                {
                    subscriberClient.disconnect().waitForCompletion();
                    subscriberClient.close();
                }
            }
            catch(MqttException e)
            {
                e.printStackTrace();
            }
        }));
    }


    public void subscribe(String topic) throws MQTTClientNotRunningException, MqttException
    {
        if(subscriberClient != null && isConnected())
        {
            subscriberClient.subscribe(topic, 2);
        }
        else
        {
            throw new MQTTClientNotRunningException();
        }
    }


    public void unsubscribe(String topic) throws MQTTClientNotRunningException, MqttException
    {
        if(subscriberClient != null && isConnected())
        {
            subscriberClient.unsubscribe(topic);
        }
        else
        {
            throw new MQTTClientNotRunningException();
        }
    }


    public void publish(String topic, MqttMessage message) throws MQTTClientNotRunningException, MqttException
    {
        if(subscriberClient != null && isConnected())
        {
            subscriberClient.publish(topic, message);
        }
        else
        {
            throw new MQTTClientNotRunningException();
        }
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
