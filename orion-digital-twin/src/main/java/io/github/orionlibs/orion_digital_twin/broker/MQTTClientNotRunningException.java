package io.github.orionlibs.orion_digital_twin.broker;

import io.github.orionlibs.orion_assert.OrionCheckedException;

public class MQTTClientNotRunningException extends OrionCheckedException
{
    private static final String DefaultErrorMessage = "MQTT client is not running.";


    public MQTTClientNotRunningException()
    {
        super(DefaultErrorMessage);
    }


    public MQTTClientNotRunningException(String message)
    {
        super(message);
    }


    public MQTTClientNotRunningException(String errorMessage, Object... arguments)
    {
        super(String.format(errorMessage, arguments));
    }


    public MQTTClientNotRunningException(Throwable cause, String errorMessage, Object... arguments)
    {
        super(String.format(errorMessage, arguments), cause);
    }


    public MQTTClientNotRunningException(Throwable cause)
    {
        super(cause, DefaultErrorMessage);
    }
}