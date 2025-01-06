package io.github.orionlibs.orion_digital_twin.broker;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.publish.PublishInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundOutput;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import io.github.orionlibs.orion_calendar.SQLTimestamp;
import io.github.orionlibs.orion_digital_twin.remote_data.DataPacketModel;
import io.github.orionlibs.orion_digital_twin.remote_data.DataPacketsDAO;
import java.nio.ByteBuffer;

public class MQTTPublishInterceptor implements PublishInboundInterceptor
{
    @Override
    public void onInboundPublish(@NotNull PublishInboundInput publishInboundInput, @NotNull PublishInboundOutput publishInboundOutput)
    {
        String clientId = publishInboundInput.getClientInformation().getClientId();
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
                storePayloadToDatabase(clientId, topic, payload, qualityOfServiceLevel, payloadPyblicationDateTime);
            }
        }
        // Optional: Modify the publish message
        // publishInboundOutput.getPublishPacket().setPayload(...);
    }


    private void storePayloadToDatabase(String clientId, String topic, String payload, int qualityOfServiceLevel, SQLTimestamp payloadPyblicationDateTime)
    {
        DataPacketsDAO.save(DataPacketModel.builder()
                        .clientId(clientId)
                        .topic(topic)
                        .content(payload)
                        .qualityOfServiceLevel(qualityOfServiceLevel)
                        .isDeliveredToClient(Boolean.FALSE)
                        .publicationDateTime(payloadPyblicationDateTime)
                        .build());
    }
}
