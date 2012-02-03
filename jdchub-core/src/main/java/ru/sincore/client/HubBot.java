/*
* HubBot.java
*
* Created on 03 02 2012, 15:26
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

package ru.sincore.client;

import ru.sincore.BigTextManager;
import ru.sincore.ConfigurationManager;
import ru.sincore.adc.ClientType;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-02-03
 */
public class HubBot extends Bot
{
    public HubBot()
    {
        this.setSid(ConfigurationManager.instance().getString(ConfigurationManager.HUB_SID));
        this.setCid(ConfigurationManager.instance().getString(ConfigurationManager.SECURITY_CID));
        this.setNick(ConfigurationManager.instance().getString(ConfigurationManager.HUB_NAME));
        this.setDescription(ConfigurationManager.instance().getAdcString(ConfigurationManager.HUB_DESCRIPTION));

        // TODO [lh] Remove code duplication
        // duplicated code placed here: SUPHandler#sendClientInitializationInfo
        if (!ConfigurationManager.instance().getAdcString(ConfigurationManager.HUB_DESCRIPTION).isEmpty())
        {
            this.setDescription(ConfigurationManager.instance().getAdcString(ConfigurationManager.HUB_DESCRIPTION));
        }

        this.setWeight(100);
        this.setClientType(ClientType.HUB | ClientType.BOT); // Client Type 32 is hub
        this.setValidated();
        this.setActive(true);
        this.setMustBeDisconnected(false);

        // load info about bot from db
        this.loadInfo();
    }


    @Override
    public String getINF()
    {
        BigTextManager bigTextManager = new BigTextManager();
        // hub description == hub topic
        String hubDescription = bigTextManager.getText(BigTextManager.TOPIC);

        if (hubDescription != null &&
            !hubDescription.isEmpty() &&
            !hubDescription.equals(""))
        {
            this.setDescription(hubDescription);
        }
        else if (!ConfigurationManager.instance()
                                      .getAdcString(ConfigurationManager.HUB_DESCRIPTION)
                                      .isEmpty())
        {
            this.setDescription(ConfigurationManager.instance()
                                                    .getAdcString(ConfigurationManager.HUB_DESCRIPTION));
        }


        return super.getINF();
    }
}
