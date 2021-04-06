package io.github.yueyinqiu.compass;

public class NoSuchSensorException extends Exception
{
    public NoSuchSensorException()
    {
    }

    public NoSuchSensorException(String message)
    {
        super(message);
    }

    public NoSuchSensorException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public NoSuchSensorException(Throwable cause)
    {
        super(cause);
    }
}
