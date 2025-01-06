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
public class DataPacketModel implements OrionModel
{
    private Long dataPacketID;
    private String subscriberId;
    private String publisherId;
    private String topic;
    private String content;
    private Integer qualityOfServiceLevel;
    private Boolean isDeliveredToSubscriber;
    private SQLTimestamp publicationDateTime;


    public static DataPacketModel of()
    {
        return DataPacketModel.builder().build();
    }


    public static DataPacketModel of(Long dataPacketID)
    {
        return DataPacketModel.builder().dataPacketID(dataPacketID).build();
    }


    @Override
    public boolean equals(Object other)
    {
        if(this == other)
        {
            return true;
        }
        else if(other instanceof DataPacketModel otherTemp)
        {
            return Objects.equals(dataPacketID, otherTemp.getDataPacketID());
        }
        else
        {
            return false;
        }
    }


    @Override
    public int hashCode()
    {
        return Objects.hash(dataPacketID);
    }


    @Override
    public DataPacketModel clone()
    {
        return (DataPacketModel)CloningService.clone(this);
    }


    @Override
    public DataPacketModel getCopy()
    {
        return this.clone();
    }
}