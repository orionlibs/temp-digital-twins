package io.github.orionlibs.orion_digital_twin.broker.client;

public class DataRecord
{
    private String rawData;


    public DataRecord(String rawData)
    {
        this.rawData = rawData;
    }


    public String getRawData()
    {
        return rawData;
    }


    @Override
    public String toString()
    {
        return rawData;
    }
}
