package io.github.orionlibs.orion_digital_twin_webapp.page.connection_configuration;

import io.github.orionlibs.orion_digital_twin.device_details.ConnectionConfigurationModel;
import io.github.orionlibs.orion_digital_twin.device_details.ConnectionConfigurationsDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/wapi/v1/connection-configurations")
public class AddConnectionConfigurationController
{
    @PostMapping(consumes = "application/json")
    public ResponseEntity<AddConnectionConfigurationResponseBean> connectionConfigurationsPageAddConnectionConfiguration(@RequestBody AddConnectionConfigurationRequestBean requestBean)
    {
        try
        {
            ConnectionConfigurationsDAO.save(ConnectionConfigurationModel.builder()
                            .dataSourceId(requestBean.getDataSourceId())
                            .dataSourceType(requestBean.getDataSourceType())
                            .apiUrl(requestBean.getApiUrl())
                            .apiKey(requestBean.getApiKey())
                            .httpMethod(requestBean.getHttpMethod())
                            .brokerUrl(requestBean.getBrokerUrl())
                            .topic(requestBean.getTopic())
                            .clientId(requestBean.getClientId())
                            .username(requestBean.getUsername())
                            .password(requestBean.getPassword())
                            .build());
            return ResponseEntity.ok(AddConnectionConfigurationResponseBean.builder().build());
        }
        catch(Throwable e)
        {
            return ResponseEntity.badRequest().build();
        }
    }
}
