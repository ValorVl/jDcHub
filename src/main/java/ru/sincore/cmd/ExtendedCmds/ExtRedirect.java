/*
 *  ExtRedirect.java
 * 
 * Created on 22 decembrie 2007, 10:19
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
import ru.sincore.SessionManager;
import ru.sincore.conf.Vars;
import ru.sincore.util.ADC;
import ru.sincore.ClientHandler;

import java.util.StringTokenizer;
import java.util.regex.PatternSyntaxException;

/**
 * @author Pietricica
 */
public class ExtRedirect
{

    public ExtRedirect(ClientHandler cur_client, String recvbuf)
    {
        if (recvbuf.equalsIgnoreCase("redirect"))
        {

            String Text =
                    "\nRedirect Command:\nRedirecting in DSHub is very simple now.\nClassic redirect:\n" +
                    "     redirect nick/CID [URL] - redirects user given by nick or CID to given URL\n" +
                    "           If URL is not specified, the redirect_url variable is used.\n" +
                    "Extended redirect has way more advantages and can be used very efficiently with a large hub.\n" +
                    "Extended redirect features:\n" +
                    "   Redirecting users that match a certain regular expression:\n" +
                    "      Example: !redirect \\[RO\\].* [URL]-- this command redirects all users that have their nick starting with [RO]\n" +
                    "      Example: !redirect .. [URL] -- this command redirects all users with 2 letter nicks\n" +
                    "      This type of redirect accepts just any regular expression.\n" +
                    "   Redirecting users that have their fields checked:\n" +
                    "      Example: !redirect share<1024 [URL] --this command just redirects all users with share less then 1 gigabyte.\n" +
                    "      Example: !redirect sl=1 [URL] -- this command redirects all users with exactly one open slot.\n" +
                    "      Example: !kick su!tcp4 [URL] -- this command redirects all passive users.\n"
                    +
                    "Extended redirect has the operators >, < , =, !\n" +
                    "   And a list of possible fields : share, sl (slots), ni (nick length),su(supports, accepts only = or !, example: !redirect su=tcp4 [URL]),hn(normal hubs count),hr(registered hub count),ho(op hub count),aw(away, 1 means normal away, 2 means extended away),rg (1- registered, 0 otherwise, registered means not op),op ( 1 -op, 0 - otherwise , op means it has key).";
            cur_client.sendFromBot(Text);
            return;
        }

        StringTokenizer ST = new StringTokenizer(recvbuf);
        ST.nextToken();
        String what = ST.nextToken();
        String URL = Vars.redirect_url;
        if (ST.hasMoreTokens())
        {
            URL = ST.nextToken();
        }

        if (ADC.isCID(what))
        {

            for (Client aux : SessionManager.getUsers())
            {
                if (aux.handler.userok == 1)
                {
                    if (aux.handler.ID.equalsIgnoreCase(what))
                    {
                        aux.redirectMe(cur_client, URL);
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                }


            }

            cur_client.sendFromBot("No user found with given CID.");
            cur_client.sendFromBot("Done.");

            return;
        }
        else //is a nick
        {
            for (Client aux : SessionManager.getUsers())
            {
                if (aux.handler.userok == 1)
                {
                    if (aux.handler.NI.equalsIgnoreCase(what))
                    {
                        aux.redirectMe(cur_client, URL);
                        cur_client.sendFromBot("Done.");
                        return;
                    }
                }

            }


        }
        try
        {
            "".matches(what);
            for (Client aux : SessionManager.getUsers())
            {
                if (aux.handler.userok == 1)
                {
                    if (aux.handler.NI.matches(what))
                    {

                        aux.redirectMe(cur_client, URL);

                    }
                }


            }
            cur_client.sendFromBot("Done with matching users...");
        }
        catch (PatternSyntaxException pse)
        {
            cur_client.sendFromBot("Not a valid regular expression...");
        }
        //done with normal redirect, parsing to field check


        /***********ok now must pass to field [share|sl|..][>|<|=|!=][number]*/
        String aux = what;
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        if (Long.parseLong(tempz.handler.SS) / 1024 / 1024 > Number)
                        {
                            tempz.redirectMe(cur_client, URL);
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with share > " +
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        try
                        {
                            if (Long.parseLong(tempz.handler.HN) > Number)
                            {
                                tempz.redirectMe(cur_client, URL);
                            }
                        }
                        catch (NumberFormatException numberFormatException)
                        {
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with Normal Hub Count > " +
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        try
                        {
                            if (Long.parseLong(tempz.handler.HR) > Number)
                            {
                                tempz.redirectMe(cur_client, URL);
                            }
                        }
                        catch (NumberFormatException numberFormatException)
                        {
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with Reg Hub Count > " +
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        try
                        {
                            if (Long.parseLong(tempz.handler.HO) > Number)
                            {
                                tempz.redirectMe(cur_client, URL);
                            }
                        }
                        catch (NumberFormatException numberFormatException)
                        {
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with Op Hub Count > " +
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        try
                        {
                            if (Long.parseLong(tempz.handler.SL) > Number)
                            {
                                tempz.redirectMe(cur_client, URL);
                            }
                        }
                        catch (NumberFormatException numberFormatException)
                        {
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with slots " +
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        if (tempz.handler.NI.length() > Number)
                        {
                            tempz.redirectMe(cur_client, URL);
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with nick length > " +
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        if (Long.parseLong(tempz.handler.SS) / 1024 / 1024 <
                            Number)//&& tempz.userok==1)
                        {
                            tempz.redirectMe(cur_client, URL);
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with share < " +
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        try
                        {
                            if (Long.parseLong(tempz.handler.HN) < Number)
                            {
                                tempz.redirectMe(cur_client, URL);
                            }
                        }
                        catch (NumberFormatException numberFormatException)
                        {
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with Normal Hub Count < " +
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        try
                        {
                            if (Long.parseLong(tempz.handler.HO) < Number)
                            {
                                tempz.redirectMe(cur_client, URL);
                            }
                        }
                        catch (NumberFormatException numberFormatException)
                        {
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with Op Hub Count < " +
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        try
                        {
                            if (Long.parseLong(tempz.handler.HR) < Number)
                            {
                                tempz.redirectMe(cur_client, URL);
                            }
                        }
                        catch (NumberFormatException numberFormatException)
                        {
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with Reg Hub Count > " +
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        if (tempz.handler.NI.length() < Number)
                        {
                            tempz.redirectMe(cur_client, URL);
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with nick length < " +
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        try
                        {
                            if (Long.parseLong(tempz.handler.SL) < Number)
                            {
                                tempz.redirectMe(cur_client, URL);
                            }
                        }
                        catch (NumberFormatException numberFormatException)
                        {
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with slots < " +
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        if (Long.parseLong(tempz.handler.SS) / 1024 / 1024 ==
                            Number)//&& tempz.handler.userok==1)
                        {
                            tempz.redirectMe(cur_client, URL);
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with share = " +
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        try
                        {
                            if (Long.parseLong(tempz.handler.HO) == Number)
                            {
                                tempz.redirectMe(cur_client, URL);
                            }
                        }
                        catch (NumberFormatException numberFormatException)
                        {
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with Op Hub Count = " +
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        if (Number == 1)
                        {
                            if (Long.parseLong(tempz.handler.CT) == 2)

                            {
                                tempz.redirectMe(cur_client, URL);
                            }
                        }
                        else if (Number == 0)
                        {
                            if (Long.parseLong(tempz.handler.CT) != 2)

                            {
                                tempz.redirectMe(cur_client, URL);
                            }
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all registered/unregistered users .");
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        if (Long.parseLong(tempz.handler.HR) == Number)
                        {
                            tempz.redirectMe(cur_client, URL);
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all away users.");
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        try
                        {
                            if (Number == 1)
                            {
                                if (Long.parseLong(tempz.handler.CT) == 4)

                                {
                                    tempz.redirectMe(cur_client, URL);
                                }
                            }
                            else if (Number == 0)
                            {
                                if (Long.parseLong(tempz.handler.CT) != 4)

                                {
                                    tempz.redirectMe(cur_client, URL);
                                }
                            }
                        }
                        catch (NumberFormatException nfe)
                        {

                        }
                    }


                }
                cur_client.sendFromBot("Redirected all Op/non op users .");
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        try
                        {
                            if (Long.parseLong(tempz.handler.HR) == Number)
                            {
                                tempz.redirectMe(cur_client, URL);
                            }
                        }
                        catch (NumberFormatException numberFormatException)
                        {
                        }
                    }


                }
                cur_client.sendFromBot("Redirected all users with Reg Hub Count = " +
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        try
                        {
                            if (Long.parseLong(tempz.handler.HN) == Number)
                            {
                                tempz.redirectMe(cur_client, URL);
                            }
                        }
                        catch (NumberFormatException numberFormatException)
                        {
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with Normal Hub Count = " +
                                       Long.toString(Number) +
                                       " .");
                cur_client.sendFromBot("Done.");


                return;
            }
            if (aux.substring(0, mark).equalsIgnoreCase("su"))
            {
                String Number = "";

                Number = aux.substring(mark + 1, aux.length());

                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        if (tempz.handler
                                .SU
                                .toLowerCase()
                                .contains(Number.toLowerCase()))//&& tempz.handler.userok==1)
                        {
                            tempz.redirectMe(cur_client, URL);
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users supporting " + Number + " .");
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        if (tempz.handler.NI.length() == Number)
                        {
                            tempz.redirectMe(cur_client, URL);
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with nick length = " +
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        if (Long.parseLong(tempz.handler.SL) == Number)
                        {
                            tempz.redirectMe(cur_client, URL);
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with slots = " +
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        if (Long.parseLong(tempz.handler.SS) / 1024 / 1024 !=
                            Number)//&& tempz.handler.userok==1)
                        {
                            tempz.redirectMe(cur_client, URL);
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with share not " +
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        try
                        {
                            if (Number == 1)
                            {
                                if (Long.parseLong(tempz.handler.CT) == 2)

                                {
                                    tempz.redirectMe(cur_client, URL);
                                }
                            }
                            else if (Number == 0)
                            {
                                if (Long.parseLong(tempz.handler.CT) != 2)

                                {
                                    tempz.redirectMe(cur_client, URL);
                                }
                            }
                        }
                        catch (NumberFormatException numberFormatException)
                        {
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all not registered/unregistered users .");
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        try
                        {
                            if (Long.parseLong(tempz.handler.AW) != Number)
                            {
                                tempz.redirectMe(cur_client, URL);
                            }
                        }
                        catch (NumberFormatException numberFormatException)
                        {
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all not away users .");
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        try
                        {
                            if (Number == 1)
                            {
                                if (Long.parseLong(tempz.handler.CT) == 4)

                                {
                                    tempz.redirectMe(cur_client, URL);
                                }
                            }
                            else if (Number == 0)
                            {
                                if (Long.parseLong(tempz.handler.CT) != 4)

                                {
                                    tempz.redirectMe(cur_client, URL);
                                }
                            }
                        }
                        catch (NumberFormatException numberFormatException)
                        {
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all not ops/non ops users .");
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        try
                        {
                            if (Long.parseLong(tempz.handler.HO) != Number)
                            {
                                tempz.redirectMe(cur_client, URL);
                            }
                        }
                        catch (NumberFormatException numberFormatException)
                        {
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with Op Hub Count not " +
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        try
                        {
                            if (Long.parseLong(tempz.handler.HR) != Number)
                            {
                                tempz.redirectMe(cur_client, URL);
                            }
                        }
                        catch (NumberFormatException numberFormatException)
                        {
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with Reg Hub Count not " +
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        try
                        {
                            if (Long.parseLong(tempz.handler.HN) != Number)
                            {
                                tempz.redirectMe(cur_client, URL);
                            }
                        }
                        catch (NumberFormatException numberFormatException)
                        {
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with Normal Hub Count not " +
                                       Long.toString(Number) +
                                       " .");
                cur_client.sendFromBot("Done.");


                return;
            }
            if (aux.substring(0, mark).equalsIgnoreCase("su"))
            {
                String Number = "";

                Number = aux.substring(mark + 1, aux.length());

                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        if (!tempz.handler
                                .SU
                                .toLowerCase()
                                .contains(Number.toLowerCase()))//&& tempz.handler.userok==1)
                        {
                            tempz.redirectMe(cur_client, URL);
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users not supporting " + Number + " .");
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        if (tempz.handler.NI.length() != Number)
                        {
                            tempz.redirectMe(cur_client, URL);
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with nick length not " +
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
                    cur_client.sendFromBot("Invalid Extended Redirect ...");
                    cur_client.sendFromBot("Done.");
                    return;
                }
                // handler.sendFromBot(""+Integer.toString (Number));//Invalid Extended Redirect ...\");handler.sendFromBot("Done.");
                //Redirect all shared > number
                for (Client tempz : SessionManager.getUsers())
                {
                    if (tempz.handler.userok == 1)
                    {
                        try
                        {
                            if (Long.parseLong(tempz.handler.SL) != Number)
                            {
                                tempz.redirectMe(cur_client, URL);
                            }
                        }
                        catch (NumberFormatException numberFormatException)
                        {
                        }
                    }

                }
                cur_client.sendFromBot("Redirected all users with slots not " +
                                       Long.toString(Number) +
                                       " .");
                cur_client.sendFromBot("Done.");


                return;
            }

        }
        cur_client.sendFromBot("Invalid Extended Redirect ...");
        cur_client.sendFromBot("Done.");


    }
}
