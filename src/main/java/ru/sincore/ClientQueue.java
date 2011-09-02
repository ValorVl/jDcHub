/*
 * ClientQueue.java
 *
 * Created on 24 aprilie 2007, 20:49
 *
 * DSHub ADC HubSoft
 * Copyright (C) 2007,2008  Eugen Hristev
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

package ru.sincore;


/**
 * Basic message queue for a specific user, keeps messages in a chained list and sends them periodically,
 * to avoid bottleneck on specific slowlike users, allows broadcasts to go on.
 *
 * @author Pietricica
 */
public class ClientQueue
{

    ClientHandler cur_client;


    static public class msgnod
    {
        String MSG;
        msgnod Next;


        public msgnod()
        {
            Next = null;
        }
    }


    ;

    msgnod First, Last;


    /**
     * Creates a new instance of ClientQueue
     */
    public ClientQueue(ClientHandler CH)
    {
        cur_client = CH;

        First = null;
        Last = null;

    }


    public void addMsg(String newmsg)
    {
        msgnod Bla = new msgnod();
        if (First == null)
        {
            First = Bla;
        }
        if (Last == null)
        {
            Last = Bla;
        }
        Last.Next = Bla;
        Last = Bla;
        Bla.MSG = newmsg;


    }


}
