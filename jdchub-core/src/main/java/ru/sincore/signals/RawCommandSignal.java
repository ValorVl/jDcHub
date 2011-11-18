package ru.sincore.signals;

import ru.sincore.client.AbstractClient;

/**
 * Signal that emited when raw command handling is called
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 19.10.11
 *         Time: 16:26
 */
public class RawCommandSignal
{
    private AbstractClient client;
    private String rawCommand;

    public RawCommandSignal(AbstractClient client, String rawCommand)
    {
        this.client = client;
        this.rawCommand = rawCommand;
    }


    public AbstractClient getClient()
    {
        return client;
    }


    public String getRawCommand()
    {
        return rawCommand;
    }
}
