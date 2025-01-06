package io.github.orionlibs.orion_digital_twin.broker.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class HttpApiConnector implements Connector
{
    private final String apiUrl;
    private final String apiKey;
    private final String method;
    @Getter
    private HttpClient client;
    @Setter
    private boolean connected;


    public HttpApiConnector(String apiUrl, String apiKey, String method)
    {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.method = method.toUpperCase();
        this.connected = false;
    }


    protected void setClient(HttpClient client)
    {
        this.client = client;
    }


    @Override
    public void connect()
    {
        client = HttpClient.newHttpClient();
        connected = true;
    }


    @Override
    public void disconnect()
    {
        client.close();
        connected = false;
    }


    @Override
    public List<DataRecord> getData() throws Exception
    {
        if(!connected)
        {
            connect();
            if(!connected)
            {
                throw new IllegalStateException("Connector is not connected!");
            }
        }
        List<DataRecord> dataRecords = null;
        HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .header("Authorization", "Bearer " + apiKey)
                        .method(method, BodyPublishers.ofString(""))
                        .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        disconnect();
        if(response.statusCode() == 200)
        {
            dataRecords = HttpResponseParser.parseResponse(response.body());
        }
        else
        {
            throw new IOException("Failed to fetch JSON. HTTP Status: " + response.statusCode());
        }
        return dataRecords;
    }
}
