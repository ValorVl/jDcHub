/*
 * STA.java
 *
 * Created on 08 septembrie 2007, 12:09
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

package ru.sincore.ProtoCmds;

import ru.sincore.Exceptions.STAException;
import ru.sincore.conf.Vars;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;
import ru.sincore.ClientHandler;
import ru.sincore.ClientNod;
import ru.sincore.SimpleHandler;

import java.util.StringTokenizer;

/**
 * Basic implementation of ADC STA command, in client to hub context.
 *
 * @author Pietricica
 */
public class STA
{

    /**
     * Creates a new instance of STA
     */
    public STA(ClientHandler cur_client, String recvbuf, String State)
            throws STAException
    {
        if (recvbuf.charAt(0) == 'B')
        {
            if (!cur_client.reg.overridespam)
            {
                if (Vars.BSTA == 0)
                {
                    cur_client.sendFromBot("STA invalid context B");
                    return;
                }
            }
        }
        else if (recvbuf.charAt(0) == 'D')
        {
            if (!cur_client.reg.overridespam)
            {
                if (Vars.DSTA == 0)
                {
                    cur_client.sendFromBot("STA invalid context D");
                    return;
                }
            }
            StringTokenizer TK = new StringTokenizer(recvbuf);
            TK.nextToken();
            if (!TK.hasMoreTokens())
            {
                new STAError(cur_client,
                             100 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             "Must supply SID");
                return;
            }
            String cursid = TK.nextToken();
            if (!cursid.equals(cur_client.SessionID))
            {
                new STAError(cur_client,
                             100 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             "Protocol Error.Wrong SID supplied.");
                return;
            }
            if (!TK.hasMoreTokens())
            {
                new STAError(cur_client,
                             140 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             "Must supply target SID");
                return;
            }
            String dsid = TK.nextToken();
            for (ClientNod target : SimpleHandler.getUsers())
            {
                if (target.cur_client.userok == 1)
                {
                    if (target.cur_client.SessionID.equals(dsid))

                    {
                        target.cur_client.sendToClient(recvbuf);
                        return;
                    }
                }
            }

            new STAError(cur_client, 100, "Invalid Target Sid.");
            return;


        }
        else if (recvbuf.charAt(0) == 'E')
        {
            if (!cur_client.reg.overridespam)
            {
                if (Vars.ESTA == 0)
                {
                    cur_client.sendFromBot("STA invalid context E");
                    return;
                }
            }
            StringTokenizer TK = new StringTokenizer(recvbuf);
            TK.nextToken();
            if (!TK.hasMoreTokens())
            {
                new STAError(cur_client, 100, "Must supply SID");
                return;
            }
            String cursid = TK.nextToken();
            if (!cursid.equals(cur_client.SessionID))
            {
                new STAError(cur_client,
                             200 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             "Protocol Error.Wrong SID supplied.");
                return;
            }
            if (!TK.hasMoreTokens())
            {
                new STAError(cur_client,
                             100 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             "Must supply SID");
                return;
            }
            String esid = TK.nextToken();
            for (ClientNod target : SimpleHandler.getUsers())
            {
                if (target.cur_client.userok == 1)
                {
                    if (target.cur_client.SessionID.equals(esid))
                    {
                        target.cur_client.sendToClient(recvbuf);
                        cur_client.sendToClient(recvbuf);
                    }
                }
            }

            new STAError(cur_client, 100, "Invalid Target Sid.");
            return;


        }
        else if (recvbuf.charAt(0) == 'F')
        {
            if (!cur_client.reg.overridespam)
            {
                if (Vars.FSTA == 0)
                {
                    cur_client.sendFromBot("STA invalid context F");
                    return;
                }
            }
        }
        else if (recvbuf.charAt(0) == 'H')
        {
            if (!cur_client.reg.overridespam)
            // ok, client has an error. what can i do about it? :))
            {
                if (Vars.HSTA == 0)
                {
                    cur_client.sendFromBot("STA invalid context H");
                    return;
                }
            }
        }
    }

}
