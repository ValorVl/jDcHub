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
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.action.actions.AbstractAction;
import ru.sincore.adc.action.actions.MSG;
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

    @Override
    public void sendAdcAction(AbstractAction action)
    {
        if (action instanceof MSG)
        {
            MSG msgAction = (MSG) action;
            AbstractClient pmSender;

            try
            {
                if (msgAction.getMessageType() != MessageType.E)
                {
                    log.debug("Chat Room can't process non EMSG messages");
                    return;
                }

                // Do not process non-PM messages
                if (msgAction.getPmSid() == null)
                {
                    log.debug("Chat room can't process non-PM messages");
                    return;
                }

                pmSender = ClientManager.getInstance().getClientBySID(msgAction.getPmSid());
                if (pmSender == null)
                {
                    throw new RuntimeException("Chat Room can't found PM-send with given SID: " + msgAction.getPmSid());
                }

                if (pmSender.getWeight() < getWeight())
                {
                    MSG lowWeightMsg = new MSG();
                    lowWeightMsg.setMessageType(MessageType.E);
                    lowWeightMsg.setSourceSID(getSid());
                    lowWeightMsg.setTargetSID(pmSender.getSid());
                    lowWeightMsg.setPmSid(getSid());
                    lowWeightMsg.setMessage("*** You don't have access to this Chat Room. " +
                                                 "Your weight is " +
                                                 pmSender.getWeight() +
                                                 "." +
                                                 "Needed weight is " +
                                                 getWeight());

                    pmSender.sendRawCommand(lowWeightMsg.getRawCommand());

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
                log.debug("toClient CID: " + toClient.getSid() + ", toClient Nick: " + toClient.getNick());
                if ((toClient.getWeight() >= getWeight()) &&
                    (!toClient.equals(this)) &&
                    (!toClient.equals(pmSender)) &&
                    !(toClient instanceof ChatRoom))
                {
                    log.debug("--- Send toClient CID: " + toClient.getSid() + ", toClient Nick: " + toClient.getNick());
                    MSG msg = new MSG();
                    try
                    {
                        msg.setMessageType(MessageType.E);
                        msg.setSourceSID(pmSender.getSid());
                        msg.setTargetSID(toClient.getSid());
                        msg.setPmSid(getSid());
                        msg.setMessage(msgAction.getMessage());

                        toClient.sendAdcAction(msg);

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public void sendRawCommand(String rawCommand)
    {
        sendAdcAction(new MSG(rawCommand));
    }

}
