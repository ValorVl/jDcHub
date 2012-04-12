/*
* RssFeeder.java
*
* Created on 03 02 2012, 16:32
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

package jdchub.module.tasks;

import com.adamtaft.eb.EventHandler;
import jdchub.module.ChatBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.events.NewRssFeed;

/**
 * Handles newRssFeedEvent and send message to chat.
 *
 * @author Alexey 'lh' Antonov
 * @since 2012-02-03
 */
public class RssFeeder
{
    private static final Logger log = LoggerFactory.getLogger(RssFeeder.class); 
    
    private ChatBot parent = null;
    
    public RssFeeder(ChatBot parent)
    {
        this.parent = parent;
    }


    @EventHandler
    public void handleRssFeedPostEvent(NewRssFeed newRssFeedEvent)
    {
        log.debug("NewRssFeed handled.");

        StringBuilder message = new StringBuilder();

        message.append(newRssFeedEvent.getPostName());

        message.append('\n');
        message.append(newRssFeedEvent.getLink());

        parent.sendMessage(message.toString());
    }
}
