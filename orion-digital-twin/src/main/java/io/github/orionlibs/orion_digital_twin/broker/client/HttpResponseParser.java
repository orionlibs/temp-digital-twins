package io.github.orionlibs.orion_digital_twin.broker.client;

import java.util.ArrayList;
import java.util.List;

public class HttpResponseParser
{
    public static List<DataRecord> parseResponse(String response)
    {
        // Example: Assuming the response is in JSON format with a list of records
        List<DataRecord> records = new ArrayList<>();
        // Parse JSON (e.g., using a library like Jackson or Gson)
        // For simplicity, let's assume each line in response represents a record
        String[] lines = response.split("\n");
        if(!"".equals(response))
        {
            for(String line : lines)
            {
                records.add(new DataRecord(line));
            }
        }
        return records;
    }
}
