package io.github.orionlibs.orion_digital_twin.broker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class MQTTBrokerClient2 implements Runnable
{
    private final Socket clientSocket;
    private final ConcurrentHashMap<String, MQTTBrokerClient2> clients;
    private String clientId;


    public MQTTBrokerClient2(Socket clientSocket, ConcurrentHashMap<String, MQTTBrokerClient2> clients)
    {
        this.clientSocket = clientSocket;
        this.clients = clients;
    }


    @Override
    public void run()
    {
        try(InputStream input = clientSocket.getInputStream();
                        OutputStream output = clientSocket.getOutputStream())
        {
            // Handle MQTT packets (CONNECT, PUBLISH, SUBSCRIBE, etc.)
            while(!clientSocket.isClosed())
            {
                byte[] buffer = new byte[1024];
                int read = input.read(buffer);
                if(read == -1)
                {
                    break;
                }
                // Parse MQTT control packet
                byte packetType = (byte)((buffer[0] >> 4) & 0x0F);
                switch(packetType)
                {
                    case 1: // CONNECT
                        handleConnect(buffer, output);
                        break;
                    case 3: // PUBLISH
                        handlePublish(buffer);
                        break;
                    case 8: // SUBSCRIBE
                        handleSubscribe(buffer, output);
                        break;
                    default:
                        System.out.println("Unsupported packet type: " + packetType);
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            disconnect();
        }
    }


    private void handleConnect(byte[] buffer, OutputStream output) throws IOException
    {
        // Extract clientId from CONNECT packet
        clientId = "testClient"; // Extract actual clientId from buffer
        clients.put(clientId, this);
        // Send CONNACK
        byte[] connack = new byte[] {0x20, 0x02, 0x00, 0x00}; // CONNACK with success
        output.write(connack);
    }


    private void handlePublish(byte[] buffer)
    {
        // Parse topic and message from PUBLISH packet
        String topic = "test/topic"; // Extract topic from buffer
        String message = "testMessage"; // Extract message from buffer
        System.out.println("Message received on topic " + topic + ": " + message);
        // Forward message to subscribers
        clients.values().forEach(client -> client.forwardMessage(topic, message));
    }


    private void handleSubscribe(byte[] buffer, OutputStream output) throws IOException
    {
        // Parse subscription request
        String topic = "test/topic"; // Extract topic from buffer
        System.out.println(clientId + " subscribed to " + topic);
        // Send SUBACK
        byte[] suback = new byte[] {(byte)0x90, 0x03, 0x00, 0x01, 0x00}; // SUBACK with QoS 0
        output.write(suback);
    }


    private void forwardMessage(String topic, String message)
    {
        try
        {
            OutputStream output = clientSocket.getOutputStream();
            byte[] publishPacket = buildPublishPacket(topic, message);
            output.write(publishPacket);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }


    private byte[] buildPublishPacket(String topic, String message)
    {
        // Build a simple PUBLISH packet
        byte[] topicBytes = topic.getBytes();
        byte[] messageBytes = message.getBytes();
        int packetLength = 2 + topicBytes.length + messageBytes.length;
        byte[] packet = new byte[packetLength + 2];
        packet[0] = 0x30; // PUBLISH packet type
        packet[1] = (byte)packetLength;
        packet[2] = (byte)((topicBytes.length >> 8) & 0xFF);
        packet[3] = (byte)(topicBytes.length & 0xFF);
        System.arraycopy(topicBytes, 0, packet, 4, topicBytes.length);
        System.arraycopy(messageBytes, 0, packet, 4 + topicBytes.length, messageBytes.length);
        return packet;
    }


    private void disconnect()
    {
        if(clientId != null)
        {
            clients.remove(clientId);
            System.out.println(clientId + " disconnected.");
        }
    }
}
