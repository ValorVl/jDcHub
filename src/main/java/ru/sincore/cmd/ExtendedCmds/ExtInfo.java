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

import ru.sincore.TigerImpl.Base32;
import ru.sincore.util.ADC;
import ru.sincore.ClientHandler;
import ru.sincore.ClientNod;
import ru.sincore.SimpleHandler;

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
        ClientNod tempx = null;

        if (ADC.isIP(aux))//we have an IP address
        {
            //ClientNod temp=ClientNod.FirstClient.NextClient;
            String Nicklist = "";
            for (ClientNod temp : SimpleHandler.getUsers())
            {
                if (temp.cur_client.userok == 1)
                {
                    if ((temp.cur_client.RealIP.equals(aux.toLowerCase())))
                    {
                        Nicklist = Nicklist + temp.cur_client.NI + "\n";
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
                    for (ClientNod temp : SimpleHandler.getUsers())
                    {
                        if (temp.cur_client.userok == 1)
                        {
                            if ((temp.cur_client.ID.equals(aux)))
                            {
                                cur_client.sendFromBot("CID " +
                                                       aux +
                                                       " is used by:\n" +
                                                       temp.cur_client.NI);
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


            for (ClientNod temp : SimpleHandler.getUsers())
            {
                if (temp.cur_client.userok == 1)
                {
                    if ((temp.cur_client.NI.toLowerCase().equals(aux.toLowerCase())))
                    {

                        String blah11 = "User Info\nNick " +
                                        ADC.retNormStr(temp.cur_client.NI) +
                                        "\nCID " +
                                        temp.cur_client.ID +
                                        "\nShare Size " +
                                        temp.cur_client.SS +
                                        " Bytes\n" +
                                        "Description " +
                                        (temp.cur_client.DE != null ?
                                         ADC.retNormStr(temp.cur_client.DE) :
                                         "") +
                                        "\nTag ";

                        String Tag = "<" + ADC.retNormStr(temp.cur_client.VE) + ",M:";
                        if (temp.cur_client.ACTIVE == 1)
                        {
                            Tag = Tag + "A";
                        }
                        else
                        {
                            Tag = Tag + "P";
                        }
                        Tag = Tag + ",H:" + temp.cur_client.HN + "/";
                        if (temp.cur_client.HR != null)
                        {
                            Tag = Tag + temp.cur_client.HR + "/";
                        }
                        else
                        {
                            Tag = Tag + "?";
                        }
                        if (temp.cur_client.HO != null)
                        {
                            Tag = Tag + temp.cur_client.HO;
                        }
                        else
                        {
                            Tag = Tag + "?";
                        }

                        Tag = Tag + ",S:";
                        if (temp.cur_client.SL != null)

                        {
                            Tag = Tag + temp.cur_client.SL + ">";
                        }
                        else
                        {
                            Tag = Tag + "?>";
                        }
                        blah11 = blah11 + Tag + "\nSupports "
                                 +
                                 ((temp.cur_client.SU != null) ?
                                  temp.cur_client.SU :
                                  "nothing special") +
                                 "\nIp address " +
                                 temp.cur_client.RealIP;
                        if (temp.cur_client.reg.isreg)
                        {

                            blah11 = blah11 + temp.cur_client.reg.getRegInfo();
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
