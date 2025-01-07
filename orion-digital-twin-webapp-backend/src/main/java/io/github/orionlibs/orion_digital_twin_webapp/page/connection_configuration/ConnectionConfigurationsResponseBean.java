package io.github.orionlibs.orion_digital_twin_webapp.page.connection_configuration;

import io.github.orionlibs.orion_digital_twin_webapp.OrionResponse;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class ConnectionConfigurationsResponseBean extends OrionResponse
{
    private String mqttServerStartStatus;
    private String clientConnectionToMQTTServerStatus;
}