package ru.sincore.signals;

import ru.sincore.adc.action.actions.SUP;
import ru.sincore.client.AbstractClient;

/**
 * Signal for incoming SUP action
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 28.11.11
 *         Time: 14:29
 */
public class SupAdcActionSignal extends GenericAdcActionSignal<SUP>
{
    public SupAdcActionSignal(AbstractClient client, SUP action)
    {
        super(client, action);
    }
}
