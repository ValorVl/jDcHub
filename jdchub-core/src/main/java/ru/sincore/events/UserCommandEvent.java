/*
* UserCommandEvent.java
*
* Created on 24 11 2011, 11:17
*
* Copyright (C) 2011 Alexey 'lh' Antonov
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

package ru.sincore.events;

import ru.sincore.client.AbstractClient;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-11-24
 */
public class UserCommandEvent
{
    String command;
    String args;
    AbstractClient client;

    public UserCommandEvent(String command, String args, AbstractClient fromClient)
    {
        this.command = command;
        this.args = args;
        this.client = fromClient;
    }


    public String getCommand()
    {
        return command;
    }


    public String getArgs()
    {
        return args;
    }


    public AbstractClient getClient()
    {
        return client;
    }
}
