package io.github.orionlibs.orion_digital_twin.broker.server;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.publish.PublishInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundOutput;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import io.github.orionlibs.orion_calendar.SQLTimestamp;
import io.github.orionlibs.orion_digital_twin.remote_data.DataPacketModel;
import io.github.orionlibs.orion_digital_twin.remote_data.DataPacketsDAO;
import java.nio.ByteBuffer;
import java.util.Optional;

public class MQTTPublishInterceptor implements PublishInboundInterceptor
{
    @Override
    public void onInboundPublish(@NotNull PublishInboundInput publishInboundInput, @NotNull PublishInboundOutput publishInboundOutput)
    {
        String publisherId = publishInboundInput.getClientInformation().getClientId();
        String topic = publishInboundInput.getPublishPacket().getTopic();
        if(publishInboundOutput.getPublishPacket().getQos() == Qos.AT_LEAST_ONCE
                        || publishInboundOutput.getPublishPacket().getQos() == Qos.EXACTLY_ONCE)
        {
            SQLTimestamp payloadPyblicationDateTime = SQLTimestamp.of(publishInboundOutput.getPublishPacket().getTimestamp());
            int qualityOfServiceLevel = publishInboundOutput.getPublishPacket().getQos().getQosNumber();
            if(publishInboundOutput.getPublishPacket().getPayload().isPresent())
            {
                ByteBuffer buffer = publishInboundOutput.getPublishPacket().getPayload().get();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                String payload = new String(bytes);
                Optional<String> subscriberID = publishInboundOutput.getPublishPacket().getUserProperties().getFirst("subscriberId");
                if(subscriberID.isPresent())
                {
                    storePayloadToDatabase(publisherId, subscriberID.get(), topic, payload, qualityOfServiceLevel, payloadPyblicationDateTime);
                }
            }
        }
        // Optional: Modify the publish message
        // publishInboundOutput.getPublishPacket().setPayload(...);
    }


    private void storePayloadToDatabase(String publisherId, String subscriberId, String topic, String payload, int qualityOfServiceLevel, SQLTimestamp payloadPyblicationDateTime)
    {
        DataPacketsDAO.save(DataPacketModel.builder()
                        .subscriberId(subscriberId)
                        .publisherId(publisherId)
                        .topic(topic)
                        .content(payload)
                        .qualityOfServiceLevel(qualityOfServiceLevel)
                        .isDeliveredToSubscriber(Boolean.FALSE)
                        .publicationDateTime(payloadPyblicationDateTime)
                        .build());
    }
}
