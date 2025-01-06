package io.github.orionlibs.orion_digital_twin.broker.client;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import io.github.orionlibs.orion_digital_twin.ATest;
import io.github.orionlibs.orion_digital_twin.Utils;
import io.github.orionlibs.orion_digital_twin.broker.server.MQTTBrokerServer;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
//@Execution(ExecutionMode.CONCURRENT)
public class MQTTBrokerServerTest extends ATest
{
    private MQTTBrokerServer brokerServer;
    private Mqtt5AsyncClient testPublisherClient;
    private Mqtt5AsyncClient testSubscriberClient;
    private Mqtt5AsyncClient testUnsubscriberClient;
    //private Mqtt5RxClient testClient;
    //private Mqtt5BlockingClient testClient;
    private String clientID = "testClientId";


    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException, URISyntaxException
    {
        resetAndSeedDatabase();
        brokerServer = new MQTTBrokerServer();
        brokerServer.startBroker();
        Utils.nonblockingDelay(3);
    }


    @AfterEach
    void teardown()
    {
        if(testPublisherClient != null && testPublisherClient.getConfig().getState().isConnectedOrReconnect())
        {
            testPublisherClient.disconnect();
        }
        if(testSubscriberClient != null && testSubscriberClient.getConfig().getState().isConnectedOrReconnect())
        {
            testSubscriberClient.disconnect();
        }
        if(testUnsubscriberClient != null && testUnsubscriberClient.getConfig().getState().isConnectedOrReconnect())
        {
            testUnsubscriberClient.disconnect();
        }
        brokerServer.stopBroker();
    }


    @Test
    void testBrokerStartup()
    {
        assertTrue(brokerServer.isRunning(), "Broker should be running after startup");
    }


    @Test
    void testPublishAndSubscribeAndUnsubscribeAndPersistenceAfterMQTTServerShutdown()
    {
        startSubscriberClient("test/topic2", clientID);
        Utils.nonblockingDelay(5);
        startPublisherClient("test/topic2", "somePayload3", clientID);
        Utils.nonblockingDelay(20);
        //startUnsubscriberClient("test/topic2", clientID);
        //Utils.nonblockingDelay(3);
        //assertEquals(0, TopicSubscribersDAO.getNumberOfRecords());

        /*Single<Mqtt5SubAck> subAckSingle1 =
                        testClient.subscribeWith().topicFilter("test/topic1").applySubscribe()
                                        .doOnSuccess(mqtt5SubAck -> {
                                            System.out.println("Successfully subscribed!");
                                        }).doOnError(throwable -> {
                                            System.out.println("Error while subscribing!");
                                            throwable.printStackTrace();
                                        });
        Single<Mqtt5SubAck> subAckSingle2 =
                        testClient.subscribeWith().topicFilter("test/topic2").applySubscribe()
                                        .doOnSuccess(mqtt5SubAck -> {
                                            System.out.println("Successfully subscribed!");
                                        }).doOnError(throwable -> {
                                            System.out.println("Error while subscribing!");
                                            throwable.printStackTrace();
                                        });*/

        /*testClient.subscribe(Mqtt5Subscribe.builder()
                        .addSubscription(Mqtt5Subscription.builder().topicFilter("test/topic1").qos(MqttQos.EXACTLY_ONCE).build())
                        .build());*/
        /*testClient.subscribe(Mqtt5Subscribe.builder()
                        .addSubscription(Mqtt5Subscription.builder().topicFilter("test/topic2").qos(MqttQos.EXACTLY_ONCE).build())
                        .build());*/
        //assertEquals(2, TopicSubscribersDAO.getNumberOfRecords());
        //assertEquals(0, DataPacketsDAO.getNumberOfRecords());
        /*testClient.publish(Mqtt5WillPublish.builder()
                        .topic("test/topic1")
                        .qos(MqttQos.EXACTLY_ONCE)
                        .payload("somePayload1".getBytes())
                        .retain(true)
                        .build());
        assertEquals(1, DataPacketsDAO.getNumberOfRecords());
        try(final Mqtt5BlockingClient.Mqtt5Publishes publishes = testClient.publishes(MqttGlobalPublishFilter.ALL))
        {
            Optional<Mqtt5Publish> message = publishes.receive(4, TimeUnit.SECONDS);
            if(message.isPresent())
            {
                System.out.println("------" + new String(message.get().getPayloadAsBytes()));
            }
        }*/
        /*testClient.unsubscribe(Mqtt5Unsubscribe.builder()
                        .topicFilter("test/topic1")
                        .build());*/
        //assertEquals(1, TopicSubscribersDAO.getNumberOfRecords());
    }


    private void startPublisherClient(String topic, String payload, String clientId)
    {
        //this.testPublisherClient = ConnectorFactory.newAsynchronousMQTTConnectorForPublisher("broker.hivemq.com", 1883, topic, payload, clientId);
        this.testPublisherClient = ConnectorFactory.newAsynchronousMQTTConnectorForPublisher("0.0.0.0", 1883, topic, payload, clientId);
        //this.testClient = ConnectorFactory.newReactiveMQTTConnector("0.0.0.0", 1883, clientId);
        //this.testClient = ConnectorFactory.newBlockingMQTTConnector("0.0.0.0", 1883, clientId);
    }


    private void startSubscriberClient(String topic, String clientId)
    {
        //this.testSubscriberClient = ConnectorFactory.newAsynchronousMQTTConnectorForSubscriber("broker.hivemq.com", 1883, topic, clientId);
        this.testSubscriberClient = ConnectorFactory.newAsynchronousMQTTConnectorForSubscriber("0.0.0.0", 1883, topic, clientId);
        //this.testClient = ConnectorFactory.newReactiveMQTTConnector("0.0.0.0", 1883, clientId);
        //this.testClient = ConnectorFactory.newBlockingMQTTConnector("0.0.0.0", 1883, clientId);
    }


    private void startUnsubscriberClient(String topic, String clientId)
    {
        //this.testSubscriberClient = ConnectorFactory.newAsynchronousMQTTConnectorForSubscriber("broker.hivemq.com", 1883, topic, clientId);
        this.testUnsubscriberClient = ConnectorFactory.newAsynchronousMQTTConnectorForUnsubscriber("0.0.0.0", 1883, topic, clientId);
        //this.testClient = ConnectorFactory.newReactiveMQTTConnector("0.0.0.0", 1883, clientId);
        //this.testClient = ConnectorFactory.newBlockingMQTTConnector("0.0.0.0", 1883, clientId);
    }
}
