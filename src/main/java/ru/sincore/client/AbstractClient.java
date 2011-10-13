/*
 * AbstractClient.java
 *
 * Created on 07 october 2011, 11:55
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
import ru.sincore.Broadcast;
import ru.sincore.ClientManager;
import ru.sincore.ConfigurationManager;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.Features;
import ru.sincore.adc.State;
import ru.sincore.util.AdcUtils;

import java.util.Date;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-10-07
 */
public abstract class AbstractClient extends ClientInfo
{
    private static final Logger log = LoggerFactory.getLogger(AbstractClient.class);


    public void sendPrivateMessageFromHub(String message)
    {
        this.sendRawCommand("EMSG " +
                            ConfigurationManager.instance()
                                                .getString(ConfigurationManager.HUB_SID) +
                            " " +
                            this.getSid() +
                            " " +
                            AdcUtils.retADCStr(message) +
                            " PM" +
                            ConfigurationManager.instance()
                                                .getString(ConfigurationManager.HUB_SID)
                           );
    }


    public void sendMessageFromHub(String message)
    {
        this.sendRawCommand("EMSG " +
                            ConfigurationManager.instance()
                                                .getString(ConfigurationManager.HUB_SID) +
                            " " +
                            this.getSid() +
                            " " +
                            AdcUtils.retADCStr(message)
                           );
    }


    /**
     * Handler is called when user is logged in
     */
    public void onLoggedIn()
    {
        this.sendRawCommand("ISTA 000 Authenticated.");

        this.setLastNick(this.getNick());
        this.setLastIP(this.getRealIP());

        //user is OK, logged in and cool
        this.setValidated();
        this.setState(State.NORMAL);
        this.setLastLogin(this.getLoggedIn());

        this.setLoggedIn(new Date());
    }


    /**
     * Handler is called when user is connected and after logged in process
     */
    public void onConnected()
    {
        // make client active
        this.setActive(true);

        ClientManager.getInstance().moveClientToRegularMap(this);

        //ok now sending infs of all others to the handler
        ClientManager.getInstance().sendClientsInfsToClient(this);

        //sending inf about itself too
        this.sendRawCommand(this.getINF());

        //ok now must send INF to all clients
        Broadcast.getInstance().broadcast(this.getINF(), this);

        if (isFeature(Features.UCMD))
        {
            //ok, he is ucmd ok, so
            this.sendRawCommand("ICMD Test CT1 TTTest");
        }
        // TODO [lh] send MOTD to client
        //this.sendFromBot(bigTextManager.getMOTD(fromClient));

    }

    public void sendRawCommand(String rawCommand)
    {
        log.error("Used unimplemented function sendRawCommand!");
    }

    public boolean loadInfo()
    {
        log.error("Used unimplemented function loadInfo!");

        return false;
    }


    public void storeInfo()
            throws STAException
    {
        log.error("Used unimplemented function storeInfo!");
    }


    public String getINF()
    {
        log.error("Used unimplemented function getINF!");
        return null;
    }
}
