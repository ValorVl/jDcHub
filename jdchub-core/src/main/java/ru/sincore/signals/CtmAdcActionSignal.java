package ru.sincore.signals;

import ru.sincore.adc.action.actions.CTM;
import ru.sincore.client.AbstractClient;

/**
 * Signal for incoming CTM action
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 28.11.11
 *         Time: 14:21
 */
public class CtmAdcActionSignal extends GenericAdcActionSignal<CTM>
{
    public CtmAdcActionSignal(AbstractClient client, CTM action)
    {
        super(client, action);
    }
}
