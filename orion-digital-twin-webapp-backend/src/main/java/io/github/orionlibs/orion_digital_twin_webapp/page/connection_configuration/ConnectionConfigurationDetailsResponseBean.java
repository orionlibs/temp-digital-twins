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
public class ConnectionConfigurationDetailsResponseBean extends OrionResponse
{
    private ConnectionConfigurationModel connectionConfiguration;


    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class ConnectionConfigurationModel implements Serializable
    {
        private Long connectionConfigurationID;
        private String dataSourceId;
        private String dataSourceType;
        private String apiUrl;
        private String apiKey;
        private String httpMethod;
        private String brokerUrl;
        private String topic;
        private String clientId;
        private String username;
        private String password;
    }
}