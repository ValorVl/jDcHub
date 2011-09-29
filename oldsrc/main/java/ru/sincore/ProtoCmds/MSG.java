/*
 * MSG.java
 *
 * Created on 27 septembrie 2007, 12:14
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
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;
import ru.sincore.*;

import java.util.StringTokenizer;

/**
 * Implementation of the MSG command in AdcUtils protocol.
 *
 * @author Pietricica
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-08
 */
public class MSG
{

    /**
     * Creates a new instance of MSG
     * @param client reference to client
     * @param state command state. See AdcUtils protocol specs.
     * @param command incoming command // TODO realy?
     * @throws STAException exception, cause the something gone wrong =)
     */
    public MSG(Client client, String state, String command)
            throws STAException
    {
        ClientHandler cur_client = client.getClientHandler();
        if (state.equals("IDENTIFY") || state.equals("VERIFY") || state.equals("PROTOCOL"))
        {
            new STAError(client,
                         200 + Constants.STA_INVALID_STATE,
                         "MSG Invalid state.",
                         "FC",
                         command.substring(0, 4));
            return;
        }

        StringTokenizer tok = new StringTokenizer(command, " ");
        String aux = tok.nextToken();
        if (command.charAt(0) == 'H') //for hub only, special check
        {
            if (aux.equals("Test"))
            {
                cur_client.sendFromBot("Test OK.");
            }
            return;
        }

        if (!tok.nextToken().equals(cur_client.SID))
        {
            new STAError(client, 200, "Protocol Error. Wrong SID supplied.");
            return;
        }
        String targetSID = null;
        if (command.charAt(0) == 'D' || command.charAt(0) == 'E')
        {
            targetSID = tok.nextToken();
        }
        String message = tok.nextToken();

        if (message.length() > ConfigurationManager.instance().getInt(ConfigurationManager.MAX_CHAT_MESSAGE_SIZE))
        {
            if (
                    !(cur_client.reg.overridespam))
            {
                new STAError(client, Constants.STA_SEVERITY_RECOVERABLE, "Message exceeds maximum lenght.");
                return;
            }
        }

        String pmSID = null;
        int me = 0;
        while (tok.hasMoreElements())
        {
            aux = tok.nextToken();
            if (aux.startsWith("PM"))
            {
                pmSID = aux.substring(2);
            }
            if (aux.startsWith("ME"))
            {
                if (aux.substring(2).equals("1"))
                {
                    me = 1;
                }
                else
                {
                    new STAError(client,
                                 Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                                 "MSG Invalid Flag.");
                    return;
                }
            }


        }
        long now = System.currentTimeMillis();
        if (cur_client.lastMSG != 0)
        {
            if (now - cur_client.lastMSG < ConfigurationManager.instance().getInt(ConfigurationManager.CHAT_REFRESH))
            {
                if (!cur_client.reg.overridespam)

                {
                    new STAError(client,
                                 Constants.STA_SEVERITY_RECOVERABLE,
                                 "Chatting Too Fast. Minimum chat interval " +
                                 String.valueOf(ConfigurationManager.instance().getInt(ConfigurationManager.CHAT_REFRESH)) +
                                 " .You made " +
                                 String.valueOf(now - cur_client.lastMSG) +
                                 ".");
                    return;
                }

            }
        }

        cur_client.lastMSG = now;


        if (command.charAt(0) == 'B') //broadcast
        {
            if (targetSID != null)
            {
                new STAError(client, Constants.STA_SEVERITY_RECOVERABLE, "MSG Can't Broadcast PM.");
                return;
            }

            Broadcast.getInstance().broadcast(command);
        }
        else if (command.charAt(0) == 'E') //echo direct msg
        {
            if (targetSID == null)
            {
                new STAError(client, Constants.STA_SEVERITY_RECOVERABLE, "MSG Can't PM to Nobody.");
                return;
            }
            if (!pmSID.equals(cur_client.SID))
            {
                new STAError(client, Constants.STA_SEVERITY_RECOVERABLE, "MSG PM not returning to self.");
                return;
            }

            if (!targetSID.equals("ABCD"))
            {
                Client tempClient = ClientManager.getInstance().getClientBySID(targetSID);
                if (tempClient != null)
                {
                    tempClient.getClientHandler().sendToClient(command);
                    cur_client.sendToClient(command);
                    return;
                }
                else
                {
                    //talking to inexisting client
                    new STAError(client,
                                 Constants.STA_SEVERITY_RECOVERABLE,
                                 "MSG User not found."); //not kick, maybe the other client just left after he sent the msg;
                    return;
                }
            }
            else
            {
                //talking to bot
                //must send to all ops...

                //cant broadcast coz must send each;s SID
                for (Client targetClient : SessionManager.getUsers())
                {
                    if (targetClient.getClientHandler().validated == 1)
                    {
                        if (targetClient.getClientHandler().reg.isreg && !targetClient.equals(client))
                        {
                            targetClient.getClientHandler()
                                    .sendToClient("EMSG " +
														  cur_client.SID +
														  " " +
														  targetClient.getClientHandler().SID +
														  " " +
														  message +
														  " PMABCD");
                        }
                    }

                }
            }
            cur_client.sendToClient(command);
        }
        else if (command.charAt(0) == 'D') //direct direct msg
        {
            if (targetSID == null)
            {
                new STAError(client, Constants.STA_SEVERITY_RECOVERABLE, "MSG Can't PM to Nobody.");
                return;
            }
            if ((pmSID != null) && (pmSID.equals(cur_client.SID)))
            {
                new STAError(client, Constants.STA_SEVERITY_RECOVERABLE, "MSG PM not returning to self.");
                return;
            }

            if (!targetSID.equals("ABCD"))
            {
                Client tempClient = ClientManager.getInstance().getClientBySID(targetSID);
                if (tempClient != null)
                {
                    tempClient.getClientHandler().sendToClient(command);
                }
                else
                //talking to inexisting client
                {
                    new STAError(client,
                                 Constants.STA_SEVERITY_RECOVERABLE,
                                 "MSG User not found."); //not kick, maybe the other client just left after he sent the msg;
                }
            }
            else
            {
                //talking to bot
                //must send to all ops...

                for (Client tempClient : SessionManager.getUsers())
                {
                    if (tempClient.getClientHandler().reg.isreg && !tempClient.equals(client))
                    {
                        tempClient.getClientHandler()
                                .sendToClient("DMSG " +
                                              cur_client.SID +
                                              " " +
                                              tempClient.getClientHandler().SID +
                                              " " +
                                              message +
                                              " PMABCD");
                    }

                }
            }
        }
        else
        {
            new STAError(client, Constants.STA_SEVERITY_RECOVERABLE, "MSG Invalid Context");
        }
    }

}
