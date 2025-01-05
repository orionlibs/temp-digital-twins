package io.github.orionlibs.orion_digital_twin.broker;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.publish.PublishInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundOutput;

public class MQTTPublishInterceptor implements PublishInboundInterceptor
{
    @Override
    public void onInboundPublish(@NotNull PublishInboundInput publishInboundInput, @NotNull PublishInboundOutput publishInboundOutput)
    {
        String clientId = publishInboundInput.getClientInformation().getClientId();
        String topic = publishInboundInput.getPublishPacket().getTopic();
        System.out.println("Publish intercepted: ClientId=" + clientId + ", Topic=" + topic);
        //broker.publish(topic, message);
        //broker.unsubscribe(topic, clientId);
        //broker.subscribe(topic, clientId);
        // Optional: Modify the publish message
        // publishInboundOutput.getPublishPacket().setPayload(...);
    }
}
