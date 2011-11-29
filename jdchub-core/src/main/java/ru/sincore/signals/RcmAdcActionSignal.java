package ru.sincore.signals;

import ru.sincore.adc.action.actions.RCM;
import ru.sincore.client.AbstractClient;

/**
 * Signal for incoming RCM action
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 28.11.11
 *         Time: 14:26
 */
public class RcmAdcActionSignal extends GenericAdcActionSignal<RCM>
{
    public RcmAdcActionSignal(AbstractClient client, RCM action)
    {
        super(client, action);
    }
}
