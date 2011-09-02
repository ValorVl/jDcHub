/*
 * SCH.java
 *
 * Created on 07 septembrie 2007, 18:34
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

package dshub.ProtoCmds;

import dshub.*;
import dshub.Exceptions.STAException;
import dshub.conf.Vars;
import dshub.util.Constants;
import dshub.util.STAError;

import java.util.StringTokenizer;

/**
 * Basic implementation of ADC SCH command.
 *
 * @author Pietricica
 */
public class SCH
{

    /**
     * Creates a new instance of SCH
     */
    public SCH(ClientHandler cur_client, String Issued_Command, String State)
            throws STAException
    {
        if (State.equals("IDENTIFY") || State.equals("VERIFY") || State.equals("PROTOCOL"))
        {
            new STAError(cur_client,
                         200 + Constants.STA_INVALID_STATE,
                         "SCH Invalid State.",
                         "FC",
                         Issued_Command.substring(0, 4));
            return;
        }

        if (!cur_client.reg.overridespam)
        {
            switch (Issued_Command.charAt(0))
            {
                case 'B':
                    if (Vars.BSCH != 1)
                    {
                        new STAError(cur_client, 100, "SCH Invalid Context B");
                        return;
                    }
                    break;
                case 'E':
                    if (Vars.ESCH != 1)
                    {
                        new STAError(cur_client, 100, "SCH Invalid Context E");
                        return;
                    }
                    break;
                case 'D':
                    if (Vars.DSCH != 1)
                    {
                        new STAError(cur_client, 100, "SCH Invalid Context D");
                        return;
                    }
                    break;
                case 'F':
                    if (Vars.FSCH != 1)
                    {
                        new STAError(cur_client, 100, "SCH Invalid Context F");
                        return;
                    }
                    break;
                case 'H':
                    if (Vars.HSCH != 1)
                    {
                        new STAError(cur_client, 100, "SCH Invalid Context H");
                        return;
                    }

            }
        }

        /** This is my new idea, the logarythmic search spam abuse.
         * This way, the hub is being kept safe, and users are not frustrated no more of bad and useless regular searches.
         * First, there are 2 types of searches, the automagic searches that client auto sends
         * and 2nd, the search by name that the user tries.
         * By logarithmic, im trying to keep a log base that would increase with every search,
         * and if no searches, is being reset.
         */
        boolean automagic = true;
        String Key = "";
        StringTokenizer tok = new StringTokenizer(Issued_Command);
        String aux = tok.nextToken();
        String TOken = "";
        int activeonly = 0;
        int len = 0;
        if (!tok.nextToken().equals(cur_client.SessionID))
        {
            new STAError(cur_client,
                         200 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Protocol Error.Wrong SID supplied.");
            return;
        }

        while (tok.hasMoreTokens())
        {
            aux = tok.nextToken();
            if (aux.startsWith("+") || aux.startsWith("-"))

            {
                if (Issued_Command.charAt(0) != 'F')
                {
                    new STAError(cur_client,
                                 100 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                                 "SCH Must Be Feature Broadcast to send to Featured clients.");
                    return;
                }
                else if (aux.equals("+TCP4"))
                {
                    activeonly = 1;
                }
                else
                {
                    new STAError(cur_client, 100, "SCH Feature Not Supported.");
                    return;
                }
            }
            else if (aux.startsWith("AN") || aux.startsWith("EX") || aux.startsWith("NO"))
            {
                len += aux.length() - 2;
                automagic = false; //its not automagic search
                Key += aux.substring(2);
                if (!cur_client.reg.overridespam)
                {
                    if (!aux.startsWith("NO"))
                    {
                        int x = Main.listaBanate.isOK(aux.substring(2));
                        if (x != -1)
                        {
                            long prop = Main.listaBanate.getPrAt(x);
                            if ((prop & BannedWord.searches) > 0)
                            {
                                if ((prop & BannedWord.kicked) > 0)
                                {
                                    cur_client.myNod.kickMeByBot("You searched forbidden words", 3);
                                }
                                else if ((prop & BannedWord.dropped) > 0)
                                {
                                    new STAError(cur_client, 200, "You searched forbidden words");
                                }
                                else
                                {
                                    new STAError(cur_client, 100, "You searched forbidden words");
                                }
                                return;
                            }

                        }
                    }
                }
            }
            else if (aux.startsWith("TO"))
            {
                TOken = aux.substring(2);
            }
        }
        if (len > Vars.max_sch_chars)
        {
            new STAError(cur_client, 100, "Search exceeds maximum length.");
            return;
        }
        if (len < Vars.min_sch_chars && len != 0)
        {
            new STAError(cur_client, 100, "Search too short.");
            return;
        }
        long curtime = System.currentTimeMillis();
        if (automagic == false)
        {
            if (curtime - cur_client.Lastsearch > Vars.search_spam_reset * 1000)
            {
                cur_client.search_step = 0;
            }
            else if (cur_client.search_step < Vars.search_steps)
            {
                double x = 1;
                for (int i = 0; i < cur_client.search_step; i++)
                {
                    x *= ((double) Vars.search_log_base) / 1000;
                }
                x *= 1000;
                long xx = (long) x;

                //System.out.println(xx+ "ok");
                if (curtime - cur_client.Lastsearch < xx)
                {
                    //cur_client.sendToClient (Issued_Command);
                    String[] Messages = Vars.Msg_Search_Spam.split("\\\n");
                    for (int j = 0; j < Messages.length; j++)
                    {
                        cur_client.sendToClient("DRES DCBA " +
                                                cur_client.SessionID +
                                                " SI1 SL1 FN/Searching: " +
                                                Key +
                                                " -- " +
                                                Messages[j] +
                                                " TRAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA TO" +
                                                TOken);
                    }
                    if (cur_client.InQueueSearch == null)
                    {
                        cur_client.search_step++;
                        // cur_client.Lastsearch=System.currentTimeMillis ();
                    }

                    cur_client.InQueueSearch = Issued_Command;
                    return;
                }

            }
            else
            {

                long xx = Vars.search_spam_reset * 1000;

                //System.out.println(xx);
                if (curtime - cur_client.Lastsearch < xx)
                {
                    //cur_client.sendToClient (Issued_Command);
                    String[] Messages = Vars.Msg_Search_Spam.split("\\\n");
                    for (int j = 0; j < Messages.length; j++)
                    {
                        cur_client.sendToClient("DRES DCBA " +
                                                cur_client.SessionID +
                                                " SI1 SL1 FN/Searching: " +
                                                Key +
                                                " -- " +
                                                Messages[j] +
                                                " TRAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA TO" +
                                                TOken);
                    }
                    if (cur_client.InQueueSearch == null)
                    {
                        cur_client.search_step++;
                        //cur_client.Lastsearch=System.currentTimeMillis ();
                    }
                    cur_client.InQueueSearch = Issued_Command;
                    return;
                }

            }

            cur_client.search_step++;
            cur_client.Lastsearch = curtime;
        }
        else
        {
            if (curtime - cur_client.Lastautomagic < Vars.automagic_search * 1000)
            {

                return;
            }
            cur_client.Lastautomagic = curtime;

        }
        if (Issued_Command.charAt(0) == 'B')
        {
            Broadcast.getInstance().broadcast(Issued_Command);
        }
        else if (Issued_Command.charAt(0) == 'F')
        {
            Broadcast.getInstance().broadcast(Issued_Command, Broadcast.STATE_ACTIVE);
        }
        else
        {
            new STAError(cur_client, 100, "SCH Invalid Context.");
            return;
        }
    }

}
