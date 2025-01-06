package io.github.orionlibs.orion_digital_twin.broker.client;

import io.github.orionlibs.orion_enumeration.OrionEnumeration;

public enum DataSourceType implements OrionEnumeration
{
    Mqtt("Mqtt"),
    HttpApi("HttpApi");
    private String name;


    private DataSourceType(String name)
    {
        setName(name);
    }


    @Override
    public String get()
    {
        return getName();
    }


    public String getName()
    {
        return this.name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    @Override
    public boolean is(OrionEnumeration other)
    {
        return other instanceof DataSourceType && this == other;
    }


    @Override
    public boolean isNot(OrionEnumeration other)
    {
        return other instanceof DataSourceType && this != other;
    }


    public static boolean valueExists(String other)
    {
        DataSourceType[] values = values();
        for(DataSourceType value : values)
        {
            if(value.get().equals(other))
            {
                return true;
            }
        }
        return false;
    }


    public static DataSourceType getEnumForValue(String other)
    {
        DataSourceType[] values = values();
        for(DataSourceType value : values)
        {
            if(value.get().equals(other))
            {
                return value;
            }
        }
        return null;
    }
}
