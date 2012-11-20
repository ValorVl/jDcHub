/*
* StatsCommand.java
*
* Created on 03 09 2012, 16:14
*
* Copyright (C) 2012 Alexey 'lh' Antonov
*
* For more info read COPYRIGHT file in project root directory.
*
*/

package jdchub.module.commands.handlers;

import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-09-03
 */
public class StatsCommand extends AbstractCommand
{
    @Override
    public String execute(String cmd, String args, AbstractClient client)
    {
        StringBuilder info = new StringBuilder();
        info.append("\n Hub status:\n");

        client.sendPrivateMessageFromHub(info.toString());

        return "Info shawn";
    }
}
