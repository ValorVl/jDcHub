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
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

import java.util.StringTokenizer;

/**
 * Implementation of the AdcUtils SUP command, feature checker of clients.
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
     * @param state command state. See AdcUtils protocol specs.
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
            /*
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
            */
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
            new STAError(client, 200 + Constants.STA_INVALID_STATE, "SUP Invalid state.");
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
                cur_client.isPing = enable;
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
            /* handler. sendToClient(AdcUtils.Init);


              handler.sendToClient(AdcUtils.ISID+" "+handler.SID);
             if(Vars.HubDE.equals (""))
                 handler.sendToClient("IINF CT32 VE"+AdcUtils.toAdcString (Vars.HubVersion)+" NI"+AdcUtils.toAdcString(Vars.HubName));
             else
                handler. sendToClient("IINF CT32 VE"+AdcUtils.toAdcString (Vars.HubVersion)+" NI"+AdcUtils.toAdcString(Vars.HubName)+ " DE"+AdcUtils.toAdcString(Vars.HubDE));
            */
            new STAError(client,
                         100 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Your client uses a very old AdcUtils version. Please update in order to connect to this hub. You can get a new version usually by visiting the developer's webpage from Help/About menu.");
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


//        if (state.equals("PROTOCOL"))
//        {
//            cur_client.sendToClient(AdcUtils.Init);
//
//
//            cur_client.sendToClient(AdcUtils.ISID + " " + cur_client.SID);
//            cur_client.sendToClient("IINF CT32 VE" +
//                                    AdcUtils.toAdcString(ConfigurationManager.instance().getString(ConfigurationManager.HUB_VERSION)) +
//                                    " NI" +
//                                    AdcUtils.toAdcString(ConfigurationManager.instance().getString(ConfigurationManager.HUB_NAME)) +
//                                    // if HUB_DE is not empty, return it
//                                    (!ConfigLoader.HUB_DE.isEmpty() ?
//                                     " DE" + AdcUtils.toAdcString(ConfigLoader.HUB_DE) :
//                                     "") +
//                                    // if client is PINGer than return PingString else empty string
//                                    (cur_client.isPingExtensionSupports ? AdcUtils.getPingString() : ""));
//
//            cur_client.sendToClient("ISTA 000 " +
//                                    // TODO replace String#replace() usage by normal external function
//                                    ConfigurationManager.instance().getString(ConfigurationManager.HUB_GREETING).replace(" ", "\\s") +
//                                    (ConfigurationManager.instance().getBoolean(ConfigurationManager.ENABLE_ADCS) ? "\\sin\\sAdcUtils\\sSecure\\smode" :
//                                     "") +
//                                    ".\nISTA 000 Hub\\sis\\sup\\ssince\\s" +
//                                    Main.server
//                                            .MyCalendar
//                                            .getTime()
//                                            .toString()
//                                            .replaceAll(" ", "\\\\s"));
//        }
   }

}
