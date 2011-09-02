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

import ru.sincore.Exceptions.STAException;
import ru.sincore.conf.Vars;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;
import ru.sincore.ClientHandler;
import ru.sincore.ClientNod;
import ru.sincore.SimpleHandler;

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
     */

    public RCM(ClientHandler cur_client, String State, String Issued_Command)
            throws STAException
    {
        if (State.equals("IDENTIFY") || State.equals("VERIFY") || State.equals("PROTOCOL"))
        {
            new STAError(cur_client,
                         100 + Constants.STA_INVALID_STATE,
                         "RCM Invalid State.",
                         "FC",
                         Issued_Command.substring(0, 4));
            return;
        }
        if (!cur_client.reg.overridespam)
        {
            switch (Issued_Command.charAt(0))
            {
                case 'B':
                    if (Vars.BRCM != 1)
                    {
                        new STAError(cur_client, 100, "RCM Invalid Context B");
                        return;
                    }
                    break;
                case 'E':
                    if (Vars.ERCM != 1)
                    {
                        new STAError(cur_client, 100, "RCM Invalid Context E");
                        return;
                    }
                    break;
                case 'D':
                    if (Vars.DRCM != 1)
                    {
                        new STAError(cur_client, 100, "RCM Invalid Context D");
                        return;
                    }
                    break;
                case 'F':
                    if (Vars.FRCM != 1)
                    {
                        new STAError(cur_client, 100, "RCM Invalid Context F");
                        return;
                    }
                    break;
                case 'H':
                    if (Vars.HRCM != 1)
                    {
                        new STAError(cur_client, 100, "RCM Invalid Context H");
                        return;
                    }

            }
        }
        if (Issued_Command.charAt(0) == 'D' || Issued_Command.charAt(0) == 'E')
        {
            StringTokenizer tok = new StringTokenizer(Issued_Command);
            String aux = tok.nextToken();
            aux = tok.nextToken();
            if (!aux.equals(cur_client.SessionID))
            {
                new STAError(cur_client,
                             200 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             "Protocol Error.Wrong SID supplied.");
                return;
            }
            aux = tok.nextToken();
            //now must look for the aux SID...
            for (ClientNod temp : SimpleHandler.getUsers())
            {
                if (temp.cur_client.SessionID.equals(aux))
                {
                    aux =
                            tok.nextToken(); // this is the string representing protocol, next token is port, next token is TO

                    temp.cur_client.sendToClient(Issued_Command);
                    if (Issued_Command.charAt(0) == 'E')
                    {
                        cur_client.sendToClient(Issued_Command);
                    }
                }

            }
            //talking to inexisting client
            return; //not kick, maybe the other client just left after he sent the msg;

        }
    }

}
