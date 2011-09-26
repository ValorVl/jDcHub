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

package ru.sincore.ProtoCmds;

import ru.sincore.*;
import ru.sincore.Exceptions.STAException;
import ru.sincore.i18n.Messages;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

import java.util.StringTokenizer;

/**
 * Basic implementation of AdcUtils SCH command.
 *
 * @author Pietricica
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-08
 */
public class SCH
{

    /**
     * Creates a new instance of SCH
     * @param client reference to client
     * @param state command state. See AdcUtils protocol specs.
     * @param command incoming command // TODO realy?
     * @throws STAException exception, cause the something gone wrong =)
     */
    public SCH(Client client, String state, String command)
            throws STAException
    {
        ClientHandler cur_client = client.getClientHandler();

        if (state.equals("IDENTIFY") || state.equals("VERIFY") || state.equals("PROTOCOL"))
        {
            new STAError(client,
                         200 + Constants.STA_INVALID_STATE,
                         "SCH Invalid state.",
                         "FC",
                         command.substring(0, 4));
            return;
        }

        if (!cur_client.reg.overridespam)
        {
            switch (command.charAt(0))
            {
                case 'B':
                    if (ConfigLoader.ADC_BSCH != 1)
                    {
                        new STAError(client, 100, "SCH Invalid Context B");
                        return;
                    }
                    break;
                case 'E':
                    if (ConfigLoader.ADC_ESCH != 1)
                    {
                        new STAError(client, 100, "SCH Invalid Context E");
                        return;
                    }
                    break;
                case 'D':
                    if (ConfigLoader.ADC_DSCH != 1)
                    {
                        new STAError(client, 100, "SCH Invalid Context D");
                        return;
                    }
                    break;
                case 'F':
                    if (ConfigLoader.ADC_FSCH != 1)
                    {
                        new STAError(client, 100, "SCH Invalid Context F");
                        return;
                    }
                    break;
                case 'H':
                    if (ConfigLoader.ADC_HSCH != 1)
                    {
                        new STAError(client, 100, "SCH Invalid Context H");
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
        StringTokenizer tok = new StringTokenizer(command);
        String aux = tok.nextToken();
        String TOken = "";
        int activeonly = 0;
        int len = 0;
        if (!tok.nextToken().equals(cur_client.SID))
        {
            new STAError(client,
                         200 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Protocol Error.Wrong SID supplied.");
            return;
        }

        while (tok.hasMoreTokens())
        {
            aux = tok.nextToken();
            if (aux.startsWith("+") || aux.startsWith("-"))

            {
                if (command.charAt(0) != 'F')
                {
                    new STAError(client,
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
                    new STAError(client, 100, "SCH Feature Not Supported.");
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
                                    client.kickMeByBot("You searched forbidden words", 3);
                                }
                                else if ((prop & BannedWord.dropped) > 0)
                                {
                                    new STAError(client, 200, "You searched forbidden words");
                                }
                                else
                                {
                                    new STAError(client, 100, "You searched forbidden words");
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
        if (len > ConfigLoader.MAX_CHARS_SEARCH_REQUEST)
        {
            new STAError(client, 100, "Search exceeds maximum length.");
            return;
        }
        if (len < ConfigLoader.MIN_CHARS_SEARCH_REQUEST && len != 0)
        {
            new STAError(client, 100, "Search too short.");
            return;
        }
        long curtime = System.currentTimeMillis();
        if (!automagic)
        {
            if (curtime - cur_client.lastSearch > ConfigLoader.SEARCH_SPAM_RESET * 1000)
            {
                cur_client.searchStep = 0;
            }
            else if (cur_client.searchStep < ConfigLoader.SEARCH_STEPS)
            {
                double x = 1;
                for (int i = 0; i < cur_client.searchStep; i++)
                {
                    x *= ((double) ConfigLoader.SEARCH_BASE_INTERVAL) / 1000;
                }
                x *= 1000;
                long xx = (long) x;

                //System.out.println(xx+ "ok");
                if (curtime - cur_client.lastSearch < xx)
                {
                    //handler.sendToClient (Issued_Command);
                    String[] messages = Messages.SEARCH_SPAM_MESSAGE.split("\\\n");
                    for (int j = 0; j < messages.length; j++)
                    {
                        cur_client.sendToClient("DRES DCBA " +
                                                cur_client.SID +
                                                " SI1 SL1 FN/Searching: " +
                                                Key +
                                                " -- " +
                                                messages[j] +
                                                " TRAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA TO" +
                                                TOken);
                    }
                    if (cur_client.inQueueSearch == null)
                    {
                        cur_client.searchStep++;
                        // handler.lastSearch=System.currentTimeMillis ();
                    }

                    cur_client.inQueueSearch = command;
                    return;
                }

            }
            else
            {

                long xx = ConfigLoader.SEARCH_SPAM_RESET * 1000;

                //System.out.println(xx);
                if (curtime - cur_client.lastSearch < xx)
                {
                    //handler.sendToClient (Issued_Command);
                    String[] messages = Messages.SEARCH_SPAM_MESSAGE.split("\\\n");

                    for (String mess : messages)
                    {
                        cur_client.sendToClient("DRES DCBA " +
                                                cur_client.SID +
                                                " SI1 SL1 FN/Searching: " +
                                                Key +
                                                " -- " +
                                                mess +
                                                " TRAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA TO" +
                                                TOken);
                    }
                    if (cur_client.inQueueSearch == null)
                    {
                        cur_client.searchStep++;
                        //handler.lastSearch=System.currentTimeMillis ();
                    }
                    cur_client.inQueueSearch = command;
                    return;
                }

            }

            cur_client.searchStep++;
            cur_client.lastSearch = curtime;
        }
        else
        {
            if (curtime - cur_client.lastAutomagicSearch < ConfigLoader.AUTOMATIC_SEARCH_INTERVAL * 1000)
            {

                return;
            }
            cur_client.lastAutomagicSearch = curtime;

        }
        if (command.charAt(0) == 'B')
        {
            Broadcast.getInstance().broadcast(command);
        }
        else if (command.charAt(0) == 'F')
        {
            Broadcast.getInstance().broadcast(command, Broadcast.STATE_ACTIVE);
        }
        else
        {
            new STAError(client, 100, "SCH Invalid Context.");
        }
    }

}
