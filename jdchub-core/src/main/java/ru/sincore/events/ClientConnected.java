/*
* ClientConnected.java
*
* Created on 11 04 2012, 14:55
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

package ru.sincore.events;

import ru.sincore.client.AbstractClient;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-04-11
 */
public class ClientConnected
{
    private AbstractClient client;


    public ClientConnected(AbstractClient client)
    {
        this.client = client;
    }


    public AbstractClient getClient()
    {
        return client;
    }
}
