package io.github.orionlibs.orion_digital_twin.broker;

import io.github.orionlibs.orion_digital_twin.remote_data.DataPacketModel;
import java.nio.charset.StandardCharsets;
import org.eclipse.paho.mqttv5.common.MqttMessage;

public class DataPacketToMQTTMessageConverter
{
    public static MqttMessage convert(DataPacketModel source)
    {
        MqttMessage target = new MqttMessage(source.getContent().getBytes(StandardCharsets.UTF_8), source.getQualityOfServiceLevel().intValue(), true, null);
        target.setId(source.getDataPacketID().intValue());
        return target;
    }
}
