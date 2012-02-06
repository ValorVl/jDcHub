/*
* ChatBot.java
*
* Created on 03 02 2012, 15:06
*
* Copyright (C) 2012 Alexey 'lh' Antonov
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

package jdchub.module;

import jdchub.module.tasks.RssFeeder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Command;
import ru.sincore.TigerImpl.CIDGenerator;
import ru.sincore.adc.ClientType;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.action.actions.MSG;
import ru.sincore.client.Bot;
import ru.sincore.util.AdcUtils;

import java.util.Timer;

/**
 * Main chat bot class.
 *
 * @author Alexey 'lh' Antonov
 * @since 2012-02-03
 */
public class ChatBot extends Bot
{
    private static final Logger log = LoggerFactory.getLogger(ChatBot.class);

    //http://korobka.tv/rss.xml
    //http://korobka.tv/rss-magnet.xml
    private static final String rssURL = "http://korobka.tv/rss-magnet.xml";
    private Timer timer;

    
    public ChatBot()
    {
        this.setNick("ChatBot");
        this.setSid("PBOT");
        this.setCid(CIDGenerator.generate());
        this.setDescription(AdcUtils.toAdcString("Я могу много чего, спроси меня"));
        this.setEmail("lh@podryad.tv");
        this.setWeight(10);
        this.setClientType(ClientType.BOT);
        this.setValidated();
        this.setActive(true);
        this.setMustBeDisconnected(false);
    }


    public void sendMessage(String message)
    {
        try
        {
            MSG outgoingMessage = new MSG();
            outgoingMessage.setMessage(message);
            outgoingMessage.setMessageType(MessageType.B);
            outgoingMessage.setSourceSID(this.getSid());
            
            // send outgoing message
            //Broadcast.getInstance().broadcast(outgoingMessage.getRawCommand(), this);
            Command.handle(this, outgoingMessage.getRawCommand());
        }
        catch (Exception ex)
        {
            log.error(ex.toString());
        }
    }


    public void start()
    {
        timer = new Timer(true);


        timer.schedule(new RssFeeder(this, rssURL), 1000, 5000);
    }
}
