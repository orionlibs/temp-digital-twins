package io.github.orionlibs.orion_digital_twin.broker.server;

import com.hivemq.embedded.EmbeddedExtension;
import com.hivemq.embedded.EmbeddedHiveMQ;
import com.hivemq.extension.sdk.api.ExtensionMain;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.client.ClientContext;
import com.hivemq.extension.sdk.api.client.parameter.InitializerInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartOutput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStopInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStopOutput;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.intializer.ClientInitializer;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

public class MQTTBrokerServer
{
    private EmbeddedHiveMQ embeddedHiveMQ;
    private boolean isRunning;


    public void startBroker() throws ExecutionException, InterruptedException, URISyntaxException
    {
        if(!isRunning)
        {
            this.embeddedHiveMQ = EmbeddedHiveMQ.builder()
                            .withConfigurationFolder(Paths.get(this.getClass().getResource("/io/github/orionlibs/orion_digital_twin/configuration").toURI()))
                            .withEmbeddedExtension(EmbeddedExtension.builder()
                                            .withId("interceptors")
                                            .withName("interceptors")
                                            .withVersion("0.0.1")
                                            .withExtensionMain(new ExtensionMain()
                                            {
                                                @Override
                                                public void extensionStart(@NotNull ExtensionStartInput extensionStartInput, @NotNull ExtensionStartOutput extensionStartOutput)
                                                {
                                                    final ClientInitializer clientInitializer = new ClientInitializer()
                                                    {
                                                        @Override
                                                        public void initialize(final @NotNull InitializerInput initializerInput, final @NotNull ClientContext clientContext)
                                                        {
                                                            clientContext.addPublishInboundInterceptor(new MQTTPublishInterceptor());
                                                            clientContext.addSubscribeInboundInterceptor(new MQTTSubscribeInterceptor());
                                                            clientContext.addUnsubscribeInboundInterceptor(new MQTTUnsubscribeInterceptor());
                                                        }
                                                    };
                                                    Services.initializerRegistry().setClientInitializer(clientInitializer);
                                                    //Services.clientService().iterateAllClients();
                                                    //Services.publishService().publishToClient();
                                                    //Services.subscriptionStore().iterateAllSubscribersWithTopicFilter();
                                                }


                                                @Override
                                                public void extensionStop(@NotNull ExtensionStopInput extensionStopInput, @NotNull ExtensionStopOutput extensionStopOutput)
                                                {
                                                    System.out.println("extension stopped");
                                                }
                                            })
                                            .build()).build();
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
        }
    }


    public void stopBroker()
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
