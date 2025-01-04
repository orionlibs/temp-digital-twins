package io.github.orionlibs.orion_digital_twin.broker;

import io.github.orionlibs.orion_assert.OrionCheckedException;

public class MQTTServerNotRunningException extends OrionCheckedException
{
    private static final String DefaultErrorMessage = "MQTT Server is not running.";


    public MQTTServerNotRunningException()
    {
        super(DefaultErrorMessage);
    }


    public MQTTServerNotRunningException(String message)
    {
        super(message);
    }


    public MQTTServerNotRunningException(String errorMessage, Object... arguments)
    {
        super(String.format(errorMessage, arguments));
    }


    public MQTTServerNotRunningException(Throwable cause, String errorMessage, Object... arguments)
    {
        super(String.format(errorMessage, arguments), cause);
    }


    public MQTTServerNotRunningException(Throwable cause)
    {
        super(cause, DefaultErrorMessage);
    }
}