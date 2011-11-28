package ru.sincore.signals;

import ru.sincore.adc.action.actions.PAS;
import ru.sincore.client.AbstractClient;

/**
 * Signal for incoming PAS action
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 28.11.11
 *         Time: 14:25
 */
public class PasAdcActionSignal extends GenericAdcActionSignal<PAS>
{
    public PasAdcActionSignal(AbstractClient client, PAS action)
    {
        super(client, action);
    }
}
