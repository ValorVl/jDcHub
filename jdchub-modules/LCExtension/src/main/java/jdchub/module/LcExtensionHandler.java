package jdchub.module;

import java.util.StringTokenizer;

import ru.sincore.signals.InfCommandPreprocessSignal;
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
    public void handleRawInfCommand(InfCommandPreprocessSignal data)
    {
        StringTokenizer tokenizer = new StringTokenizer(data.getRawCommand(), " ");
        tokenizer.nextToken(); // skip 'BINF'
        tokenizer.nextToken(); // skip SID

        System.out.println("Signal handled: " + data.getClass().getName());

        while (tokenizer.hasMoreElements())
        {
            String token = tokenizer.nextToken();
            if (token.startsWith("LC"))
            {
                String locale = token.substring(2);

                // Country and Language tokens can be delimeted by '-' or '_' chars
                // move all variants simple to underscore delimiter ('_')
                // ru_RU and ru-RU ==> ru_RU
                // en_US and en-US ==> en_US
                locale = locale.replace('-', '_');
                data.getClient().setExtendedField("LC", locale);
            }
        }
    }
}
