package ru.sincore.signals;

import ru.sincore.adc.action.actions.RES;
import ru.sincore.client.AbstractClient;

/**
 * Signal for incoming RES action
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 29.11.11
 *         Time: 15:39
 */
public class ResAdcActionSignal extends GenericAdcActionSignal<RES>
{
    public ResAdcActionSignal(AbstractClient client, RES action)
    {
        super(client, action);
    }
}
