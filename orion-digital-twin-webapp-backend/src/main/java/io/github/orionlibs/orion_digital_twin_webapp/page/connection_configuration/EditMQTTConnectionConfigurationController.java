package io.github.orionlibs.orion_digital_twin_webapp.page.connection_configuration;

import io.github.orionlibs.orion_digital_twin.device_details.ConnectionConfigurationModel;
import io.github.orionlibs.orion_digital_twin.device_details.ConnectionConfigurationsDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/wapi/v1/connection-configurations/MQTT")
public class EditMQTTConnectionConfigurationController
{
    @PutMapping(consumes = "application/json")
    public ResponseEntity<EditConnectionConfigurationResponseBean> connectionConfigurationsPageEditMQTTConnectionConfiguration(@RequestBody EditMQTTConnectionConfigurationRequestBean requestBean)
    {
        try
        {
            ConnectionConfigurationModel model = ConnectionConfigurationsDAO.getByID(requestBean.getConnectionConfigurationID());
            model.setDataSourceId(requestBean.getDataSourceId());
            model.setDataSourceType(requestBean.getDataSourceType());
            //model.setApiUrl(requestBean.getApiUrl());
            //model.setApiKey(requestBean.getApiKey());
            //model.setHttpMethod(requestBean.getHttpMethod());
            model.setBrokerUrl(requestBean.getBrokerUrl());
            model.setBrokerPort(requestBean.getBrokerPort());
            model.setTopic(requestBean.getTopic());
            model.setClientId(requestBean.getClientId());
            model.setQualityOfServiceLevel(requestBean.getQualityOfServiceLevel());
            //model.setUsername(requestBean.getUsername());
            //model.setPassword(requestBean.getPassword());
            ConnectionConfigurationsDAO.update(model);
            return ResponseEntity.ok(EditConnectionConfigurationResponseBean.builder().build());
        }
        catch(Throwable e)
        {
            return ResponseEntity.badRequest().build();
        }
    }
}
