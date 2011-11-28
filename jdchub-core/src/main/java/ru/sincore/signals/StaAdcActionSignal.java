package ru.sincore.signals;

import ru.sincore.adc.action.actions.STA;
import ru.sincore.client.AbstractClient;

/**
 * Signal for incoming STA action
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 28.11.11
 *         Time: 14:28
 */
public class StaAdcActionSignal extends GenericAdcActionSignal<STA>
{
    public StaAdcActionSignal(AbstractClient client, STA action)
    {
        super(client, action);
    }
}
