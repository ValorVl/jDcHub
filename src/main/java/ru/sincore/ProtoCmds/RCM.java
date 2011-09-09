/*
 * RCM.java
 *
 * Created on 25 septembrie 2007, 12:39
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
 * Basic implementation of the ADC RCM command.
 *
 * @author Pietricica
 */
public class RCM
{

    /**
     * Creates a new instance of RCM
     * @param client reference to client
     * @param state command state. See ADC protocol specs.
     * @param command incoming command // TODO realy?
     * @throws STAException exception, cause the something gone wrong =)
     */
    public RCM(Client client, String state, String command)
            throws STAException
    {
        ClientHandler cur_client = client.getClientHandler();

        if (state.equals("IDENTIFY") || state.equals("VERIFY") || state.equals("PROTOCOL"))
        {
            new STAError(client,
                         100 + Constants.STA_INVALID_STATE,
                         "RCM Invalid State.",
                         "FC",
                         command.substring(0, 4));
            return;
        }
        if (!cur_client.reg.overridespam)
        {
            switch (command.charAt(0))
            {
                case 'B':
                    if (ConfigLoader.ADC_BRCM != 1)
                    {
                        new STAError(client, 100, "RCM Invalid Context B");
                        return;
                    }
                    break;
                case 'E':
                    if (ConfigLoader.ADC_ERCM != 1)
                    {
                        new STAError(client, 100, "RCM Invalid Context E");
                        return;
                    }
                    break;
                case 'D':
                    if (ConfigLoader.ADC_DRCM != 1)
                    {
                        new STAError(client, 100, "RCM Invalid Context D");
                        return;
                    }
                    break;
                case 'F':
                    if (ConfigLoader.ADC_FRCM != 1)
                    {
                        new STAError(client, 100, "RCM Invalid Context F");
                        return;
                    }
                    break;
                case 'H':
                    if (ConfigLoader.ADC_HRCM != 1)
                    {
                        new STAError(client, 100, "RCM Invalid Context H");
                        return;
                    }

            }
        }
        if (command.charAt(0) == 'D' || command.charAt(0) == 'E')
        {
            StringTokenizer tok = new StringTokenizer(command);
            String aux = tok.nextToken();
            aux = tok.nextToken();
            if (!aux.equals(cur_client.SessionID))
            {
                new STAError(client,
                             200 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             "Protocol Error.Wrong SID supplied.");
                return;
            }
            aux = tok.nextToken();
            //now must look for the aux SID...
            for (Client targetClient : SessionManager.getUsers())
            {
                if (targetClient.getClientHandler().SessionID.equals(aux))
                {
                    aux =
                            tok.nextToken(); // this is the string representing protocol, next token is port, next token is TO

                    targetClient.getClientHandler().sendToClient(command);
                    if (command.charAt(0) == 'E')
                    {
                        cur_client.sendToClient(command);
                    }
                }

            }

            //talking to inexisting client
            //not kick, maybe the other client just left after he sent the msg;
        }
    }

}
