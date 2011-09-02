/*
 * ExtDrop.java
 *
 * Created on 07 septembrie 2007, 11:23
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

import ru.sincore.ClientHandler;
import ru.sincore.ClientNod;
import ru.sincore.SimpleHandler;

import java.util.StringTokenizer;
import java.util.regex.PatternSyntaxException;

/**
 * The client drop command, also with extended call.
 *
 * @author Pietricica
 */
public class ExtDrop
{

    /**
     * Creates a new instance of ExtDrop
     */
    public ExtDrop(ClientHandler cur_client, String recvbuf)
    {
        if (recvbuf.equalsIgnoreCase("drop"))
        {

            String Text =
                    "\nDrop Command:\nDropping in DSHub is also very simple now.\nClassic drop:\n" +
                    "     drop just a user down from the sky, with no temporary ban or message.\n" +
                    "Extended drop has way more advantages and can be used very efficiently with a large hub.\n" +
                    "Extended drop features:\n" +
                    "   Kicking users that match a certain regular expression:\n" +
                    "      Example: !drop \\[RO\\].* -- this command drops all users that have their nick starting with [RO]\n" +
                    "      Example: !drop .. --this command drops all users with 2 letter nicks\n" +
                    "      This type of drop accepts just any regular expression.\n" +
                    "   Kicking users that have their fields checked:\n" +
                    "      Example: !drop share<1024 --this command just drops all users with share less then 1 gigabyte.\n" +
                    "      Example: !drop sl=1 -- this command drops all users with exactly one open slot.\n" +
                    "      Example: !drops su!tcp4 -- this command drops all passive users.\n"
                    +
                    "Extended drop has the operators >, < , =, !\n" +
                    "   And a list of possible fields : share, sl (slots), ni (nick length),su(supports, accepts only = or !, example: !drop su=tcp4),hn(normal hubs count),hr(registered hub count),ho(op hub count),aw(away, 1 means normal away, 2 means extended away),rg (1- registered, 0 otherwise, registered means not op),op ( 1 -op, 0 - otherwise , op means it has key).";
            cur_client.sendFromBot(Text);
            return;
        }
        StringTokenizer ST = new StringTokenizer(recvbuf);
        ST.nextToken();
        String aux = ST.nextToken(); //the nick to drop;

        for (ClientNod temp : SimpleHandler.getUsers())
        {
            if (temp.cur_client.userok == 1)
            {
                if ((temp.cur_client.NI.toLowerCase().equals(aux.toLowerCase())))
                {
                    if (!temp.cur_client.reg.kickable)
                    {
                        cur_client.sendFromBot("This user can't be dropped.");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    else
                    {
                        //actual dropping.

                        temp.dropMe(cur_client);
                        cur_client.sendFromBot("Done.");

                        return;


                    }
                }
            }
        }

        cur_client.sendFromBot("No such user online. Parsing to Extended Drop...");
        /***************extended kick**********************/
        try
        {
            //aux=aux.replaceAll ("\\\\\\\\","\\\\");
            // System.out.println (aux);
            "".matches(aux);
            for (ClientNod temp : SimpleHandler.getUsers())
            {

                if (temp.cur_client.userok == 1)
                {
                    if ((temp.cur_client.NI.toLowerCase().matches(aux.toLowerCase())))
                    {
                        temp.dropMe(cur_client);
                    }
                }

            }

            cur_client.sendFromBot("Done with matching users...");
            throw new PatternSyntaxException("whatever...", "bla", 1);


        }
        catch (PatternSyntaxException pse)
        {
            //cur_client.sendFromBot("Not a valid Regular Expression...");
            /***********ok now must pass to field [share|sl|..][>|<|=|!=][number]*/

            int mark = aux.indexOf('>');
            if (mark != -1)
            {
                if (aux.substring(0, mark).equalsIgnoreCase("share"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Kick ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.SS) > Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with share > " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("hn"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended drop ...\");cur_client.sendFromBot("Done.");
                    //drop all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.HN) > Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with Normal Hub Count > " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("hr"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended drop ...\");cur_client.sendFromBot("Done.");
                    //drop all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.HR) > Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with Reg Hub Count > " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("ho"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.HO) > Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with Op Hub Count > " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("share"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.SS) > Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with share > " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("ni"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (tempz.cur_client.NI.length() > Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with nick length > " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
            }
            mark = aux.indexOf('<');
            if (mark != -1)
            {
                if (aux.substring(0, mark).equalsIgnoreCase("share"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.SS) / 1024 / 1024 <
                                Number)//&& tempz.userok==1)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with share < " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("hn"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.HN) < Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with Normal Hub Count < " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("ho"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.HO) < Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with Op Hub Count < " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("hr"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.HR) < Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with Reg Hub Count > " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("ni"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (tempz.cur_client.NI.length() < Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with nick length < " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("sl"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.SL) < Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with slots < " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
            }
            mark = aux.indexOf('=');
            if (mark != -1)
            {
                if (aux.substring(0, mark).equalsIgnoreCase("share"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.SS) / 1024 / 1024 ==
                                Number)//&& tempz.userok==1)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with share = " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("ho"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.HO) == Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with Op Hub Count = " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("rg"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Number == 1)
                            {
                                if (Long.parseLong(tempz.cur_client.CT) == 2)

                                {
                                    tempz.dropMe(cur_client);
                                }
                            }
                            else if (Number == 0)

                            {
                                if (Long.parseLong(tempz.cur_client.CT) != 2)

                                {
                                    tempz.dropMe(cur_client);
                                }
                            }
                        }


                    }
                    cur_client.sendFromBot("Dropped all registered/unregistered users .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("aw"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.HR) == Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all away users.");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("op"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Number == 1)
                            {
                                if (Long.parseLong(tempz.cur_client.CT) == 4)

                                {
                                    tempz.dropMe(cur_client);
                                }
                            }
                            else if (Number == 0)

                            {
                                if (Long.parseLong(tempz.cur_client.CT) != 4)

                                {
                                    tempz.dropMe(cur_client);
                                }
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all Op/non op users .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("hr"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.HR) == Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with Reg Hub Count = " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("hn"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.HN) == Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with Normal Hub Count = " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("su"))
                {
                    String Number = "";

                    Number = aux.substring(mark + 1, aux.length());

                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (tempz.cur_client
                                    .SU
                                    .toLowerCase()
                                    .contains(Number.toLowerCase()))//&& tempz.userok==1)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users supporting " + Number + " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("ni"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (tempz.cur_client.NI.length() == Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with nick length = " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("sl"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.SL) == Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with slots = " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
            }
            mark = aux.indexOf('!');
            if (mark != -1)
            {

                if (aux.substring(0, mark).equalsIgnoreCase("share"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.SS) / 1024 / 1024 !=
                                Number)//&& tempz.userok==1)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with share not " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("rg"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Number == 1)
                            {
                                if (Long.parseLong(tempz.cur_client.CT) == 2)

                                {
                                    tempz.dropMe(cur_client);
                                }
                            }
                            else if (Number == 0)

                            {
                                if (Long.parseLong(tempz.cur_client.CT) != 2)

                                {
                                    tempz.dropMe(cur_client);
                                }
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all not registered/unregistered users .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("aw"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.AW) != Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all not away users .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("op"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Number == 1)
                            {
                                if (Long.parseLong(tempz.cur_client.CT) == 4)

                                {
                                    tempz.dropMe(cur_client);
                                }
                            }
                            else if (Number == 0)

                            {
                                if (Long.parseLong(tempz.cur_client.CT) != 4)

                                {
                                    tempz.dropMe(cur_client);
                                }
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all not ops/non ops users .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("ho"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.HO) != Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with Op Hub Count not " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("hr"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.HR) != Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with Reg Hub Count not " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("hn"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.HN) != Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with Normal Hub Count not " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("su"))
                {
                    String Number = "";

                    Number = aux.substring(mark + 1, aux.length());

                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (!tempz.cur_client
                                    .SU
                                    .toLowerCase()
                                    .contains(Number.toLowerCase()))//&& tempz.userok==1)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users not supporting " + Number + " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("ni"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (tempz.cur_client.NI.length() != Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with nick length not " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }
                if (aux.substring(0, mark).equalsIgnoreCase("sl"))
                {
                    long Number = 0;
                    try
                    {
                        Number = Long.parseLong(aux.substring(mark + 1, aux.length()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        cur_client.sendFromBot("Invalid Extended Drop ...");
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                    // cur_client.sendFromBot(""+Integer.toString (Number));//Invalid Extended Drop ...\");cur_client.sendFromBot("Done.");
                    //kick all shared > number
                    for (ClientNod tempz : SimpleHandler.getUsers())
                    {
                        if (tempz.cur_client.userok == 1)
                        {
                            if (Long.parseLong(tempz.cur_client.SL) != Number)
                            {
                                tempz.dropMe(cur_client);
                            }
                        }

                    }
                    cur_client.sendFromBot("Dropped all users with slots not " +
                                           Long.toString(Number) +
                                           " .");
                    cur_client.sendFromBot("Done.");


                    return;
                }

            }
            cur_client.sendFromBot("Invalid Extended Drop ...");
            cur_client.sendFromBot("Done.");

        }
        /*****************extended drop*******************/

    }

}
