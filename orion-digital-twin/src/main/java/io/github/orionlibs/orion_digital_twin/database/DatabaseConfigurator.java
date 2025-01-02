package io.github.orionlibs.orion_digital_twin.database;

import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;

public abstract class DatabaseConfigurator
{
    protected BasicDataSource dataSource;


    protected DatabaseConfigurator(BasicDataSource dataSource)
    {
        this.dataSource = dataSource;
    }


    public abstract DataSource configure();
}