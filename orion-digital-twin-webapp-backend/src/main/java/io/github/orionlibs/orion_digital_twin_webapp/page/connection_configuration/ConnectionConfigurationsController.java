package io.github.orionlibs.orion_digital_twin_webapp.page.connection_configuration;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import io.github.orionlibs.orion_digital_twin.broker.client.ConnectorFactory;
import io.github.orionlibs.orion_digital_twin.broker.client.RunningMQTTClients;
import io.github.orionlibs.orion_digital_twin.broker.server.MQTTBrokerServer;
import io.github.orionlibs.orion_digital_twin.database.Databases;
import io.github.orionlibs.orion_digital_twin.device_details.ConnectionConfigurationModel;
import io.github.orionlibs.orion_digital_twin.device_details.ConnectionConfigurationsDAO;
import io.github.orionlibs.orion_digital_twin_webapp.page.data_explorer.topic_explorer.TestMQTTPublisherTask;
import io.github.orionlibs.orion_digital_twin_webapp.page.data_explorer.topic_explorer.TopicExplorerResponseBean;
import io.github.orionlibs.orion_object.UUIDSecurityService;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/wapi/v1/connection-configurations")
public class ConnectionConfigurationsController
{
    private static Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private TaskScheduler taskScheduler;


    @GetMapping(value = "/summaries")
    public ResponseEntity<ConnectionConfigurationsSummariesResponseBean> connectionConfigurationsPageGetSummaries()
    {
        List<ConnectionConfigurationModel> configurations = ConnectionConfigurationsDAO.getAllWithAscendingOrder(Databases.connectionConfigurationID);
        List<ConnectionConfigurationsSummariesResponseBean.ConnectionConfigurationModel> connectionConfigurationsTemp = new ArrayList<>();
        for(ConnectionConfigurationModel config : configurations)
        {
            connectionConfigurationsTemp.add(ConnectionConfigurationsSummariesResponseBean.ConnectionConfigurationModel.builder()
                            .connectionConfigurationID(config.getConnectionConfigurationID())
                            .dataSourceId(config.getDataSourceId())
                            .dataSourceType(config.getDataSourceType())
                            .topic(config.getTopic())
                            .clientId(config.getClientId())
                            .build());
        }
        return ResponseEntity.ok(ConnectionConfigurationsSummariesResponseBean.builder()
                        .connectionConfigurations(connectionConfigurationsTemp)
                        .build());
    }


    @GetMapping(value = "/details/{connectionConfigurationID}")
    public ResponseEntity<ConnectionConfigurationDetailsResponseBean> connectionConfigurationsPageGetSummaries(@PathVariable Long connectionConfigurationID)
    {
        ConnectionConfigurationModel configuration = ConnectionConfigurationsDAO.getByID(connectionConfigurationID);
        return ResponseEntity.ok(ConnectionConfigurationDetailsResponseBean.builder()
                        .connectionConfiguration(ConnectionConfigurationDetailsResponseBean.ConnectionConfigurationModel.builder()
                                        .connectionConfigurationID(configuration.getConnectionConfigurationID())
                                        .dataSourceId(configuration.getDataSourceId())
                                        .dataSourceType(configuration.getDataSourceType())
                                        .apiUrl(configuration.getApiUrl())
                                        .apiKey(configuration.getApiKey())
                                        .httpMethod(configuration.getHttpMethod())
                                        .brokerUrl(configuration.getBrokerUrl())
                                        .brokerPort(configuration.getBrokerPort())
                                        .topic(configuration.getTopic())
                                        .clientId(configuration.getClientId())
                                        .username(configuration.getUsername())
                                        .password(configuration.getPassword())
                                        .build())
                        .build());
    }


