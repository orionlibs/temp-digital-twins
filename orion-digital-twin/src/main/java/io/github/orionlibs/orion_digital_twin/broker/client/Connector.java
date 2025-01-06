package io.github.orionlibs.orion_digital_twin.broker.client;

import java.util.List;

public interface Connector
{
    void connect() throws Exception;


    void disconnect() throws Exception;


    List<DataRecord> getData() throws Exception;
}
