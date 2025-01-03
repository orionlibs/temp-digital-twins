package io.github.orionlibs.orion_digital_twin_webapp.page.connection_configuration;

import io.github.orionlibs.orion_digital_twin.connector.ConnectorConfigurationService;
import io.github.orionlibs.orion_digital_twin.device_details.ConnectionConfigurationModel;
import io.github.orionlibs.orion_digital_twin.device_details.ConnectionConfigurationsDAO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/wapi/v1/connection-configurations")
public class AddConnectionConfigurationController
{
    @PostMapping(consumes = "application/json")
    public ResponseEntity<AddConnectionConfigurationResponseBean> connectionConfigurationsPageAddConnectionConfiguration(@RequestBody AddConnectionConfigurationRequestBean requestBean)
    {
        try
        {
            ConnectionConfigurationsDAO.save(ConnectionConfigurationModel.builder()
                            .dataSourceId(requestBean.getDataSourceId())
                            .dataSourceType(requestBean.getDataSourceType())
                            .apiUrl(requestBean.getApiUrl())
                            .apiKey(requestBean.getApiKey())
                            .httpMethod(requestBean.getHttpMethod())
                            .brokerUrl(requestBean.getBrokerUrl())
                            .topic(requestBean.getTopic())
                            .clientId(requestBean.getClientId())
                            .username(requestBean.getUsername())
                            .password(requestBean.getPassword())
                            .build());
            return ResponseEntity.ok(AddConnectionConfigurationResponseBean.builder().build());
        }
        catch(Throwable e)
        {
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping(value = "/uploads/json", consumes = "multipart/form-data")
    public ResponseEntity<String> connectionConfigurationsPageAddConnectionConfigurationViaJSON(@RequestParam("files[]") MultipartFile[] files)
    {
        List<String> fileNames = new ArrayList<>();
        try
        {
            for(MultipartFile file : files)
            {
                if(file.isEmpty())
                {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty!");
                }
                String fileJSONContent = IOUtils.toString(file.getInputStream(), "UTF-8");
                new ConnectorConfigurationService().configureAndSaveToDatabase(fileJSONContent);
            }
            return ResponseEntity.ok("Uploaded successfully: " + String.join(", ", fileNames));
        }
        catch(IOException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to upload files: " + e.getMessage());
        }
        catch(Throwable e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to upload files: " + e.getMessage());
        }
    }
}
