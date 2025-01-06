package io.github.orionlibs.orion_digital_twin.broker.client;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
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
        Mqtt5BlockingClient client = Mqtt5Client.builder()
                        .identifier(clientId)
                        .serverHost(brokerUrl)
                        .serverPort(port)
                        .buildBlocking();
        client.connect();
        return client;
    }


    public static Mqtt5AsyncClient newAsynchronousMQTTConnectorForPublisher(String brokerUrl, int port, String topic, String payload, String clientId)
    {
        Mqtt5AsyncClient client = Mqtt5Client.builder()
                        .identifier(clientId)
                        .serverHost(brokerUrl)
                        .serverPort(port)
                        .buildAsync();
        client.connect()
                        .thenAccept(connAck -> System.out.println("connected " + connAck))
                        .thenCompose(v -> client.publishWith().topic(topic).qos(MqttQos.EXACTLY_ONCE).send())
                        .thenAccept(publishResult -> System.out.println("published " + publishResult))
                        //.thenCompose(v -> client.disconnect())
                        .thenAccept(v -> System.out.println("disconnected"));
        /*client.connect();
        Mqtt5Publish publish = Mqtt5Publish.builder()
                        .topic(topic)
                        .payload(payload.getBytes(UTF_8))
                        .qos(MqttQos.EXACTLY_ONCE)
                        .build();
        final var future = client.publish(publish);
        CompletableFuture.allOf(future);*/
        /*client.connect()
                        .thenCompose(connAck -> {
                            System.out.println("Successfully connected publisher!");
                            final Mqtt5Publish publish = Mqtt5Publish.builder()
                                            .topic(topic)
                                            .payload(payload.getBytes(UTF_8))
                                            .qos(MqttQos.EXACTLY_ONCE)
                                            .build();
                            final var future = client.publish(publish);
                            return CompletableFuture.allOf(future);
                        }).thenCompose(unused -> {
                            System.out.println("Successfully published publisher!");
                            return client.disconnect();
                        }).exceptionally(throwable -> {
                            System.out.println("Something went wrong publisher!");
                            return null;
                        });*/
        return client;
    }


    public static Mqtt5AsyncClient newAsynchronousMQTTConnectorForSubscriber(String brokerUrl, int port, String topic, String clientId)
    {
        Mqtt5AsyncClient client = Mqtt5Client.builder()
                        .identifier(clientId)
                        .serverHost(brokerUrl)
                        .serverPort(port)
                        .buildAsync();
        client.publishes(MqttGlobalPublishFilter.SUBSCRIBED, publish -> {
            System.out.println("Received publish with payload: " + new String(publish.getPayloadAsBytes(), UTF_8));
            //publish.acknowledge();
        });
        client.connect()
                        .thenAccept(connAck -> System.out.println("connected subscriber" + connAck))
                        .thenCompose(v -> client.subscribeWith().topicFilter(topic).qos(MqttQos.EXACTLY_ONCE).send())
                        .thenAccept(publishResult -> System.out.println("published " + publishResult));
        //.thenCompose(v -> client.disconnect())
        //.thenAccept(v -> System.out.println("disconnected"));




        /*client.connect()
                        .thenCompose(connAck -> {
                            System.out.println("Successfully connected subscriber!");
                            return client.subscribeWith().topicFilter(topic).send();
                        }).thenRun(() -> {
                            System.out.println("Successfully subscribed!");
                        }).exceptionally(throwable -> {
                            System.out.println("Something went wrong subscriber!");
                            return null;
                        });*/
        return client;
    }


    public static Mqtt5AsyncClient newAsynchronousMQTTConnectorForUnsubscriber(String brokerUrl, int port, String topic, String clientId)
    {
        Mqtt5AsyncClient client = Mqtt5Client.builder()
                        .identifier(clientId)
                        .serverHost(brokerUrl)
                        .serverPort(port)
                        .buildAsync();
        client.connect()
                        .thenCompose(connAck -> {
                            System.out.println("Successfully connected unsubscriber!");
                            return client.unsubscribeWith().topicFilter(topic).send();
                        }).thenRun(() -> {
                            System.out.println("Successfully unsubscribed!");
                        }).exceptionally(throwable -> {
                            System.out.println("Something went wrong unsubscriber!");
                            return null;
                        });
        return client;
    }
}
