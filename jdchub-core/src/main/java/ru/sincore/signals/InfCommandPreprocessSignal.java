package ru.sincore.signals;

import ru.sincore.adc.MessageType;
import ru.sincore.client.AbstractClient;

/**
 * Signal emited before processing INF command of ADC protocol
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 21.10.11
 *         Time: 18:20
 */
public class InfCommandPreprocessSignal
{
    private AbstractClient client;
    private MessageType    messageType;
    private String         rawCommand;

    public InfCommandPreprocessSignal(AbstractClient client, MessageType messageType, String rawCommand)
    {
        this.client = client;
        this.messageType = messageType;
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