    @GetMapping(value = "/MQTT/servers/starts/{connectionConfigurationID}")
    public ResponseEntity<ConnectionConfigurationsResponseBean> connectionConfigurationsPageStartMQTTBrokerServer(@PathVariable Long connectionConfigurationID)
    {
        MQTTBrokerServer brokerServer = new MQTTBrokerServer();
        try
        {
            brokerServer.startBroker(false, false);
            return ResponseEntity.ok(ConnectionConfigurationsResponseBean.builder()
                            .mqttServerStartStatus("server started")
                            .build());
        }
        catch(ExecutionException e)
        {
            return ResponseEntity.badRequest().body(ConnectionConfigurationsResponseBean.builder()
                            .mqttServerStartStatus(e.getMessage())
                            .build());
        }
        catch(InterruptedException e)
        {
            return ResponseEntity.badRequest().body(ConnectionConfigurationsResponseBean.builder()
                            .mqttServerStartStatus(e.getMessage())
                            .build());
        }
        catch(URISyntaxException e)
        {
            return ResponseEntity.badRequest().body(ConnectionConfigurationsResponseBean.builder()
                            .mqttServerStartStatus(e.getMessage())
                            .build());
        }
    }


    @GetMapping(value = "/MQTT/connections/{connectionConfigurationID}")
    public ResponseEntity<ConnectionConfigurationsResponseBean> connectionConfigurationsPageConnectClientToMQTTBrokerServer(@PathVariable Long connectionConfigurationID)
    {
        ConnectionConfigurationModel config = ConnectionConfigurationsDAO.getByID(connectionConfigurationID);
        Mqtt5AsyncClient client = new ConnectorFactory().newAsynchronousMQTTConnectorForSubscriber(config.getBrokerUrl(), Integer.parseInt(config.getBrokerPort()), config.getTopic(), MqttQos.fromCode(config.getQualityOfServiceLevel()), config.getClientId()).getClient();
        String clientIDGenerated = UUIDSecurityService.generateUUIDWithoutHyphens();
        RunningMQTTClients.idToClientMapper.put(clientIDGenerated, client);
        client.publishes(MqttGlobalPublishFilter.SUBSCRIBED, publish -> {
            TopicExplorerResponseBean responseBean = TopicExplorerResponseBean.builder()
                            .mqttMessageArrived(new String(publish.getPayloadAsBytes(), UTF_8))
                            .build();
            this.messagingTemplate.convertAndSend("/mqtt/iot-devices-live/summaries", responseBean);
        });
        return ResponseEntity.ok(ConnectionConfigurationsResponseBean.builder()
                        .clientConnectionToMQTTServerStatus("client " + clientIDGenerated + " connected")
                        .build());
    }


    @GetMapping(value = "/MQTT/disconnections/{clientIDGenerated}")
    public ResponseEntity<ConnectionConfigurationsResponseBean> connectionConfigurationsPageDisconnectClientFromMQTTBrokerServer(@PathVariable String clientIDGenerated)
    {
        if(RunningMQTTClients.idToClientMapper.get(clientIDGenerated) != null && RunningMQTTClients.idToClientMapper.get(clientIDGenerated).getConfig().getState().isConnectedOrReconnect())
        {
            RunningMQTTClients.idToClientMapper.get(clientIDGenerated).disconnect();
            RunningMQTTClients.idToClientMapper.remove(clientIDGenerated);
        }
        return ResponseEntity.ok(ConnectionConfigurationsResponseBean.builder()
                        .clientConnectionToMQTTServerStatus("client disconnected")
                        .build());
    }


    @GetMapping(value = "/MQTT/scheduled-test-publishes")
    public ResponseEntity<ConnectionConfigurationsResponseBean> TESTconnectionConfigurationsPageScheduleTestPublishesToMQTTBrokerServer()
    {
        ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(new TestMQTTPublisherTask(), 2000);
        scheduledTasks.put("TestMQTTPublisherTask", future);
        return ResponseEntity.ok(ConnectionConfigurationsResponseBean.builder()
                        .hasErrors(Boolean.FALSE)
                        .build());
    }


    @DeleteMapping(value = "/MQTT/scheduled-test-publishes")
    public ResponseEntity<ConnectionConfigurationsResponseBean> TESTconnectionConfigurationsPageStopScheduledTestPublishesToMQTTBrokerServer()
    {
        ScheduledFuture<?> future = scheduledTasks.get("TestMQTTPublisherTask");
        if(future != null)
        {
            future.cancel(true);
            scheduledTasks.remove("TestMQTTPublisherTask");
        }
        return ResponseEntity.ok(ConnectionConfigurationsResponseBean.builder()
                        .hasErrors(Boolean.FALSE)
                        .build());
    }
}
