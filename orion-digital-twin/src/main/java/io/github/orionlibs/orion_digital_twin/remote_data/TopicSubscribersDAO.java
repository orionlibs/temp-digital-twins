package io.github.orionlibs.orion_digital_twin.remote_data;

import io.github.orionlibs.orion_database_mysql.MySQL;
import io.github.orionlibs.orion_digital_twin.database.Databases;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TopicSubscribersDAO
{
    public static long getNumberOfRecords()
    {
        return MySQL.getNumberOfRecords(Databases.tableTopicSubscribers,
                        Databases.remoteDataDatabase);
    }


    public static long getNumberOfTopicsForClientId(String clientId)
    {
        TopicSubscriberModel model = TopicSubscriberModel.builder()
                        .clientId(clientId)
                        .build();
        return MySQL.getNumberOfRecords(model,
                        Databases.tableTopicSubscribers,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.clientId));
    }


    public static long getNumberOfSubscribersForTopic(String topic)
    {
        TopicSubscriberModel model = TopicSubscriberModel.builder()
                        .topic(topic)
                        .build();
        return MySQL.getNumberOfRecords(model,
                        Databases.tableTopicSubscribers,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.topic));
    }


    public static List<TopicSubscriberModel> getAll()
    {
        List<TopicSubscriberModel> results = new ArrayList<>();
        List<Object> temp = MySQL.getAllRows(TopicSubscriberModel.of(),
                        Databases.tableTopicSubscribers,
                        Databases.remoteDataDatabase);
        if(temp != null && !temp.isEmpty())
        {
            temp.forEach(record -> results.add((TopicSubscriberModel)record));
        }
        return results;
    }


    public static List<TopicSubscriberModel> getAllTopicsForClientId(String clientId)
    {
        TopicSubscriberModel model = TopicSubscriberModel.builder()
                        .clientId(clientId)
                        .build();
        List<Object> temp = MySQL.getModels(model,
                        Databases.tableTopicSubscribers,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.clientId));
        List<TopicSubscriberModel> results = new ArrayList<>();
        if(temp != null && !temp.isEmpty())
        {
            temp.forEach(record -> results.add((TopicSubscriberModel)record));
        }
        return results;
    }


    public static List<TopicSubscriberModel> getAllSubscribersForTopic(String topic)
    {
        TopicSubscriberModel model = TopicSubscriberModel.builder()
                        .topic(topic)
                        .build();
        List<Object> temp = MySQL.getModels(model,
                        Databases.tableTopicSubscribers,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.topic));
        List<TopicSubscriberModel> results = new ArrayList<>();
        if(temp != null && !temp.isEmpty())
        {
            temp.forEach(record -> results.add((TopicSubscriberModel)record));
        }
        return results;
    }


    public static TopicSubscriberModel getByID(Long topicSubscriberID)
    {
        TopicSubscriberModel model = TopicSubscriberModel.of(topicSubscriberID);
        return (TopicSubscriberModel)MySQL.getOneModel(model,
                        Databases.tableTopicSubscribers,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.topicSubscriberID));
    }


    public static int save(TopicSubscriberModel model)
    {
        return MySQL.saveModel(model,
                        Databases.tableTopicSubscribers,
                        Databases.remoteDataDatabase);
    }


    public static int update(TopicSubscriberModel model)
    {
        return MySQL.updateModel(model,
                        Databases.tableTopicSubscribers,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.topicSubscriberID));
    }


    public static int delete(TopicSubscriberModel model)
    {
        return MySQL.deleteModel(model,
                        Databases.tableTopicSubscribers,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.topicSubscriberID));
    }


    public static int delete(String topic, String clientId)
    {
        TopicSubscriberModel model = TopicSubscriberModel.builder()
                        .clientId(clientId)
                        .topic(topic)
                        .build();
        return MySQL.deleteModel(model,
                        Databases.tableTopicSubscribers,
                        Databases.remoteDataDatabase,
                        Arrays.asList(Databases.clientId,
                                        Databases.topic));
    }
}