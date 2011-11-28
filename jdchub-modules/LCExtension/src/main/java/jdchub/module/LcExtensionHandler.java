package jdchub.module;

import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.Flags;
import ru.sincore.adc.action.actions.INF;
import ru.sincore.signals.InfAdcActionSignal;
import ru.sincore.signalservice.SignalHandler;

/**
 * Process LC Extension
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 21.10.11
 *         Time: 18:26
 */
public class LcExtensionHandler
{
    @SignalHandler
    public void handleRawInfCommand(InfAdcActionSignal data)
    {
        INF action = data.getAction();
        System.out.println("Signal handled: " + data.getClass().getName());

        try
        {
            if (action.isFlagSet(Flags.LOCALE))
            {
                String locale = action.getFlagValue(Flags.LOCALE);

                // Country and Language tokens can be delimeted by '-' or '_' chars
                // move all variants simple to underscore delimiter ('_')
                // ru_RU and ru-RU ==> ru_RU
                // en_US and en-US ==> en_US
                locale = locale.replace('-', '_');
                data.getClient().setExtendedField(Flags.LOCALE, locale);
            }
        }
        catch (CommandException e)
        {
            e.printStackTrace();
        }
        catch (STAException e)
        {
            e.printStackTrace();
        }
    }
}
