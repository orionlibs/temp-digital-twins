package io.github.orionlibs.orion_digital_twin.connector;

import io.github.orionlibs.orion_json.JSONService;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ConnectorConfigurationService
{
    public static List<String> configure(String configAsJSON, String filePathToSaveConfig)
    {
        Map<Object, Object> config = JSONService.convertJSONToMap(configAsJSON);
        if(config != null)
        {
            String dataSourceType = (String)config.get("dataSourceType");
            ConnectorConfigurator configurator = null;
            if(DataSourceType.HttpApi.get().equals(dataSourceType))
            {
                configurator = new HttpApiConnectorConfigurator(config);
            }
            else if(DataSourceType.Mqtt.get().equals(dataSourceType))
            {
                configurator = new MqttConnectorConfigurator(config);
            }
            if(configurator != null)
            {
                ConnectorConfigurator.Errors errorWrapper = configurator.validate();
                if(!errorWrapper.errors.isEmpty())
                {
                    return errorWrapper.errors;
                }
                try
                {
                    configurator.storeToFile(filePathToSaveConfig);
                }
                catch(IOException e)
                {
                    return List.of(e.getMessage());
                }
            }
        }
        return List.of();
    }
}
