package ru.sincore.signals;

import ru.sincore.adc.action.actions.SCH;
import ru.sincore.client.AbstractClient;

/**
 * Signal for incoming SCH action
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 28.11.11
 *         Time: 14:27
 */
public class SchAdcActionSignal extends GenericAdcActionSignal<SCH>
{
    public SchAdcActionSignal(AbstractClient client, SCH action)
    {
        super(client, action);
    }
}
