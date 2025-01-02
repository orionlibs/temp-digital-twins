package io.github.orionlibs.orion_digital_twin.connector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

public class MqttConnector implements Connector
{
    private final String brokerUrl;
    private final String topic;
    private final String clientId;
    private final String username;
    private final String password;
    private MqttAsyncClient client;
    private final ConcurrentLinkedQueue<DataRecord> dataQueue;


    public MqttConnector(String brokerUrl, String topic, String clientId, String username, String password)
    {
        this.brokerUrl = brokerUrl;
        this.topic = topic;
        this.clientId = clientId;
        this.username = username;
        this.password = password;
        this.dataQueue = new ConcurrentLinkedQueue<>();
    }


    protected ConcurrentLinkedQueue<DataRecord> getDataQueue()
    {
        return dataQueue;
    }


    protected void setClient(MqttAsyncClient client)
    {
        this.client = client;
    }


    @Override
    public void connect() throws MqttException
    {
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setUserName(username);
        options.setPassword(password.getBytes());
        options.setCleanStart(true);
        if(client == null)
        {
            client = new MqttAsyncClient(brokerUrl, clientId);
            client.setCallback(new MqttCallback()
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
                    dataQueue.add(new DataRecord(payload));
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
        client.connect(options).waitForCompletion();
        client.subscribe(topic, 1);
    }


    @Override
    public List<DataRecord> getData() throws Exception
    {
        List<DataRecord> records = new ArrayList<>();
        while(!dataQueue.isEmpty())
        {
            records.add(dataQueue.poll());
        }
        return records;
    }


    @Override
    public void disconnect() throws MqttException
    {
        if(client != null)
        {
            if(client.isConnected())
            {
                client.disconnect().waitForCompletion();
            }
            client.close();
        }
    }
}
