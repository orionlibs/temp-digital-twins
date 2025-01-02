package io.github.orionlibs.orion_digital_twin.database;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.orionlibs.orion_database_mysql.MySQL;
import io.github.orionlibs.orion_digital_twin.ATest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
//@Execution(ExecutionMode.CONCURRENT)
public class DatabasesTest extends ATest
{
    @BeforeEach
    void setUp()
    {
        resetAndSeedDatabase();
    }


    @Test
    public void whenInsertRecordToDatabase_thenOK()
    {
        assertEquals(2L, MySQL.getNumberOfRecords(Databases.tableConnectionConfigurations,
                        Databases.dataSourcesDatabase));
    }
}
