package ru.sincore.signals;

import ru.sincore.adc.action.actions.SID;
import ru.sincore.client.AbstractClient;

/**
 * Signal for incoming SID action
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 28.11.11
 *         Time: 14:27
 */
public class SidAdcActionSignal extends GenericAdcActionSignal<SID>
{
    public SidAdcActionSignal(AbstractClient client, SID action)
    {
        super(client, action);
    }
}
