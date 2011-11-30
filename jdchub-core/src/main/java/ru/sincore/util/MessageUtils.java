/*
* MessageUtils.java
*
* Created on 29 11 2011, 11:39
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

package ru.sincore.util;

import ru.sincore.ClientManager;
import ru.sincore.ConfigurationManager;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-11-29
 */
public class MessageUtils
{
    public static void sendMessageToOpChat(String message)
    {
        ConfigurationManager configurationManager = ConfigurationManager.instance();
        ClientManager.getInstance()
                     .getClientBySID(
                             configurationManager.getString(ConfigurationManager.OP_CHAT_SID)
                                    )
                     .sendPrivateMessageFromHub(message);
    }
}