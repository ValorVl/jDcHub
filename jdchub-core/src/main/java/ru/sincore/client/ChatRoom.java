/*
 * ChatRoom.java
 *
 * Created on 07 october 2011, 12:26
 *
 * Copyright (C) 2011 Alexey 'lh' Antonov
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

package ru.sincore.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.ClientManager;
import ru.sincore.ConfigurationManager;
import ru.sincore.Exceptions.STAException;
import ru.sincore.util.AdcUtils;
import ru.sincore.util.Constants;

import java.util.StringTokenizer;

/**
 * @author Alexey 'lh' Antonov
 * @author Alexander 'hatred' Drozdov
 * @since 2011-10-07
 */
public class ChatRoom extends Bot
{
    private static final Logger log = LoggerFactory.getLogger(ChatRoom.class);

    private String  mySID;
    private String  targetSID;
    private String  pmSID;
    private String  message;
    private boolean haveME;
    private boolean isPm = false;

    @Override
    public void sendRawCommand(String rawCommand)
    {
        // TODO: reorganize ADC Action parsing and executing and remove stupid code here (like command parsing and composing)

        StringTokenizer tokenizer = new StringTokenizer(rawCommand, " ");
        String action = tokenizer.nextToken();

        if (!action.equals("EMSG"))
        {
            log.debug("Chat Room can't process non EMSG messages");
            return;
        }

        AbstractClient pmSender;

        try
        {
            parseMySID(tokenizer);
            parseTargetSID(tokenizer);
            parseMessage(tokenizer);
            parseFlags(tokenizer);

            // Do not process non-PM messages
            if (isPm == false)
            {
                log.debug("Chat room can't process non-PM messages");
                return;
            }

            pmSender = ClientManager.getInstance().getClientBySID(pmSID);
            if (pmSender == null)
            {
                throw new RuntimeException("Chat Room can't found PM-send with given SID: " + pmSID);
            }

            if (pmSender.getWeight() < getWeight())
            {
                pmSender.sendRawCommand(constructMessage(getSid(),
                                                         pmSender.getSid(),
                                                         getSid(),
                                                         AdcUtils.toAdcString(
                                                                 "*** You don't have access to this Chat Room. " +
                                                                 "Your weight is " +
                                                                 pmSender.getWeight() +
                                                                 "." +
                                                                 "Needed weight is " +
                                                                 getWeight())));
                return;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        for (AbstractClient toClient : ClientManager.getInstance().getClients())
        {
            if ((toClient.getWeight() >= getWeight()) &&
                (!toClient.equals(this)) &&
                (!toClient.equals(pmSender)) &&
                !(toClient instanceof ChatRoom))
            {
                toClient.sendRawCommand(constructMessage(pmSID, toClient.getSid(), getSid(), message));
            }
        }
    }


    private String constructMessage(String messageFromSid,
                                    String messageToSid,
                                    String chatFromSid,
                                    String message)
    {
        StringBuffer command = new StringBuffer("EMSG ");
        command.append(messageFromSid);             // Pm message SID (private message from)
        command.append(' ');
        command.append(messageToSid);               // Target client SID
        command.append(' ');
        if (haveME && message.startsWith("/me "))
        {
            message.replaceFirst("\\/me", "");
        }
        command.append(message);
        command.append(' ');
        command.append("PM" + chatFromSid);         // Bot's SID (private chat from)

        if (haveME)
        {
            command.append(' ');
            command.append("ME1");
        }

        return command.toString();
    }

    private void parseMySID(StringTokenizer tokenizer)
            throws STAException
    {
        mySID = tokenizer.nextToken();

        if (mySID.length() != 4)
            new STAException("MSG contains wrong my_sid value!",
                             Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR);

    }


    private void parseTargetSID(StringTokenizer tokenizer)
            throws STAException
    {
        targetSID = tokenizer.nextToken();

        if (targetSID.length() != 4)
            new STAException("MSG contains wrong target_sid value!",
                             Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR);

    }


    private void parseMessage(StringTokenizer tokenizer)
            throws STAException
    {
        String messageText = tokenizer.nextToken();

        if (messageText.length() > ConfigurationManager.instance().getInt(ConfigurationManager.MAX_CHAT_MESSAGE_SIZE))
            throw new STAException("MSG Message exceeds maximum length.",
                                   Constants.STA_SEVERITY_RECOVERABLE);

        message = messageText;
    }



    private void parseFlags(StringTokenizer tokenizer)
            throws STAException
    {
        while (tokenizer.hasMoreTokens())
        {
            String token = tokenizer.nextToken();
            if (token.startsWith("PM"))
            {
                isPm = true;
                pmSID = token.substring(2);
                if (pmSID.length() != 4)
                    new STAException("MSG Invalid flag value.",
                                     Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR);
            }
            else if (token.startsWith("ME"))
            {
                if (token.substring(2).equals("1"))
                    haveME = true;
            }
        }
    }

}
