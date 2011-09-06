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

import ru.sincore.Exceptions.STAException;
import ru.sincore.SessionManager;
import ru.sincore.conf.Vars;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;
import ru.sincore.ClientHandler;
import ru.sincore.Client;

import java.util.StringTokenizer;

/**
 * Class that basically implements the CTM adc command.
 *
 * @author Pietricica
 */
public class CTM
{

    /**
     * Creates a new instance of CTM
     */
    public CTM(ClientHandler cur_client, String State, String Issued_Command)
            throws STAException
    {
        if (cur_client.ACTIVE == 0)
        {
            new STAError(cur_client, 100, "Error: Must be TCP active to use CTM.");
            return;
        }
        if (State.equals("IDENTIFY") || State.equals("VERIFY") || State.equals("PROTOCOL"))
        {
            new STAError(cur_client,
                         100 + Constants.STA_INVALID_STATE,
                         "CTM Invalid State.",
                         "FC",
                         Issued_Command.substring(0, 4));
            return;
        }

        if (!cur_client.reg.overridespam)
        {
            switch (Issued_Command.charAt(0))
            {
                case 'B':
                    if (Vars.BCTM != 1)
                    {
                        new STAError(cur_client, 100, "CTM Invalid Context B");
                        return;
                    }
                    break;
                case 'E':
                    if (Vars.ECTM != 1)
                    {
                        new STAError(cur_client, 100, "CTM Invalid Context E");
                        return;
                    }
                    break;
                case 'D':
                    if (Vars.DCTM != 1)
                    {
                        new STAError(cur_client, 100, "CTM Invalid Context D");
                        return;
                    }
                    break;
                case 'F':
                    if (Vars.FCTM != 1)
                    {
                        new STAError(cur_client, 100, "CTM Invalid Context F");
                        return;
                    }
                    break;
                case 'H':
                    if (Vars.HCTM != 1)
                    {
                        new STAError(cur_client, 100, "CTM Invalid Context H");
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

        if (Issued_Command.charAt(0) == 'D' || Issued_Command.charAt(0) == 'E')
        {
            StringTokenizer tok = new StringTokenizer(Issued_Command);
            String aux = tok.nextToken();
            aux = tok.nextToken();
            if (!aux.equals(cur_client.SessionID))
            {
                new STAError(cur_client,
                             200 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             "Protocol Error. Wrong SID supplied.");
                return;
            }
            aux = tok.nextToken();
            //now must look for the aux SID...
            for (Client temp : SessionManager.getUsers())
            {
                if (temp.handler.userok == 1)
                {
                    if (temp.handler.SessionID.equals(aux))
                    {
                        aux =
                                tok.nextToken(); // this is the string representing protocol, next token is port, next token is TO

                        temp.handler.sendToClient(Issued_Command);
                        if (Issued_Command.charAt(0) == 'E')
                        {
                            cur_client.sendToClient(Issued_Command);
                        }
                    }
                }

            }
            //talking to inexisting client
            return; //not kick, maybe the other client just left after he sent the msg;

        }
    }

}
