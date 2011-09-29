/*
 * RES.java
 *
 * Created on 25 septembrie 2007, 15:10
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
import ru.sincore.Exceptions.STAException;
import ru.sincore.SessionManager;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

import java.util.StringTokenizer;

/**
 * Basic implementation of the AdcUtils RES command.
 *
 * @author Pietricica
 */
public class RES
{

    /**
     * Creates a new instance of RES
     * @param client reference to client
     * @param state command state. See AdcUtils protocol specs.
     * @param command incoming command // TODO realy?
     * @throws STAException exception, cause the something gone wrong =)
     */
    public RES(Client client, String state, String command)
            throws STAException
    {
        ClientHandler cur_client = client.getClientHandler();
        if (cur_client.active == 0)
        {
            new STAError(client, 100, "Error: Must be TCP active to use RES.");
            return;
        }
        if (state.equals("IDENTIFY") || state.equals("VERIFY") || state.equals("PROTOCOL"))
        {
            new STAError(client,
                         100 + Constants.STA_INVALID_STATE,
                         "RES Invalid state.",
                         "FC",
                         command.substring(0, 4));
            return;
        }
        if (!cur_client.reg.overridespam)
        {
            switch (command.charAt(0))
            {
                case 'B':
                    if (ConfigLoader.ADC_BRES != 1)
                    {
                        new STAError(client, 100, "RES Invalid Context B");
                        return;
                    }
                    break;
                case 'E':
                    if (ConfigLoader.ADC_ERES != 1)
                    {
                        new STAError(client, 140, "RES Invalid Context E");
                        return;
                    }
                    break;
                case 'D':
                    if (ConfigLoader.ADC_DRES != 1)
                    {
                        new STAError(client, 100, "RES Invalid Context D");
                        return;
                    }
                    break;
                case 'F':
                    if (ConfigLoader.ADC_FRES != 1)
                    {
                        new STAError(client, 100, "RES Invalid Context F");
                        return;
                    }
                    break;
                case 'H':
                    if (ConfigLoader.ADC_HRES != 1)
                    {
                        new STAError(client, 100, "RES Invalid Context H");
                        return;
                    }

            }
        }
        if (command.charAt(0) == 'D' || command.charAt(0) == 'E')
        {
            StringTokenizer tok = new StringTokenizer(command);

            // TODO is it realy works?
            String aux = tok.nextToken();
            aux = tok.nextToken();
            if (!aux.equals(cur_client.SID))
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
                if (targetClient.getClientHandler().SID.equals(aux))
                {
                    aux = tok.nextToken(); // this is the effective result

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
