package ru.sincore.signals;

import ru.sincore.adc.action.actions.AbstractAction;
import ru.sincore.client.AbstractClient;

/**
 * Generic class for incomming Adc actions signals
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 28.11.11
 *         Time: 14:14
 */
public class GenericAdcActionSignal<T extends AbstractAction>
{
    private AbstractClient client;
    private T              action;


    public GenericAdcActionSignal(AbstractClient client, T action)
    {
        this.client = client;
        this.action = action;
    }


    public AbstractClient getClient()
    {
        return client;
    }


    public void setClient(AbstractClient client)
    {
        this.client = client;
    }


    public T getAction()
    {
        return action;
    }


    public void setAction(T action)
    {
        this.action = action;
    }
}
