package ru.sincore.i18n;

import ru.sincore.ConfigurationManager;
import ru.sincore.client.AbstractClient;

import java.util.Locale;

/**
 * Class/file description
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 27.10.11
 *         Time: 16:05
 */
public class HubLocale
{
    private static ConfigurationManager configInstance = ConfigurationManager.instance();

    public static Locale getLocale()
    {
        return getLocale(configInstance.getString(ConfigurationManager.HUB_DEFAULT_LOCALE));
    }


    public static Locale getLocale(String localeString)
    {
        String[] localeStringParts = localeString.split("_");
        if (localeStringParts.length == 1)
        {
            return new Locale(localeStringParts[0]);
        }
        else if (localeStringParts.length == 2)
        {
            return new Locale(localeStringParts[0], localeStringParts[1]);
        }
        else
        {
            return Locale.ENGLISH;
        }
    }

    public static Locale getLocale(AbstractClient client)
    {
        if (client.isExtendedFieldExists("LC"))
        {
            return getLocale((String) client.getExtendedField("LC"));
        }

        return getLocale();
    }
}
