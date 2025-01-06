package io.github.orionlibs.orion_digital_twin.broker.server;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.unsubscribe.UnsubscribeInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.unsubscribe.parameter.UnsubscribeInboundInput;
import com.hivemq.extension.sdk.api.interceptor.unsubscribe.parameter.UnsubscribeInboundOutput;
import io.github.orionlibs.orion_digital_twin.remote_data.DataPacketsDAO;
import io.github.orionlibs.orion_digital_twin.remote_data.TopicSubscribersDAO;

public class MQTTUnsubscribeInterceptor implements UnsubscribeInboundInterceptor
{
    @Override
    public void onInboundUnsubscribe(@NotNull UnsubscribeInboundInput unsubscribeInboundInput, @NotNull UnsubscribeInboundOutput unsubscribeInboundOutput)
    {
        String clientId = unsubscribeInboundInput.getClientInformation().getClientId();
        unsubscribeInboundInput.getUnsubscribePacket()
                        .getTopicFilters()
                        .forEach(topic -> {
                            TopicSubscribersDAO.delete(topic, clientId);
                            DataPacketsDAO.deleteDataPacketsForTopicAndClientId(topic, clientId);
                        });
    }
}
