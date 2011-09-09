/*
 * SUP.java
 *
 * Created on 29 septembrie 2007, 14:56
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
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.Main;
import ru.sincore.util.ADC;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

import java.util.StringTokenizer;

/**
 * Implementation of the ADC SUP command, feature checker of clients.
 *
 * @author Pietricica
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-08
 */
public class SUP
{

    /**
     * Creates a new instance of SUP
     * @param client reference to client
     * @param state command state. See ADC protocol specs.
     * @param command incoming command // TODO realy?
     * @throws STAException exception, cause the something gone wrong =)
     * @throws CommandException exception, cause the something gone wrong =)
     */
    public SUP(Client client, String state, String command)
            throws STAException, CommandException
    {
        ClientHandler cur_client = client.getClientHandler();
        if (!cur_client.reg.overridespam)
        {
            switch (command.charAt(0))
            {
                case 'B':
                    if (ConfigLoader.ADC_BSUP != 1)
                    {
                        new STAError(client, 100, "SUP Invalid Context B");
                        return;
                    }
                    break;
                case 'E':
                    if (ConfigLoader.ADC_ESUP != 1)
                    {
                        new STAError(client, 100, "SUP Invalid Context E");
                        return;
                    }
                    break;
                case 'D':
                    if (ConfigLoader.ADC_DSUP != 1)
                    {
                        new STAError(client, 100, "SUP Invalid Context D");
                        return;
                    }
                    break;
                case 'F':
                    if (ConfigLoader.ADC_FSUP != 1)
                    {
                        new STAError(client, 100, "SUP Invalid Context F");
                        return;
                    }
                    break;
                case 'H':
                    if (ConfigLoader.ADC_HSUP != 1)
                    {
                        new STAError(client, 100, "SUP Invalid Context H");
                        return;
                    }

            }
        }

        if (command.charAt(0) != 'H')
        {
            if (state.equals("PROTOCOL"))
            {
                throw new CommandException("FAIL state:PROTOCOL reason:NOT BASE CLIENT");
            }

        }
        if (state.equals("VERIFY") || state.equals("IDENTIFY"))
        {
            new STAError(client, 200 + Constants.STA_INVALID_STATE, "SUP Invalid State.");
            return;
        }
        command = command.substring(4);
        StringTokenizer tok = new StringTokenizer(command);
        while (tok.hasMoreTokens())
        {
            String aux = tok.nextToken();
            boolean enable = false;
            if (aux.startsWith("AD"))
            {
                enable = true;
            }
            else if (aux.startsWith("RM"))
            {
                enable = false;
            }
            else
            {
                new STAError(client, 100, "Unknown SUP token (not an \'AD\' or \'RM\').");
            }

            aux = aux.substring(2);
            if (aux.equals("BAS0"))
            {
                cur_client.bas0 = enable;
                cur_client.base = 1;
            }
            else if (aux.equals("BASE"))
            {
                cur_client.base = (enable ? 2 : 0);
            }
            else if (aux.startsWith("PIN") && aux.length() == 4)
            {
                cur_client.ping = enable;
            }
            else if (aux.startsWith("UCM") && aux.length() == 4)
            {
                cur_client.ucmd = (enable ? 1 : 0);
            }
            else if (aux.startsWith("TIGR") && aux.length() == 4)
            {
                cur_client.tigr = enable;
            }
        }
        if (cur_client.bas0)
        {
            //System.out.println("bas0");
            /* handler. sendToClient(ADC.Init);


              handler.sendToClient(ADC.ISID+" "+handler.SessionID);
             if(Vars.HubDE.equals (""))
                 handler.sendToClient("IINF CT32 VE"+ADC.retADCStr (Vars.HubVersion)+" NI"+ADC.retADCStr(Vars.HubName));
             else
                handler. sendToClient("IINF CT32 VE"+ADC.retADCStr (Vars.HubVersion)+" NI"+ADC.retADCStr(Vars.HubName)+ " DE"+ADC.retADCStr(Vars.HubDE));
            */
            new STAError(client,
                         100 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Your client uses a very old ADC version. Please update in order to connect to this hub. You can get a new version usually by visiting the developer's webpage from Help/About menu.");
        }

        if (cur_client.base == 0)
        {
            if (state.equals("PROTOCOL"))
            {
                throw new CommandException("FAIL state:PROTOCOL reason:NOT BASE CLIENT");
            }
            else if (state.equals("NORMAL"))
            {
                new STAError(client,
                             200 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             "You removed BASE features therefore you can't stay on hub anymore.");
                return;
            }
        }

        if (!cur_client.tigr)
        {
            new STAError(client,
                         100 + Constants.STA_NO_HASH_OVERLAP,
                         "Cannot find any compatible hash function to use. Defaulting to TIGER.");
        }


        if (state.equals("PROTOCOL"))
        {
            cur_client.sendToClient(ADC.Init);


            cur_client.sendToClient(ADC.ISID + " " + cur_client.SessionID);
            if (!cur_client.ping)
            {
                if (ConfigLoader.HUB_DE.isEmpty())
                {
                    cur_client.sendToClient("IINF CT32 VE" +
                                            ADC.retADCStr(ConfigLoader.HUB_VERSION) +
                                            " NI" +
                                            ADC.retADCStr(ConfigLoader.HUB_NAME));
                }
                else
                {
                    cur_client.sendToClient("IINF CT32 VE" +
                                            ADC.retADCStr(ConfigLoader.HUB_VERSION) +
                                            " NI" +
                                            ADC.retADCStr(ConfigLoader.HUB_NAME) +
                                            " DE" +
                                            ADC.retADCStr(ConfigLoader.HUB_DE));
                }
            }

            else
                //its a PINGer
                if (ConfigLoader.HUB_DE.isEmpty())
                {
                    cur_client.sendToClient("IINF CT32 VE" +
                                            ADC.retADCStr(ConfigLoader.HUB_VERSION) +
                                            " NI" +
                                            ADC.retADCStr(ConfigLoader.HUB_NAME)
                                            +
                                            ADC.getPingString()


                                           );
                }
                else
                {
                    cur_client.sendToClient("IINF CT32 VE" +
                                            ADC.retADCStr(ConfigLoader.HUB_VERSION) +
                                            " NI" +
                                            ADC.retADCStr(ConfigLoader.HUB_NAME) +
                                            " DE" +
                                            ADC.retADCStr(ConfigLoader.HUB_DE) +
                                            ADC.getPingString()
                                           );
                }


            cur_client.sendToClient("ISTA 000 " +
                                    "Running\\sKappa\\sVersion\\sof\\sDSHub" +
                                    (ConfigLoader.ENABLE_ADCS ? "\\sin\\sADC\\sSecure\\smode" :
                                     "") +
                                    ".\nISTA 000 Hub\\sis\\sup\\ssince\\s" +
                                    Main.Server
                                            .MyCalendar
                                            .getTime()
                                            .toString()
                                            .replaceAll(" ", "\\\\s"));

            //handler. sendToClient("ISTA 000 "+
            //    "Running\\Iota\\sVersion\\sof\\sDSHub.\nISTA 000 Hub\\sis\\sup\\ssince\\s"+ Main.Server.MyCalendar.getTime ().toString ().replaceAll (" ","\\\\s"));

        }
    }

}
