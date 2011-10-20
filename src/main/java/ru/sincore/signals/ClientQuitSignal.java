package ru.sincore.signals;

import ru.sincore.client.Client;

/**
 * Signal that emited when client quit from Hub
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 19.10.11
 *         Time: 16:21
 */
public class ClientQuitSignal
{
    private Client client;

    public ClientQuitSignal(Client client)
    {
        this.client = client;
    }


    public Client getClient()
    {
        return client;
    }
}
