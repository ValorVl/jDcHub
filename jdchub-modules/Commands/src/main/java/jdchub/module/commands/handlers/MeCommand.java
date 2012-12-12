/*
* MeCommand.java
*
* Created on 27 02 2012, 15:48
*
* Copyright (C) 2012 Alexey 'lh' Antonov
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package jdchub.module.commands.handlers;

import ru.sincore.Command;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.action.actions.MSG;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-02-27
 */
public class MeCommand extends AbstractCommand
{
    @Override
    public String execute(String cmd, String args, AbstractClient commandOwner)
    {
        try
        {
            MSG bmsg = new MSG();
            bmsg.setMessageType(MessageType.B);
            bmsg.setSourceSID(commandOwner.getSid());
            bmsg.setMessage(args);
            bmsg.setToMe(true);

            Command.handle(commandOwner, bmsg.getRawCommand());
        }
        catch (Exception e)
        {
            return e.toString();
        }

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
