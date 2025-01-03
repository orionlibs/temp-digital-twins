package io.github.orionlibs.orion_digital_twin.remote_data;

import io.github.orionlibs.orion_calendar.SQLTimestamp;
import io.github.orionlibs.orion_database.OrionModel;
import io.github.orionlibs.orion_object.CloningService;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TopicSubscriberModel implements OrionModel
{
    private Long topicSubscriberID;
    private String clientId;
    private String topic;
    private SQLTimestamp subscriptionDateTime;


    public static TopicSubscriberModel of()
    {
        return TopicSubscriberModel.builder().build();
    }


    public static TopicSubscriberModel of(Long topicSubscriberID)
    {
        return TopicSubscriberModel.builder().topicSubscriberID(topicSubscriberID).build();
    }


    @Override
    public boolean equals(Object other)
    {
        if(this == other)
        {
            return true;
        }
        else if(other instanceof TopicSubscriberModel otherTemp)
        {
            return Objects.equals(topicSubscriberID, otherTemp.getTopicSubscriberID());
        }
        else
        {
            return false;
        }
    }


    @Override
    public int hashCode()
    {
        return Objects.hash(topicSubscriberID);
    }


    @Override
    public TopicSubscriberModel clone()
    {
        return (TopicSubscriberModel)CloningService.clone(this);
    }


    @Override
    public TopicSubscriberModel getCopy()
    {
        return this.clone();
    }
}