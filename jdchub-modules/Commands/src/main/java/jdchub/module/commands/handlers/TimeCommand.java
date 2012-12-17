/*
* TimeCommand.java
*
* Created on 18 12 2012, 09:45
*
* Copyright (C) 2012 Alexey 'lh' Antonov
*
* For more info read COPYRIGHT file in project root directory.
*
*/

package jdchub.module.commands.handlers;

import ru.sincore.Exceptions.STAException;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-12-18
 */
public class TimeCommand extends AbstractCommand
{
    @Override
    public String execute(String cmd, String args, AbstractClient commandOwner)
            throws STAException
    {
        Locale locale = Locale.US;

        if (commandOwner.isFeature("LC"))
        {
            String localeString = (String) commandOwner.getExtendedField("LC");
            String[] localeElements = localeString.split("_");
            if (localeElements.length == 1)
            {
                locale = new Locale(localeElements[0]);
            }
            else
            {
                locale = new Locale(localeElements[0], localeElements[1]);
            }

        }

        SimpleDateFormat
                dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z", locale);

        commandOwner.sendPrivateMessageFromHub(dateFormat.format(new Date()));
        return "Time shown";
    }
}
