package io.github.orionlibs.orion_digital_twin.connector;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

class HttpApiConnectorConfigurator implements ConnectorConfigurator
{
    private static final Pattern URL_PATTERN = Pattern.compile("^(http|https)://.+$");
    private final Map<Object, Object> config;


    HttpApiConnectorConfigurator(Map<Object, Object> config)
    {
        this.config = config;
    }


    @Override
    public ConnectorConfigurator.Errors validate()
    {
        ConnectorConfigurator.Errors errorWrapper = new ConnectorConfigurator.Errors();
        List<String> errorsFound = new ArrayList<>();
        if(config == null || config.isEmpty())
        {
            errorsFound.add("Configuration cannot be null or empty.");
        }
        if(!config.containsKey("dataSourceId"))
        {
            errorsFound.add("Configuration must include an 'dataSourceId' key.");
        }
        if(!config.containsKey("dataSourceType"))
        {
            errorsFound.add("Configuration must include an 'dataSourceType' key.");
        }
        if(!config.containsKey("apiUrl"))
        {
            errorsFound.add("Configuration must include an 'apiUrl' key.");
        }
        if(!config.containsKey("apiKey"))
        {
            errorsFound.add("Configuration must include an 'apiKey' key.");
        }
        String url = (String)config.get("apiUrl");
        if(!URL_PATTERN.matcher(url).matches())
        {
            errorsFound.add("Invalid URL format: " + url);
        }
        // Validate optional authentication details
        if(!config.containsKey("method"))
        {
            errorsFound.add("Configuration must include an HTTP 'method' key.");
        }
        errorWrapper.errors = errorsFound;
        return errorWrapper;
    }


    @Override
    public void storeToFile(String configFilePath) throws IOException
    {
        validate();
        Properties properties = new Properties();
        properties.putAll(config);
        try(OutputStream outputStream = Files.newOutputStream(Paths.get(configFilePath)))
        {
            properties.store(outputStream, "Connector Configuration");
        }
    }


    @Override
    public void storeToDatabase()
    {
    }
}
