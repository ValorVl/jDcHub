/*
* NAT.java
*
* Created on 26 06 2012, 14:35
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

package ru.sincore.adc.action.actions;

import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;

import java.util.List;
import java.util.Vector;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-06-26
 */
public class NAT extends AbstractAction
{
    private String  protocol;
    private Integer port;
    private String  token;

    {
        actionName = "NAT";
    }

    public NAT()
    {
        super();
    }


    public NAT(String rawCommand)
    {
        super(rawCommand);
    }

    @Override
    public String getRawCommand()
            throws CommandException
    {
        // Small optimisation: check parsing state before do any action
        if (isParsed == false)
        {
            return super.getRawCommand();
        }

        // Fill arguments list
        List<String> arguments = new Vector<String>();
        arguments.add(protocol);
        arguments.add(port.toString());
        arguments.add(token);

        return super.getRawCommand(arguments);
    }

    @Override
    protected boolean parse()
            throws CommandException, STAException
    {
        if (!super.parse())
        {
            return false;
        }

        // parse here
        protocol = arguments.poll();
        port     = Integer.parseInt(arguments.poll());
        token    = arguments.poll();

        return true;
    }


    public String getProtocol()
    {
        return protocol;
    }


    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }


    public Integer getPort()
    {
        return port;
    }


    public void setPort(Integer port)
    {
        this.port = port;
    }


    public String getToken()
    {
        return token;
    }


    public void setToken(String token)
    {
        this.token = token;
    }
}
