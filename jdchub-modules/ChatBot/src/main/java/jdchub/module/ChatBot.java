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

import ru.sincore.TigerImpl.CIDGenerator;
import ru.sincore.adc.ClientType;
import ru.sincore.client.AbstractClient;
import ru.sincore.util.AdcUtils;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-02-03
 */
public class ChatBot extends AbstractClient
{
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

}
