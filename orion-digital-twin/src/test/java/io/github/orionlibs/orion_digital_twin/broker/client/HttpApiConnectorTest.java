package io.github.orionlibs.orion_digital_twin.broker.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.orionlibs.orion_digital_twin.ATest;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
//@Execution(ExecutionMode.CONCURRENT)
public class HttpApiConnectorTest extends ATest
{
    private HttpClient mockHttpClient;
    private HttpResponse<String> mockResponse;
    private HTTPAPIConnector connector;


    @BeforeEach
    void setUp()
    {
        // Mock HttpClient and HttpResponse
        mockHttpClient = mock(HttpClient.class);
        mockResponse = mock(HttpResponse.class);
        // Initialize connector with a mock API URL and key
        connector = new HTTPAPIConnector("https://mock-api.com/data", "mock-api-key", "GET")
        {
            @Override
            public void connect()
            {
                setClient(mockHttpClient);
                setConnected(true);
            }
        };
    }


    @Test
    void testGetData_SuccessfulResponse() throws Exception
    {
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("data1\ndata2\ndata3");
        List<DataRecord> dataRecords = connector.getData();
        assertEquals(3, dataRecords.size());
        assertEquals("data1", dataRecords.get(0).getRawData());
        assertEquals("data2", dataRecords.get(1).getRawData());
        assertEquals("data3", dataRecords.get(2).getRawData());
    }


    @Test
    void testFetchData_HttpErrorResponse() throws Exception
    {
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
        Exception exception = assertThrows(Exception.class, connector::getData);
        assertTrue(exception.getMessage().contains("HTTP Status: 500"));
    }


    @Test
    void testFetchData_EmptyResponse() throws Exception
    {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
        List<DataRecord> dataRecords = connector.getData();
        assertEquals(0, dataRecords.size());
    }
}
