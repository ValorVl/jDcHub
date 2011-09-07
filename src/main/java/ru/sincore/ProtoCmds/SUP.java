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
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.conf.Vars;
import ru.sincore.util.ADC;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;
import ru.sincore.ClientHandler;
import ru.sincore.Main;

import java.util.StringTokenizer;

/**
 * Implementation of the ADC SUP command, feature checker of clients.
 *
 * @author Pietricica
 */
public class SUP
{

    /**
     * Creates a new instance of SUP
     */
    public SUP(Client client, String State, String Issued_Command)
            throws STAException, CommandException
    {
        ClientHandler cur_client = client.getClientHandler();
        if (!cur_client.reg.overridespam)
        {
            switch (Issued_Command.charAt(0))
            {
                case 'B':
                    if (Vars.BSUP != 1)
                    {
                        new STAError(client, 100, "SUP Invalid Context B");
                        return;
                    }
                    break;
                case 'E':
                    if (Vars.ESUP != 1)
                    {
                        new STAError(client, 100, "SUP Invalid Context E");
                        return;
                    }
                    break;
                case 'D':
                    if (Vars.DSUP != 1)
                    {
                        new STAError(client, 100, "SUP Invalid Context D");
                        return;
                    }
                    break;
                case 'F':
                    if (Vars.FSUP != 1)
                    {
                        new STAError(client, 100, "SUP Invalid Context F");
                        return;
                    }
                    break;
                case 'H':
                    if (Vars.HSUP != 1)
                    {
                        new STAError(client, 100, "SUP Invalid Context H");
                        return;
                    }

            }
        }

        if (Issued_Command.charAt(0) != 'H')
        {
            if (State.equals("PROTOCOL"))
            {
                throw new CommandException("FAIL state:PROTOCOL reason:NOT BASE CLIENT");
            }

        }
        if (State.equals("VERIFY") || State.equals("IDENTIFY"))
        {
            new STAError(client, 200 + Constants.STA_INVALID_STATE, "SUP Invalid State.");
            return;
        }
        Issued_Command = Issued_Command.substring(4);
        StringTokenizer tok = new StringTokenizer(Issued_Command);
        while (tok.hasMoreTokens())
        {
            String aux = tok.nextToken();
            boolean enable;
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
                    cur_client.bas0 = true;
                    cur_client.base = 1;
                }
                if (aux.equals("BASE"))
                {
                    cur_client.base = 2;
                }
                if (aux.startsWith("PIN") && aux.length() == 4)
                {
                    cur_client.ping = true;
                }
                if (aux.startsWith("UCM") && aux.length() == 4)
                {
                    cur_client.ucmd = 1;
                }
                if (aux.startsWith("TIGR") && aux.length() == 4)
                {
                    cur_client.tigr = true;
                }



                aux = aux.substring(2);
                if (aux.startsWith("UCM") && aux.length() == 4)
                {
                    cur_client.ucmd = 0;
                }
                if (aux.equals("BAS0"))
                {
                    cur_client.bas0 = false;
                }
                if (aux.equals("BASE"))
                {
                    cur_client.base = 0;
                }
                if (aux.startsWith("PIN") && aux.length() == 4)
                {
                    cur_client.ping = false;
                }
                if (aux.startsWith("TIG") && aux.length() == 4)
                {
                    cur_client.tigr = false;
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
            if (State.equals("PROTOCOL"))
            {
                throw new CommandException("FAIL state:PROTOCOL reason:NOT BASE CLIENT");
            }
            else if (State.equals("NORMAL"))
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


        if (State.equals("PROTOCOL"))
        {
            cur_client.sendToClient(ADC.Init);


            cur_client.sendToClient(ADC.ISID + " " + cur_client.SessionID);
            if (!cur_client.ping)
            {
                if (Vars.HubDE.equals(""))
                {
                    cur_client.sendToClient("IINF CT32 VE" +
                                            ADC.retADCStr(Vars.HubVersion) +
                                            " NI" +
                                            ADC.retADCStr(Vars.HubName));
                }
                else
                {
                    cur_client.sendToClient("IINF CT32 VE" +
                                            ADC.retADCStr(Vars.HubVersion) +
                                            " NI" +
                                            ADC.retADCStr(Vars.HubName) +
                                            " DE" +
                                            ADC.retADCStr(Vars.HubDE));
                }
            }

            else
                //its a PINGer
                if (Vars.HubDE.equals(""))
                {
                    cur_client.sendToClient("IINF CT32 VE" +
                                            ADC.retADCStr(Vars.HubVersion) +
                                            " NI" +
                                            ADC.retADCStr(Vars.HubName)
                                            +
                                            ADC.getPingString()


                                           );
                }
                else
                {
                    cur_client.sendToClient("IINF CT32 VE" +
                                            ADC.retADCStr(Vars.HubVersion) +
                                            " NI" +
                                            ADC.retADCStr(Vars.HubName) +
                                            " DE" +
                                            ADC.retADCStr(Vars.HubDE) +
                                            ADC.getPingString()
                                           );
                }


            cur_client.sendToClient("ISTA 000 " +
                                    "Running\\sKappa\\sVersion\\sof\\sDSHub" +
                                    (Vars.adcs_mode ? "\\sin\\sADC\\sSecure\\smode" :
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
