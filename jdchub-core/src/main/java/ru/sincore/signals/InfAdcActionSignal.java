package ru.sincore.signals;

import ru.sincore.adc.action.actions.INF;
import ru.sincore.client.AbstractClient;

/**
 * Signal for incoming INF action
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 28.11.11
 *         Time: 14:23
 */
public class InfAdcActionSignal extends GenericAdcActionSignal<INF>
{
    public InfAdcActionSignal(AbstractClient client, INF action)
    {
        super(client, action);
    }
}
