package io.github.orionlibs.orion_digital_twin_webapp.page.data_explorer.topic_explorer;

import io.github.orionlibs.orion_digital_twin_webapp.OrionResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class TopicExplorerResponseBean extends OrionResponse
{
    private String mqttMessageArrived;
}