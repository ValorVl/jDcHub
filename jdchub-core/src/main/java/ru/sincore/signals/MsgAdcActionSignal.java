package ru.sincore.signals;

import ru.sincore.adc.action.actions.MSG;
import ru.sincore.client.AbstractClient;

/**
 * Signal for incoming MSG action
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 28.11.11
 *         Time: 14:24
 */
public class MsgAdcActionSignal extends GenericAdcActionSignal<MSG>
{
    public MsgAdcActionSignal(AbstractClient client, MSG action)
    {
        super(client, action);
    }
}
