package io.github.orionlibs.orion_digital_twin_webapp.page.data_explorer.topic_explorer;

import io.github.orionlibs.orion_digital_twin.broker.client.ConnectorFactory;
import io.github.orionlibs.orion_digital_twin.device_details.ConnectionConfigurationModel;
import io.github.orionlibs.orion_digital_twin.device_details.ConnectionConfigurationsDAO;

public class TestMQTTPublisherTask implements Runnable
{
    private int counter = 0;


    @Override
    public void run()
    {
        try
        {
            ConnectionConfigurationModel config = ConnectionConfigurationsDAO.getByID(16L);
            String payload = "new message " + counter;
            counter++;
            new ConnectorFactory().newAsynchronousMQTTConnectorForPublisher(config.getBrokerUrl(), Integer.parseInt(config.getBrokerPort()), config.getTopic(), payload, "testPublisherClientID1");
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
    }
}
