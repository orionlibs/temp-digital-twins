package io.github.orionlibs.orion_digital_twin.remote_data;

import io.github.orionlibs.orion_database_mysql.MySQL;
import io.github.orionlibs.orion_digital_twin.database.Databases;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataPacketsDAO
{
    public static long getNumberOfRecords()
    {
        return MySQL.getNumberOfRecords(Databases.tableDataPackets,
                        Databases.remoteDataDatabase);
    }


    public static long getNumberOfRecordsForSubscriberId(String subscriberId)
    {
        DataPacketModel model = DataPacketModel.builder()
                        .subscriberId(subscriberId)
                        .build();
        return MySQL.getNumberOfRecords(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.subscriberId));
    }


    public static long getNumberOfRecordsForTopic(String topic)
    {
        DataPacketModel model = DataPacketModel.builder()
                        .topic(topic)
                        .build();
        return MySQL.getNumberOfRecords(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.topic));
    }


    public static long getNumberOfRecordsForUndeliveredMessages()
    {
        DataPacketModel model = DataPacketModel.builder()
                        .isDeliveredToSubscriber(Boolean.FALSE)
                        .build();
        return MySQL.getNumberOfRecords(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.isDeliveredToSubscriber));
    }


    public static long getNumberOfRecordsForUndeliveredMessagesForSubscriberId(String subscriberId)
    {
        DataPacketModel model = DataPacketModel.builder()
                        .subscriberId(subscriberId)
                        .isDeliveredToSubscriber(Boolean.FALSE)
                        .build();
        return MySQL.getNumberOfRecords(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.clientId,
                                        Databases.isDeliveredToSubscriber));
    }


    public static List<DataPacketModel> getAll()
    {
        List<DataPacketModel> results = new ArrayList<>();
        List<Object> temp = MySQL.getAllRows(DataPacketModel.of(),
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase);
        if(temp != null && !temp.isEmpty())
        {
            temp.forEach(record -> results.add((DataPacketModel)record));
        }
        return results;
    }


    public static List<DataPacketModel> getAllForSubscriberId(String subscriberId)
    {
        DataPacketModel model = DataPacketModel.builder()
                        .subscriberId(subscriberId)
                        .build();
        List<Object> temp = MySQL.getModels(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.subscriberId));
        List<DataPacketModel> results = new ArrayList<>();
        if(temp != null && !temp.isEmpty())
        {
            temp.forEach(record -> results.add((DataPacketModel)record));
        }
        return results;
    }


    public static List<DataPacketModel> getAllForTopic(String topic)
    {
        DataPacketModel model = DataPacketModel.builder()
                        .topic(topic)
                        .build();
        List<Object> temp = MySQL.getModels(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.topic));
        List<DataPacketModel> results = new ArrayList<>();
        if(temp != null && !temp.isEmpty())
        {
            temp.forEach(record -> results.add((DataPacketModel)record));
        }
        return results;
    }


    public static List<DataPacketModel> getAllUndeliveredForSubscriberIdInChronologicalOrder(String subscriberId)
    {
        DataPacketModel model = DataPacketModel.builder()
                        .subscriberId(subscriberId)
                        .isDeliveredToSubscriber(Boolean.FALSE)
                        .build();
        List<Object> temp = MySQL.getModelsWithAscendingOrder(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.subscriberId,
                                        Databases.isDeliveredToSubscriber),
                        Databases.publicationDateTime);
        List<DataPacketModel> results = new ArrayList<>();
        if(temp != null && !temp.isEmpty())
        {
            temp.forEach(record -> results.add((DataPacketModel)record));
        }
        return results;
    }


    public static List<DataPacketModel> getAllUndeliveredForSubscriberIdInReverseChronologicalOrder(String subscriberId)
    {
        DataPacketModel model = DataPacketModel.builder()
                        .subscriberId(subscriberId)
                        .isDeliveredToSubscriber(Boolean.FALSE)
                        .build();
        List<Object> temp = MySQL.getModelsWithDescendingOrder(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.subscriberId,
                                        Databases.isDeliveredToSubscriber),
                        Databases.publicationDateTime);
        List<DataPacketModel> results = new ArrayList<>();
        if(temp != null && !temp.isEmpty())
        {
            temp.forEach(record -> results.add((DataPacketModel)record));
        }
        return results;
    }


    public static List<DataPacketModel> getAllUndeliveredForSubscriberIdAndTopicInChronologicalOrder(String subscriberId, String topic)
    {
        DataPacketModel model = DataPacketModel.builder()
                        .subscriberId(subscriberId)
                        .topic(topic)
                        .isDeliveredToSubscriber(Boolean.FALSE)
                        .build();
        List<Object> temp = MySQL.getModelsWithAscendingOrder(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.subscriberId,
                                        Databases.topic,
                                        Databases.isDeliveredToSubscriber),
                        Databases.publicationDateTime);
        List<DataPacketModel> results = new ArrayList<>();
        if(temp != null && !temp.isEmpty())
        {
            temp.forEach(record -> results.add((DataPacketModel)record));
        }
        return results;
    }


    public static List<DataPacketModel> getAllUndeliveredForSubscriberIdAndTopicInReverseChronologicalOrder(String subscriberId, String topic)
    {
        DataPacketModel model = DataPacketModel.builder()
                        .subscriberId(subscriberId)
                        .topic(topic)
                        .isDeliveredToSubscriber(Boolean.FALSE)
                        .build();
        List<Object> temp = MySQL.getModelsWithDescendingOrder(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.subscriberId,
                                        Databases.topic,
                                        Databases.isDeliveredToSubscriber),
                        Databases.publicationDateTime);
        List<DataPacketModel> results = new ArrayList<>();
        if(temp != null && !temp.isEmpty())
        {
            temp.forEach(record -> results.add((DataPacketModel)record));
        }
        return results;
    }


    public static DataPacketModel getByID(Long dataPacketID)
    {
        DataPacketModel model = DataPacketModel.of(dataPacketID);
        return (DataPacketModel)MySQL.getOneModel(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.dataPacketID));
    }


    public static int save(DataPacketModel model)
    {
        return MySQL.saveModel(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase);
    }


    public static int update(DataPacketModel model)
    {
        return MySQL.updateModel(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.dataPacketID));
    }


    public static int delete(DataPacketModel model)
    {
        return MySQL.deleteModel(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.dataPacketID));
    }


    public static int deleteDataPacketsForTopicAndClientId(String topic, String subscriberId)
    {
        DataPacketModel model = DataPacketModel.builder()
                        .subscriberId(subscriberId)
                        .topic(topic)
                        .build();
        return MySQL.deleteModel(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.subscriberId,
                                        Databases.topic));
    }
}