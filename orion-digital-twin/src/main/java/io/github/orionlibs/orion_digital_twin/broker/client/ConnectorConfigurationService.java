package io.github.orionlibs.orion_digital_twin.broker.client;

import io.github.orionlibs.orion_json.JSONService;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ConnectorConfigurationService
{
    private Map<Object, Object> config;
    private ConnectorConfigurator configurator;


    public List<String> configureAndSaveToFile(String configAsJSON, String filePathToSaveConfig)
    {
        extractAndValidate(configAsJSON);
        if(configurator != null)
        {
            try
            {
                configurator.storeToFile(filePathToSaveConfig);
            }
            catch(IOException e)
            {
                return List.of(e.getMessage());
            }
        }
        return Collections.emptyList();
    }


    public List<String> configureAndSaveToDatabase(String configAsJSON)
    {
        extractAndValidate(configAsJSON);
        if(configurator != null)
        {
            configurator.storeToDatabase();
        }
        return Collections.emptyList();
    }


    private List<String> extractAndValidate(String configAsJSON)
    {
        this.config = JSONService.convertJSONToMap(configAsJSON);
        if(config != null)
        {
            String dataSourceType = (String)config.get("dataSourceType");
            if(DataSourceType.HttpApi.get().equals(dataSourceType))
            {
                this.configurator = new HttpApiConnectorConfigurator(config);
            }
            else if(DataSourceType.Mqtt.get().equals(dataSourceType))
            {
                this.configurator = new MqttConnectorConfigurator(config);
            }
            if(configurator != null)
            {
                ConnectorConfigurator.Errors errorWrapper = configurator.validate();
                if(!errorWrapper.errors.isEmpty())
                {
                    return errorWrapper.errors;
                }
            }
        }
        return Collections.emptyList();
    }
}
