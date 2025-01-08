package io.github.orionlibs.orion_digital_twin.broker.client;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.hivemq.client.internal.mqtt.datatypes.MqttUserPropertyImpl;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.datatypes.Mqtt5UserProperty;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.general.IterationCallback;
import com.hivemq.extension.sdk.api.services.general.IterationContext;
import com.hivemq.extension.sdk.api.services.subscription.SubscriberWithFilterResult;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ConnectorFactory
{
    public HTTPAPIConnector newHTTPAPIConnector(String apiUrl, String apiKey, String method)
    {
        return new HTTPAPIConnector(apiUrl, apiKey, method);
    }


    public Mqtt5BlockingClient newBlockingMQTTConnector(String brokerUrl, int port, String clientId)
    {
        Mqtt5BlockingClient client = Mqtt5Client.builder()
                        .identifier(clientId)
                        .serverHost(brokerUrl)
                        .serverPort(port)
                        .buildBlocking();
        client.connect();
        return client;
    }


    public Mqtt5AsyncClient newAsynchronousMQTTConnectorForPublisher(String brokerUrl, int port, String topic, String payload, String clientId)
    {
        Mqtt5AsyncClient client = Mqtt5Client.builder()
                        .identifier(clientId)
                        .serverHost(brokerUrl)
                        .serverPort(port)
                        .buildAsync();
        client.connect()
                        .thenCompose(connAck -> {
                            System.out.println("Successfully connected publisher!");
                            List<String> subscriberIDsForTopic = new ArrayList<>();
                            IterationCallback<SubscriberWithFilterResult> subscribersForTopic = new IterationCallback()
                            {
                                @Override
                                public void iterate(@NotNull IterationContext iterationContext, Object o)
                                {
                                    SubscriberWithFilterResult result = (SubscriberWithFilterResult)o;
                                    subscriberIDsForTopic.add(result.getClientId());
                                }
                            };
                            CompletableFuture<Void> searchResults = Services.subscriptionStore().iterateAllSubscribersWithTopicFilter(topic, subscribersForTopic);
                            List<Mqtt5UserProperty> subscribers = new ArrayList<>();
                            searchResults.thenRun(() -> {
                                subscriberIDsForTopic.forEach(subscriberID -> subscribers.add(MqttUserPropertyImpl.of("subscriberId", subscriberID)));
                            });
                            final Mqtt5Publish publish = Mqtt5Publish.builder()
                                            .topic(topic)
                                            .payload(payload.getBytes(UTF_8))
                                            .qos(MqttQos.EXACTLY_ONCE)
                                            .userProperties().addAll(subscribers).applyUserProperties()
                                            .build();
                            final var future = client.publish(publish);
                            return CompletableFuture.allOf(future);
                        }).thenCompose(unused -> {
                            System.out.println("Successfully published!");
                            return client.disconnect();
                        }).exceptionally(throwable -> {
                            System.out.println("Something went wrong publisher!");
                            return null;
                        });
        return client;
    }


    public Mqtt5AsyncClient newAsynchronousMQTTConnectorForSubscriber(String brokerUrl, int port, String topic, MqttQos qualityOfServiceLevel, String clientId)
    {
        Mqtt5AsyncClient client = Mqtt5Client.builder()
                        .identifier(clientId)
                        .serverHost(brokerUrl)
                        .serverPort(port)
                        .buildAsync();
        /*client.publishes(MqttGlobalPublishFilter.SUBSCRIBED, publish -> {
            System.out.println("Received publish with payload: " + new String(publish.getPayloadAsBytes(), UTF_8));
        });*/
        client.connect()
                        .thenCompose(connAck -> {
                            System.out.println("Successfully connected subscriber!");
                            return client.subscribeWith().topicFilter(topic).qos(qualityOfServiceLevel).send();
                        }).thenRun(() -> {
                            System.out.println("Successfully subscribed!");
                        }).exceptionally(throwable -> {
                            System.out.println("Something went wrong subscriber!");
                            return null;
                        });
        return client;
    }


    public Mqtt5AsyncClient newAsynchronousMQTTConnectorForUnsubscriber(String brokerUrl, int port, String topic, String clientId)
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
