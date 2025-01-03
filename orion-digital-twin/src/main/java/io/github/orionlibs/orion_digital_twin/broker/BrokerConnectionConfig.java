package io.github.orionlibs.orion_digital_twin.broker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class BrokerConnectionConfig
{
    private String host;
    private int port;
    private String username;
    private String password;
    //whether to use TLS/SSL
    private boolean secure;
    //optional URL for HTTP API
    private String apiUrl;
}
