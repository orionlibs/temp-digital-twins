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


    public static long getNumberOfRecordsForClientId(String clientId)
    {
        DataPacketModel model = DataPacketModel.builder()
                        .clientId(clientId)
                        .build();
        return MySQL.getNumberOfRecords(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.clientId));
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
                        .isDeliveredToClient(Boolean.FALSE)
                        .build();
        return MySQL.getNumberOfRecords(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.isDeliveredToClient));
    }


    public static long getNumberOfRecordsForUndeliveredMessagesForClientId(String clientId)
    {
        DataPacketModel model = DataPacketModel.builder()
                        .clientId(clientId)
                        .isDeliveredToClient(Boolean.FALSE)
                        .build();
        return MySQL.getNumberOfRecords(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.clientId,
                                        Databases.isDeliveredToClient));
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


    public static List<DataPacketModel> getAllForClientId(String clientId)
    {
        DataPacketModel model = DataPacketModel.builder()
                        .clientId(clientId)
                        .build();
        List<Object> temp = MySQL.getModels(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.clientId));
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


    public static List<DataPacketModel> getAllUndeliveredForClientIdInChronologicalOrder(String clientId)
    {
        DataPacketModel model = DataPacketModel.builder()
                        .clientId(clientId)
                        .isDeliveredToClient(Boolean.FALSE)
                        .build();
        List<Object> temp = MySQL.getModelsWithAscendingOrder(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.clientId,
                                        Databases.isDeliveredToClient),
                        Databases.publicationDateTime);
        List<DataPacketModel> results = new ArrayList<>();
        if(temp != null && !temp.isEmpty())
        {
            temp.forEach(record -> results.add((DataPacketModel)record));
        }
        return results;
    }


    public static List<DataPacketModel> getAllUndeliveredForClientIdInReverseChronologicalOrder(String clientId)
    {
        DataPacketModel model = DataPacketModel.builder()
                        .clientId(clientId)
                        .isDeliveredToClient(Boolean.FALSE)
                        .build();
        List<Object> temp = MySQL.getModelsWithDescendingOrder(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.clientId,
                                        Databases.isDeliveredToClient),
                        Databases.publicationDateTime);
        List<DataPacketModel> results = new ArrayList<>();
        if(temp != null && !temp.isEmpty())
        {
            temp.forEach(record -> results.add((DataPacketModel)record));
        }
        return results;
    }


    public static List<DataPacketModel> getAllUndeliveredForClientIdAndTopicInChronologicalOrder(String clientId, String topic)
    {
        DataPacketModel model = DataPacketModel.builder()
                        .clientId(clientId)
                        .topic(topic)
                        .isDeliveredToClient(Boolean.FALSE)
                        .build();
        List<Object> temp = MySQL.getModelsWithAscendingOrder(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.clientId,
                                        Databases.topic,
                                        Databases.isDeliveredToClient),
                        Databases.publicationDateTime);
        List<DataPacketModel> results = new ArrayList<>();
        if(temp != null && !temp.isEmpty())
        {
            temp.forEach(record -> results.add((DataPacketModel)record));
        }
        return results;
    }


    public static List<DataPacketModel> getAllUndeliveredForClientIdAndTopicInReverseChronologicalOrder(String clientId, String topic)
    {
        DataPacketModel model = DataPacketModel.builder()
                        .clientId(clientId)
                        .topic(topic)
                        .isDeliveredToClient(Boolean.FALSE)
                        .build();
        List<Object> temp = MySQL.getModelsWithDescendingOrder(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.clientId,
                                        Databases.topic,
                                        Databases.isDeliveredToClient),
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


    public static int deleteDataPacketsForTopicAndClientId(String topic, String clientId)
    {
        DataPacketModel model = DataPacketModel.builder()
                        .clientId(clientId)
                        .topic(topic)
                        .build();
        return MySQL.deleteModel(model,
                        Databases.tableDataPackets,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.topic,
                                        Databases.clientId));
    }
}