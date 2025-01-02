package io.github.orionlibs.orion_digital_twin_webapp.page.connection_configuration;

import io.github.orionlibs.orion_digital_twin.database.Databases;
import io.github.orionlibs.orion_digital_twin.device_details.ConnectionConfigurationModel;
import io.github.orionlibs.orion_digital_twin.device_details.ConnectionConfigurationsDAO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/wapi/v1/connection-configurations")
public class ConnectionConfigurationsController
{
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
                                        .topic(configuration.getTopic())
                                        .clientId(configuration.getClientId())
                                        .username(configuration.getUsername())
                                        .password(configuration.getPassword())
                                        .build())
                        .build());
    }
}
