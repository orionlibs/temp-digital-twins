package io.github.orionlibs.orion_digital_twin.device_details;

import io.github.orionlibs.orion_database_mysql.MySQL;
import io.github.orionlibs.orion_digital_twin.database.Databases;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConnectionConfigurationsDAO
{
    public static long getNumberOfRecords()
    {
        return MySQL.getNumberOfRecords(Databases.tableConnectionConfigurations,
                        Databases.dataSourcesDatabase);
    }


    public static List<ConnectionConfigurationModel> getAll()
    {
        List<ConnectionConfigurationModel> results = new ArrayList<>();
        List<Object> temp = MySQL.getAllRows(ConnectionConfigurationModel.of(),
                        Databases.tableConnectionConfigurations,
                        Databases.dataSourcesDatabase);
        if(temp != null && !temp.isEmpty())
        {
            temp.forEach(record -> results.add((ConnectionConfigurationModel)record));
        }
        return results;
    }


    public static List<ConnectionConfigurationModel> getAllWithAscendingOrder(String columnToAscendBy)
    {
        List<ConnectionConfigurationModel> results = new ArrayList<>();
        List<Object> temp = MySQL.getAllRowsWithAscendingOrder(ConnectionConfigurationModel.of(),
                        Databases.tableConnectionConfigurations,
                        Databases.dataSourcesDatabase,
                        columnToAscendBy);
        if(temp != null && !temp.isEmpty())
        {
            temp.forEach(record -> results.add((ConnectionConfigurationModel)record));
        }
        return results;
    }


    public static ConnectionConfigurationModel getByID(Long connectionConfigurationID)
    {
        ConnectionConfigurationModel model = ConnectionConfigurationModel.builder()
                        .connectionConfigurationID(connectionConfigurationID)
                        .build();
        return (ConnectionConfigurationModel)MySQL.getOneModel(model,
                        Databases.tableConnectionConfigurations,
                        Databases.dataSourcesDatabase,
                        Arrays.asList(Databases.connectionConfigurationID));
    }


    public static int save(ConnectionConfigurationModel model)
    {
        return MySQL.saveModel(model,
                        Databases.tableConnectionConfigurations,
                        Databases.dataSourcesDatabase);
    }


    public static int update(ConnectionConfigurationModel model)
    {
        return MySQL.updateModel(model,
                        Databases.tableConnectionConfigurations,
                        Databases.dataSourcesDatabase,
                        Arrays.asList(Databases.connectionConfigurationID));
    }


    public static int delete(ConnectionConfigurationModel model)
    {
        return MySQL.deleteModel(model,
                        Databases.tableConnectionConfigurations,
                        Databases.dataSourcesDatabase,
                        Arrays.asList(Databases.connectionConfigurationID));
    }
}