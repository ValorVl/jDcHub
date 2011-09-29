/*
 * ExtMass.java
 *
 * Created on 07 septembrie 2007, 11:14
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

import ru.sincore.Client;
import ru.sincore.ClientHandler;
import ru.sincore.SessionManager;

import java.util.StringTokenizer;
import java.util.regex.PatternSyntaxException;

/**
 * The client mass command, also with extended call.
 *
 * @author Pietricica
 */
public class ExtMass
{

    /**
     * Creates a new instance of ExtMass
     */
    public ExtMass(ClientHandler cur_client, String recvbuf)
    {
        StringTokenizer ST = new StringTokenizer(recvbuf, " ");
        ST.nextToken();
        String aux = "";
        if (!(ST.hasMoreTokens()))
        {
            String Text =
                    "\nMass Command:\nBroadcasting in DSHub is very simple now.\nClassic mass:\n" +
                    "     Mass message to all users, example: \"mass all text\".\n" +
                    "Extended mass has way more advantages and can be used very efficiently with a large hub.\n" +
                    "Extended mass features:\n" +
                    "   Sending to users that match a certain regular expression:\n" +
                    "      Example: !mass \\[RO\\].* text -- this command sends mass to all users that have their nick starting with [RO]\n" +
                    "      Example: !mass .. text --this command sends mass to all users with 2 letter nicks\n" +
                    "      This type of mass command accepts just any regular expression.\n" +
                    "   Sending to users that have their fields checked:\n" +
                    "      Example: !mass share<1024 text --this command just sends text to all users with share less then 1 gigabyte.\n" +
                    "      Example: !mass sl=1 text-- this command just sends text to all users with exactly one open slot.\n" +
                    "      Example: !mass su!tcp4 text -- this command just sends text to all passive users.\n"
                    +
                    "Extended mass has the operators >, < , =, !\n" +
                    "   And a list of possible fields : all ( to everybody ) share, sl (slots), ni (nick length),su(supports, accepts only = or !, example: !mass su=tcp4 text),hn(normal hubs count),hr(registered hub count),ho(op hub count),aw(away, 1 means normal away, 2 means extended away),rg (1- registered, 0 otherwise, registered means not op),op ( 1 -op, 0 - otherwise , op means it has key).";
            cur_client.sendFromBot(Text);
            return;
        }
        String extmass = ST.nextToken();
        while (ST.hasMoreTokens())
        {
            aux = aux + ST.nextToken() + " "; //the message to broadcast;
        }
        System.out.println(recvbuf);
        if (extmass.equalsIgnoreCase("all"))
        {
            for (Client temp : SessionManager.getUsers())
            {
                temp.getClientHandler().sendFromBotPM(aux);

            }
            cur_client.sendFromBot("Broadcast sent.");
            return;
        }

        try
        {

            "".matches(extmass);


            for (Client temp : SessionManager.getUsers())
            {
                if (temp.getClientHandler().validated == 1)
                {
                    if ((temp.getClientHandler().NI.toLowerCase().matches(extmass.toLowerCase())))
                    {
                        temp.getClientHandler().sendFromBotPM(aux);
                    }
                }


            }

            cur_client.sendFromBot("Done with matching users...");
            throw new PatternSyntaxException("whatever...", "bla", 1);


        }
        catch (PatternSyntaxException pse)
        {
            //Not a valid Regular Expression...
            /***********ok now must pass to field [share|sl|..][>|<|=|!=][number]*/

            int mark = extmass.indexOf('>');
            if (mark != -1)
            {
                if (extmass.substring(0, mark).equalsIgnoreCase("share"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().SS) / 1024 / 1024 > Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with share > " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("hn"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().HN) > Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with Normal Hub Count > " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("hr"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().HR) > Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with Reg Hub Count > " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("ho"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().HO) > Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with Op Hub Count > " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("share"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().SS) > Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with share > " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("ni"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (tempz.getClientHandler().NI.length() > Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with nick length > " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
            }
            mark = extmass.indexOf('<');
            if (mark != -1)
            {
                if (extmass.substring(0, mark).equalsIgnoreCase("share"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().SS) / 1024 / 1024 <
                                Number)//&& tempz.getClientHandler().validated==1)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with share < " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("hn"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().HN) < Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with Normal Hub Count < " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("ho"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().HO) < Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with Op Hub Count < " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("hr"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().HR) < Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with Reg Hub Count > " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("ni"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (tempz.getClientHandler().NI.length() < Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with nick length < " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("sl"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().SL) < Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with slots < " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
            }
            mark = extmass.indexOf('=');
            if (mark != -1)
            {
                if (extmass.substring(0, mark).equalsIgnoreCase("share"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().SS) / 1024 / 1024 ==
                                Number)//&& tempz.getClientHandler().validated==1)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with share = " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("ho"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().HO) == Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with Op Hub Count = " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("rg"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Number == 1)
                            {
                                if (Long.parseLong(tempz.getClientHandler().CT) == 2)

                                {
                                    tempz.getClientHandler().sendFromBotPM(aux);
                                }
                            }
                            else if (Number == 0)
                            {
                                if (Long.parseLong(tempz.getClientHandler().CT) != 2)

                                {
                                    tempz.getClientHandler().sendFromBotPM(aux);
                                }
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all registered/non registered users .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("aw"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().HR) == Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all away users.");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("op"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Number == 1)
                            {
                                if (Long.parseLong(tempz.getClientHandler().CT) == 4)

                                {
                                    tempz.getClientHandler().sendFromBotPM(aux);
                                }
                            }
                            else if (Number == 0)
                            {
                                if (Long.parseLong(tempz.getClientHandler().CT) != 4)

                                {
                                    tempz.getClientHandler().sendFromBotPM(aux);
                                }
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all Op/non op users .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("hr"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().HR) == Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with Reg Hub Count = " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("hn"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().HN) == Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with Normal Hub Count = " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("su"))
                {
                    String Number = "";

                    Number = extmass.substring(mark + 1, extmass.length());


                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (tempz.getClientHandler()
                                    .SU
                                    .toLowerCase()
                                    .contains(Number.toLowerCase()))//&& tempz.getClientHandler().validated==1)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users supporting " + Number + " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("ni"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (tempz.getClientHandler().NI.length() == Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with nick length = " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("sl"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().SL) == Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with slots = " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
            }
            mark = extmass.indexOf('!');
            if (mark != -1)
            {

                if (extmass.substring(0, mark).equalsIgnoreCase("share"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().SS) / 1024 / 1024 !=
                                Number)//&& tempz.getClientHandler().validated==1)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with share not " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("rg"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Number == 1)
                            {
                                if (Long.parseLong(tempz.getClientHandler().CT) == 2)

                                {
                                    tempz.getClientHandler().sendFromBotPM(aux);
                                }
                            }
                            else if (Number == 0)
                            {
                                if (Long.parseLong(tempz.getClientHandler().CT) != 2)

                                {
                                    tempz.getClientHandler().sendFromBotPM(aux);
                                }
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all not registered/unregistered users .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("aw"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().AW) != Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all not away users .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("op"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Number == 1)
                            {
                                if (Long.parseLong(tempz.getClientHandler().CT) == 4)

                                {
                                    tempz.getClientHandler().sendFromBotPM(aux);
                                }
                            }
                            else if (Number == 0)
                            {
                                if (Long.parseLong(tempz.getClientHandler().CT) != 4)

                                {
                                    tempz.getClientHandler().sendFromBotPM(aux);
                                }
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all not ops/non ops users .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("ho"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().HO) != Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with Op Hub Count not " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("hr"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().HR) != Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with Reg Hub Count not " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("hn"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().HN) != Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with Normal Hub Count not " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("su"))
                {
                    String Number = "";

                    Number = extmass.substring(mark + 1, extmass.length());


                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (!tempz.getClientHandler()
                                    .SU
                                    .toLowerCase()
                                    .contains(Number.toLowerCase()))//&& tempz.getClientHandler().validated==1)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users not supporting " + Number + " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("ni"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (tempz.getClientHandler().NI.length() != Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with nick length not " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (extmass.substring(0, mark).equalsIgnoreCase("sl"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(extmass.substring(mark + 1, extmass.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Mass ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }

                    for (Client tempz : SessionManager.getUsers())
                    {
                        if (tempz.getClientHandler().validated == 1)
                        {
                            if (Long.parseLong(tempz.getClientHandler().SL) != Number)
                            {
                                tempz.getClientHandler().sendFromBotPM(aux);
                            }
                        }

                    }
                    cur_client.sendFromBot("Sent to all users with slots not " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }

            }
            cur_client.sendFromBot("Invalid Extended Mass ...");
            cur_client.sendFromBot("Done.");

        }
    }

}
