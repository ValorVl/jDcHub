/*
 * CTM.java
 *
 * Created on 24 septembrie 2007, 19:57
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
import ru.sincore.ClientManager;
import ru.sincore.ConfigLoader;
import ru.sincore.Exceptions.STAException;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

import java.util.StringTokenizer;

/**
 * Class that basically implements the CTM adc command.
 *
 * @author Pietricica
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-08
 */
public class CTM
{

    /**
     * Creates a new instance of CTM
     * @param client reference to client
     * @param state command state. See ADC protocol specs.
     * @param command incoming command // TODO realy?
     * @throws STAException exception, cause the something gone wrong =)
     */
    public CTM(Client client, String state, String command)
            throws STAException
    {
        ClientHandler cur_client = client.getClientHandler();

        if (cur_client.ACTIVE == 0)
        {
            new STAError(client, 100, "Error: Must be TCP active to use CTM.");
            return;
        }
        if (state.equals("IDENTIFY") || state.equals("VERIFY") || state.equals("PROTOCOL"))
        {
            new STAError(client,
                         100 + Constants.STA_INVALID_STATE,
                         "CTM Invalid State.",
                         "FC",
                         command.substring(0, 4));
            return;
        }

        if (!cur_client.reg.overridespam)
        {
            switch (command.charAt(0))
            {
                case 'B':
                    if (ConfigLoader.ADC_BCTM != 1)
                    {
                        new STAError(client, 100, "CTM Invalid Context B");
                        return;
                    }
                    break;
                case 'E':
                    if (ConfigLoader.ADC_ECTM != 1)
                    {
                        new STAError(client, 100, "CTM Invalid Context E");
                        return;
                    }
                    break;
                case 'D':
                    if (ConfigLoader.ADC_DCTM != 1)
                    {
                        new STAError(client, 100, "CTM Invalid Context D");
                        return;
                    }
                    break;
                case 'F':
                    if (ConfigLoader.ADC_FCTM != 1)
                    {
                        new STAError(client, 100, "CTM Invalid Context F");
                        return;
                    }
                    break;
                case 'H':
                    if (ConfigLoader.ADC_HCTM != 1)
                    {
                        new STAError(client, 100, "CTM Invalid Context H");
                        return;
                    }

            }
        }

        /*  if(System.currentTimeMillis()-handler.LastCTM<1000*30)
    {
    if(!(handler.reg.overridespam))
    {
        new STAError(handler,0,"CTM spam.");
        return;
    }
    }
    else
        handler.LastCTM=System.currentTimeMillis();*/

        if (command.charAt(0) == 'D' || command.charAt(0) == 'E')
        {
            StringTokenizer tok = new StringTokenizer(command);
            String aux = tok.nextToken();
            aux = tok.nextToken();
            if (!aux.equals(cur_client.SessionID))
            {
                new STAError(client,
                             200 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             "Protocol Error. Wrong SID supplied.");
                return;
            }
            aux = tok.nextToken();
            //now must look for the aux SID...
            for (Client targetClient : ClientManager.getInstance().getClients())
            {
                if (targetClient.getClientHandler().validated == 1)
                {
                    if (targetClient.getClientHandler().SessionID.equals(aux))
                    {
                        aux = tok.nextToken(); // this is the string representing protocol, next token is port, next token is TO

                        targetClient.getClientHandler().sendToClient(command);
                        if (command.charAt(0) == 'E')
                        {
                            cur_client.sendToClient(command);
                        }
                    }
                }

            }

            //talking to inexisting client
            //not kick, maybe the other client just left after he sent the msg;
        }
    }

}
