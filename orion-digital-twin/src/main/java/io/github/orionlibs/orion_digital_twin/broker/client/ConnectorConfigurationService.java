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


    public List<String> configureHTTPAPIAndSaveToFile(String configAsJSON, String filePathToSaveConfig)
    {
        extractAndValidateForHTTPAPI(configAsJSON);
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


    public List<String> configureMQTTAndSaveToFile(String configAsJSON, String filePathToSaveConfig)
    {
        extractAndValidateForMQTT(configAsJSON);
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


    public List<String> configureHTTPAPIAndSaveToDatabase(String configAsJSON)
    {
        extractAndValidateForHTTPAPI(configAsJSON);
        if(configurator != null)
        {
            configurator.storeToDatabase();
        }
        return Collections.emptyList();
    }


    public List<String> configureMQTTAndSaveToDatabase(String configAsJSON)
    {
        extractAndValidateForMQTT(configAsJSON);
        if(configurator != null)
        {
            configurator.storeToDatabase();
        }
        return Collections.emptyList();
    }


    private List<String> extractAndValidateForMQTT(String configAsJSON)
    {
        this.config = JSONService.convertJSONToMap(configAsJSON);
        if(config != null)
        {
            String dataSourceType = (String)config.get("dataSourceType");
            if(DataSourceType.Mqtt.get().equals(dataSourceType))
            {
                this.configurator = new MQTTConnectorConfigurator(config);
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


    private List<String> extractAndValidateForHTTPAPI(String configAsJSON)
    {
        this.config = JSONService.convertJSONToMap(configAsJSON);
        if(config != null)
        {
            String dataSourceType = (String)config.get("dataSourceType");
            if(DataSourceType.HttpApi.get().equals(dataSourceType))
            {
                this.configurator = new HTTPAPIConnectorConfigurator(config);
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
