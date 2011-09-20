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
import ru.sincore.util.AdcUtils;
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
        aux = AdcUtils.retADCStr(aux);
        Client tempx = null;

        if (AdcUtils.isIP(aux))//we have an IP address
        {
            //Client temp=Client.FirstClient.NextClient;
            String Nicklist = "";
            for (Client temp : SessionManager.getUsers())
            {
                if (temp.getClientHandler().validated == 1)
                {
                    if ((temp.getClientHandler().RealIP.equals(aux.toLowerCase())))
                    {
                        Nicklist = Nicklist + temp.getClientHandler().NI + "\n";
                    }
                }


            }
            if (!Nicklist.equals(""))
            {
                cur_client.sendFromBot("users with IP " +
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
                        if (temp.getClientHandler().validated == 1)
                        {
                            if ((temp.getClientHandler().ID.equals(aux)))
                            {
                                cur_client.sendFromBot("CID " +
                                                       aux +
                                                       " is used by:\n" +
                                                       temp.getClientHandler().NI);
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
                if (temp.getClientHandler().validated == 1)
                {
                    if ((temp.getClientHandler().NI.toLowerCase().equals(aux.toLowerCase())))
                    {

                        String blah11 = "User Info\nNick " +
                                        AdcUtils.retNormStr(temp.getClientHandler().NI) +
                                        "\nCID " +
                                        temp.getClientHandler().ID +
                                        "\nShare Size " +
                                        temp.getClientHandler().SS +
                                        " Bytes\n" +
                                        "Description " +
                                        (temp.getClientHandler().DE != null ?
                                         AdcUtils.retNormStr(temp.getClientHandler().DE) :
                                         "") +
                                        "\nTag ";

                        String Tag = "<" + AdcUtils.retNormStr(temp.getClientHandler().VE) + ",M:";
                        if (temp.getClientHandler().ACTIVE == 1)
                        {
                            Tag = Tag + "A";
                        }
                        else
                        {
                            Tag = Tag + "P";
                        }
                        Tag = Tag + ",H:" + temp.getClientHandler().HN + "/";
                        if (temp.getClientHandler().HR != null)
                        {
                            Tag = Tag + temp.getClientHandler().HR + "/";
                        }
                        else
                        {
                            Tag = Tag + "?";
                        }
                        if (temp.getClientHandler().HO != null)
                        {
                            Tag = Tag + temp.getClientHandler().HO;
                        }
                        else
                        {
                            Tag = Tag + "?";
                        }

                        Tag = Tag + ",S:";
                        if (temp.getClientHandler().SL != null)

                        {
                            Tag = Tag + temp.getClientHandler().SL + ">";
                        }
                        else
                        {
                            Tag = Tag + "?>";
                        }
                        blah11 = blah11 + Tag + "\nSupports "
                                 +
                                 ((temp.getClientHandler().SU != null) ?
                                  temp.getClientHandler().SU :
                                  "nothing special") +
                                 "\nIp address " +
                                 temp.getClientHandler().RealIP;
                        if (temp.getClientHandler().reg.isreg)
                        {

                            blah11 = blah11 + temp.getClientHandler().reg.getRegInfo();
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
