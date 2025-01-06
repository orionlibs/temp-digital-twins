package io.github.orionlibs.orion_digital_twin.broker.server;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.subscribe.SubscribeInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.subscribe.parameter.SubscribeInboundInput;
import com.hivemq.extension.sdk.api.interceptor.subscribe.parameter.SubscribeInboundOutput;
import io.github.orionlibs.orion_calendar.CalendarService;
import io.github.orionlibs.orion_digital_twin.remote_data.TopicSubscriberModel;
import io.github.orionlibs.orion_digital_twin.remote_data.TopicSubscribersDAO;

public class MQTTSubscribeInterceptor implements SubscribeInboundInterceptor
{
    @Override
    public void onInboundSubscribe(@NotNull SubscribeInboundInput subscribeInboundInput, @NotNull SubscribeInboundOutput subscribeInboundOutput)
    {
        String clientId = subscribeInboundInput.getClientInformation().getClientId();
        subscribeInboundInput.getSubscribePacket()
                        .getSubscriptions()
                        .forEach(subscription -> {
                            TopicSubscribersDAO.save(TopicSubscriberModel.builder()
                                            .clientId(clientId)
                                            .topic(subscription.getTopicFilter())
                                            .subscriptionDateTime(CalendarService.getCurrentDatetimeAsSQLTimestamp())
                                            .build());
                        });
        // Optional: Reject subscriptions
        // subscribeInboundOutput.preventSubscription();
    }
}
