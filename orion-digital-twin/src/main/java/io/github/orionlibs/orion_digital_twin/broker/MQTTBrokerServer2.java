package io.github.orionlibs.orion_digital_twin.broker;

import com.hivemq.configuration.service.InternalConfigurations;
import com.hivemq.embedded.EmbeddedHiveMQ;
import com.hivemq.migration.meta.PersistenceType;
import java.util.concurrent.ExecutionException;

public class MQTTBrokerServer2
{
    private final BrokerConnectionConfig config;
    private EmbeddedHiveMQ embeddedHiveMQ;
    private boolean isRunning;
    //private final ExecutorService clientHandlerPool;
    //private final ConcurrentHashMap<String, MQTTBrokerClient2> clients;


    public MQTTBrokerServer2(BrokerConnectionConfig config)
    {
        this.config = config;
        //this.clientHandlerPool = Executors.newCachedThreadPool();
        //this.clients = new ConcurrentHashMap<>();
    }


    public void startBroker() throws ExecutionException, InterruptedException
    {
        if(!isRunning)
        {
            this.embeddedHiveMQ = EmbeddedHiveMQ.builder().build();
            /*this.embeddedHiveMQ = EmbeddedHiveMQ.builder()
                        .withConfigurationFolder(Path.of("/path/to/embedded-config-folder"))
                        .withDataFolder(Path.of("/path/to/embedded-data-folder"))
                        .withExtensionsFolder(Path.of("/path/to/embedded-extensions-folder"));*/
            try
            {
                //InternalConfigurations.PAYLOAD_PERSISTENCE_TYPE.set(PersistenceType.FILE);
                //InternalConfigurations.RETAINED_MESSAGE_PERSISTENCE_TYPE.set(PersistenceType.FILE);
                embeddedHiveMQ.start().join();
                this.isRunning = true;
            }
            catch(final Exception ex)
            {
                ex.printStackTrace();
            }
            /*finally
            {
                embeddedHiveMQ.close();
            }*/
        }
    }


    public void stopBroker() throws ExecutionException, InterruptedException
    {
        if(isRunning && embeddedHiveMQ != null)
        {
            try
            {
                embeddedHiveMQ.stop().join();
                this.isRunning = false;
            }
            catch(final Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }


    public boolean isRunning()
    {
        return isRunning;
    }
}
