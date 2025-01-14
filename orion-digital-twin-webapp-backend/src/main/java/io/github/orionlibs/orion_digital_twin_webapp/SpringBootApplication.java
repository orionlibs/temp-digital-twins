package io.github.orionlibs.orion_digital_twin_webapp;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.orionlibs.orion_digital_twin.OrionDomain;
import io.github.orionlibs.orion_digital_twin.Setup;
import io.github.orionlibs.orion_digital_twin_webapp.page.DashboardController;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@org.springframework.boot.autoconfigure.SpringBootApplication
@EnableWebMvc
@EnableScheduling
@Import({DashboardController.class})
public class SpringBootApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(SpringBootApplication.class, args);
        System.setProperty("active.execution.profile", OrionDomain.production);
        Setup.setup();
        //Properties customConfig = new Properties();
        //customConfig.put("orionlibs.orion-iot.database.of.iot.device.data.name", "iot_devices_temp22222");
        //ConfigurationService.updateProps(customConfig);
    }


    @Bean
    public HandlerMapping handlerMapping()
    {
        return new RequestMappingHandlerMapping();
    }


    @Bean
    public HandlerAdapter handlerAdapter()
    {
        return new RequestMappingHandlerAdapter();
    }


    @Bean
    public ObjectMapper getObjectMapper()
    {
        return new Jackson2ObjectMapperBuilder().serializationInclusion(Include.NON_NULL)
                        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                                        SerializationFeature.FAIL_ON_EMPTY_BEANS,
                                        SerializationFeature.FAIL_ON_SELF_REFERENCES)
                        .build()
                        .setDefaultPrettyPrinter(new MinimalPrettyPrinter());
    }
}