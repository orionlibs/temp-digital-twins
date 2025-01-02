package io.github.orionlibs.orion_digital_twin.connector;

import java.io.IOException;
import java.util.List;

public interface ConnectorConfigurator
{
    ConnectorConfigurator.Errors validate();


    void storeToFile(String configFilePath) throws IOException;


    void storeToDatabase();


    public static class Errors
    {
        public List<String> errors;
    }
}
