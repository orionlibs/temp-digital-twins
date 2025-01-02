package io.github.orionlibs.orion_digital_twin_webapp.page.connection_configuration;

import io.github.orionlibs.orion_digital_twin.device_details.ConnectionConfigurationModel;
import io.github.orionlibs.orion_digital_twin.device_details.ConnectionConfigurationsDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/wapi/v1/connection-configurations")
public class DeleteConnectionConfigurationsController
{
    @DeleteMapping()
    public ResponseEntity<ConnectionConfigurationsSummariesResponseBean> connectionConfigurationsPageDeleteConnectionConfiguration(@RequestParam(name = "connectionConfigurationID", required = true) Long connectionConfigurationID)
    {
        ConnectionConfigurationModel configuration = ConnectionConfigurationsDAO.getByID(connectionConfigurationID);
        ConnectionConfigurationsDAO.delete(configuration);
        return ResponseEntity.ok(ConnectionConfigurationsSummariesResponseBean.builder()
                        .hasErrors(false)
                        .build());
    }
}
