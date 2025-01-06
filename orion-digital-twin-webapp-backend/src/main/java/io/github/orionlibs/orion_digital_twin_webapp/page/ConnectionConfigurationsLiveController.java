package io.github.orionlibs.orion_digital_twin_webapp.page;

import io.github.orionlibs.orion_digital_twin.database.Databases;
import io.github.orionlibs.orion_digital_twin.device_details.ConnectionConfigurationModel;
import io.github.orionlibs.orion_digital_twin.device_details.ConnectionConfigurationsDAO;
import io.github.orionlibs.orion_digital_twin_webapp.page.connection_configuration.ConnectionConfigurationsSummariesResponseBean;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ConnectionConfigurationsLiveController
{
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    //@Scheduled(initialDelay = 5000, fixedRate = 3000)
    public void connectionConfigurationsPageGetSummariesLive()
    {
        try
        {
            List<ConnectionConfigurationModel> configurations = ConnectionConfigurationsDAO.getAllWithAscendingOrder(Databases.connectionConfigurationID);
            List<ConnectionConfigurationsSummariesResponseBean.ConnectionConfigurationModel> connectionConfigurationsTemp = new ArrayList<>();
            for(ConnectionConfigurationModel config : configurations)
            {
                connectionConfigurationsTemp.add(ConnectionConfigurationsSummariesResponseBean.ConnectionConfigurationModel.builder()
                                .dataSourceId(config.getDataSourceId())
                                .dataSourceType(config.getDataSourceType())
                                .topic(config.getTopic())
                                .clientId(config.getClientId())
                                .build());
            }
            ConnectionConfigurationsSummariesResponseBean responseBean = ConnectionConfigurationsSummariesResponseBean.builder()
                            .connectionConfigurations(connectionConfigurationsTemp)
                            .build();
            this.messagingTemplate.convertAndSend("/topic/connection-configurations-live/summaries", responseBean);
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
    }
}
