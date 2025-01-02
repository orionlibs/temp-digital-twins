package io.github.orionlibs.orion_digital_twin.device_details;

import io.github.orionlibs.orion_database.OrionModel;
import io.github.orionlibs.orion_object.CloningService;
import java.util.Objects;
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
public class ConnectionConfigurationModel implements OrionModel
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


    public static ConnectionConfigurationModel of()
    {
        return ConnectionConfigurationModel.builder().build();
    }


    public static ConnectionConfigurationModel of(Long connectionConfigurationID)
    {
        return ConnectionConfigurationModel.builder().connectionConfigurationID(connectionConfigurationID).build();
    }


    @Override
    public boolean equals(Object other)
    {
        if(this == other)
        {
            return true;
        }
        else if(other instanceof ConnectionConfigurationModel otherTemp)
        {
            return Objects.equals(connectionConfigurationID, otherTemp.getConnectionConfigurationID());
        }
        else
        {
            return false;
        }
    }


    @Override
    public int hashCode()
    {
        return Objects.hash(connectionConfigurationID);
    }


    @Override
    public ConnectionConfigurationModel clone()
    {
        return (ConnectionConfigurationModel)CloningService.clone(this);
    }


    @Override
    public ConnectionConfigurationModel getCopy()
    {
        return this.clone();
    }
}