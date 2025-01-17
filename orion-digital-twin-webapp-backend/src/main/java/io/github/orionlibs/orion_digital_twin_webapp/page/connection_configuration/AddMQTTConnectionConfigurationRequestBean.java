package io.github.orionlibs.orion_digital_twin_webapp.page.connection_configuration;

import io.github.orionlibs.orion_digital_twin_webapp.OrionResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class AddMQTTConnectionConfigurationRequestBean extends OrionResponse
{
    private String dataSourceId;
    private String dataSourceType;
    private String apiUrl;
    private String apiKey;
    private String httpMethod;
    private String brokerUrl;
    private String brokerPort;
    private String topic;
    private String clientId;
    private Integer qualityOfServiceLevel;
    private String username;
    private String password;
}