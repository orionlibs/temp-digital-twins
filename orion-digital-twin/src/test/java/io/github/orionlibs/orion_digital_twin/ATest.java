package io.github.orionlibs.orion_digital_twin;

import io.github.orionlibs.orion_database_mysql.MySQL;
import io.github.orionlibs.orion_file_system.file.FileService;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.TimeZone;
import org.apache.commons.io.IOUtils;

public class ATest
{
    static
    {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        System.setProperty("active.execution.profile", OrionDomain.testing);
        Setup.setup();
    }

    protected String loadResourceAsString(String fileLocation)
    {
        try
        {
            return IOUtils.toString(this.getClass().getResourceAsStream(fileLocation));
        }
        catch(IOException e)
        {
            return "";
        }
    }


    protected InputStream loadResourceAsStream(String fileLocation)
    {
        return this.getClass().getResourceAsStream(fileLocation);
    }


    protected void assertApproximate(double expected, double actual, double tolerance)
    {
        if(Math.abs(actual - expected) > tolerance)
        {
            new AssertionError("" + expected + "does not approximate " + actual + "given tolerance " + tolerance);
        }
    }


    protected void resetDatabase()
    {
        resetTheDatabase();
    }


    protected void resetAndSeedDatabase()
    {
        resetTheDatabase();
        seedDatabase();
    }


    protected static void resetTheDatabase()
    {
        try
        {
            MySQL.runDDL("BEGIN NOT ATOMIC\n" + FileService.convertFileResourceToString("/io/github/orionlibs/orion_digital_twin/configuration/MySQLSchema.sql") + "\nEND");
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    protected void seedDatabase()
    {
        resetDatabase();
        seedTheDatabase();
    }


    protected static void seedTheDatabase()
    {
        resetTheDatabase();
        try
        {
            MySQL.runDDL("BEGIN NOT ATOMIC\n" + FileService.convertFileResourceToString("/io/github/orionlibs/orion_digital_twin/configuration/MySQLSchema-initialisation.sql") + "\nEND");
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
