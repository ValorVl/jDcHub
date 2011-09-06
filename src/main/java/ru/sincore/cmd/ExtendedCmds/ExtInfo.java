/*
 * ExtInfo.java
 *
 * Created on 10 septembrie 2007, 22:41
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

package ru.sincore.cmd.ExtendedCmds;

import ru.sincore.SessionManager;
import ru.sincore.TigerImpl.Base32;
import ru.sincore.util.ADC;
import ru.sincore.ClientHandler;
import ru.sincore.Client;

import java.util.StringTokenizer;

/**
 * The client Info command, also with extended call.
 *
 * @author Pietricica
 */
public class ExtInfo
{

    /**
     * Creates a new instance of ExtInfo
     */
    public ExtInfo(ClientHandler cur_client, String recvbuf)
    {
        StringTokenizer ST = new StringTokenizer(recvbuf);
        ST.nextToken();
        String aux = ST.nextToken(); //the thing to check;
        while (ST.hasMoreTokens())
        {
            aux += ST.nextToken();
        }
        aux = ADC.retADCStr(aux);
        Client tempx = null;

        if (ADC.isIP(aux))//we have an IP address
        {
            //Client temp=Client.FirstClient.NextClient;
            String Nicklist = "";
            for (Client temp : SessionManager.getUsers())
            {
                if (temp.handler.userok == 1)
                {
                    if ((temp.handler.RealIP.equals(aux.toLowerCase())))
                    {
                        Nicklist = Nicklist + temp.handler.NI + "\n";
                    }
                }


            }
            if (!Nicklist.equals(""))
            {
                cur_client.sendFromBot("Users with IP " +
                                       aux +
                                       " :\n" +
                                       Nicklist.substring(0, Nicklist.length() - 1));
            }
            else
            {
                cur_client.sendFromBot("No user with IP " + aux);
            }

        }
        else
        {

            //ok now lets see if its a valid CID
            if (aux.length() == 39)
            {
                try
                {
                    Base32.decode(aux);
                    //ok if we are here, its a CID
                    for (Client temp : SessionManager.getUsers())
                    {
                        if (temp.handler.userok == 1)
                        {
                            if ((temp.handler.ID.equals(aux)))
                            {
                                cur_client.sendFromBot("CID " +
                                                       aux +
                                                       " is used by:\n" +
                                                       temp.handler.NI);
                                return;
                            }
                        }


                    }

                    cur_client.sendFromBot("Nobody is using " + aux);
                    return;
                }
                catch (IllegalArgumentException e)
                {
                    //its a nick though...
                }
            }


            for (Client temp : SessionManager.getUsers())
            {
                if (temp.handler.userok == 1)
                {
                    if ((temp.handler.NI.toLowerCase().equals(aux.toLowerCase())))
                    {

                        String blah11 = "User Info\nNick " +
                                        ADC.retNormStr(temp.handler.NI) +
                                        "\nCID " +
                                        temp.handler.ID +
                                        "\nShare Size " +
                                        temp.handler.SS +
                                        " Bytes\n" +
                                        "Description " +
                                        (temp.handler.DE != null ?
                                         ADC.retNormStr(temp.handler.DE) :
                                         "") +
                                        "\nTag ";

                        String Tag = "<" + ADC.retNormStr(temp.handler.VE) + ",M:";
                        if (temp.handler.ACTIVE == 1)
                        {
                            Tag = Tag + "A";
                        }
                        else
                        {
                            Tag = Tag + "P";
                        }
                        Tag = Tag + ",H:" + temp.handler.HN + "/";
                        if (temp.handler.HR != null)
                        {
                            Tag = Tag + temp.handler.HR + "/";
                        }
                        else
                        {
                            Tag = Tag + "?";
                        }
                        if (temp.handler.HO != null)
                        {
                            Tag = Tag + temp.handler.HO;
                        }
                        else
                        {
                            Tag = Tag + "?";
                        }

                        Tag = Tag + ",S:";
                        if (temp.handler.SL != null)

                        {
                            Tag = Tag + temp.handler.SL + ">";
                        }
                        else
                        {
                            Tag = Tag + "?>";
                        }
                        blah11 = blah11 + Tag + "\nSupports "
                                 +
                                 ((temp.handler.SU != null) ?
                                  temp.handler.SU :
                                  "nothing special") +
                                 "\nIp address " +
                                 temp.handler.RealIP;
                        if (temp.handler.reg.isreg)
                        {

                            blah11 = blah11 + temp.handler.reg.getRegInfo();
                        }

                        else
                        {
                            blah11 = blah11 + "\nNormal user.";
                        }
                        cur_client.sendFromBot("" + blah11);
                        return;
                    }
                }


            }

            cur_client.sendFromBot("No such user online.");


        }
    }

}
