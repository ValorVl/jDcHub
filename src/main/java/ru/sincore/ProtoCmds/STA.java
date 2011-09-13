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

import ru.sincore.Client;
import ru.sincore.ClientHandler;
import ru.sincore.ConfigLoader;
import ru.sincore.Exceptions.STAException;
import ru.sincore.SessionManager;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

import java.util.StringTokenizer;

/**
 * Basic implementation of ADC STA command, in client to hub context.
 *
 * @author Pietricica
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-08
 */
public class STA
{

    /**
     * Creates a new instance of STA
     * @param client reference to client
     * @param state command state. See ADC protocol specs.
     * @param command incoming command // TODO really?
     * @throws STAException exception, cause the something gone wrong =)
     */
    public STA(Client client, String state, String command)
            throws STAException
    {
        ClientHandler cur_client = client.getClientHandler();

        if (command.charAt(0) == 'B')
        {
            if (!cur_client.reg.overridespam)
            {
                if (ConfigLoader.ADC_BSTA == 0)
                {
                    cur_client.sendFromBot("STA invalid context B");
                }
            }
        }
        else if (command.charAt(0) == 'D')
        {
            if (!cur_client.reg.overridespam)
            {
                if (ConfigLoader.ADC_DSTA == 0)
                {
                    cur_client.sendFromBot("STA invalid context D");
                    return;
                }
            }
            StringTokenizer TK = new StringTokenizer(command);
            TK.nextToken();
            if (!TK.hasMoreTokens())
            {
                new STAError(client,
                             100 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             "Must supply SID");
                return;
            }
            String cursid = TK.nextToken();
            if (!cursid.equals(cur_client.SessionID))
            {
                new STAError(client,
                             100 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             "Protocol Error.Wrong SID supplied.");
                return;
            }
            if (!TK.hasMoreTokens())
            {
                new STAError(client,
                             140 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             "Must supply target SID");
                return;
            }
            String dsid = TK.nextToken();
            for (Client targetClient : SessionManager.getUsers())
            {
                if (targetClient.getClientHandler().validated == 1)
                {
                    if (targetClient.getClientHandler().SessionID.equals(dsid))

                    {
                        targetClient.getClientHandler().sendToClient(command);
                        return;
                    }
                }
            }

            new STAError(client, 100, "Invalid Target Sid.");
        }
        else if (command.charAt(0) == 'E')
        {
            if (!cur_client.reg.overridespam)
            {
                if (ConfigLoader.ADC_ESTA == 0)
                {
                    cur_client.sendFromBot("STA invalid context E");
                    return;
                }
            }
            StringTokenizer TK = new StringTokenizer(command);
            TK.nextToken();
            if (!TK.hasMoreTokens())
            {
                new STAError(client, 100, "Must supply SID");
                return;
            }
            String cursid = TK.nextToken();
            if (!cursid.equals(cur_client.SessionID))
            {
                new STAError(client,
                             200 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             "Protocol Error.Wrong SID supplied.");
                return;
            }
            if (!TK.hasMoreTokens())
            {
                new STAError(client,
                             100 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             "Must supply SID");
                return;
            }
            String esid = TK.nextToken();
            for (Client targetClient : SessionManager.getUsers())
            {
                if (targetClient.getClientHandler().validated == 1)
                {
                    if (targetClient.getClientHandler().SessionID.equals(esid))
                    {
                        targetClient.getClientHandler().sendToClient(command);
                        cur_client.sendToClient(command);
                    }
                }
            }

            new STAError(client, 100, "Invalid Target Sid.");
        }
        else if (command.charAt(0) == 'F')
        {
            if (!cur_client.reg.overridespam)
            {
                if (ConfigLoader.ADC_FSTA == 0)
                {
                    cur_client.sendFromBot("STA invalid context F");
                }
            }
        }
        else if (command.charAt(0) == 'H')
        {
            if (!cur_client.reg.overridespam)
            // ok, client has an error. what can i do about it? :))
            {
                if (ConfigLoader.ADC_HSTA == 0)
                {
                    cur_client.sendFromBot("STA invalid context H");
                }
            }
        }
    }

}
