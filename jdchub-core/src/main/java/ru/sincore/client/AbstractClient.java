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
import ru.sincore.ConfigurationManager;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.action.actions.AbstractAction;
import ru.sincore.util.AdcUtils;

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
                            AdcUtils.toAdcString(message) +
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
                            AdcUtils.toAdcString(message)
                           );
    }


    /**
     * Handler is called when user is logged in
     */
    public void onLoggedIn()
    {
        log.error("Used unimplemented function onLoggedIn!");
    }


    /**
     * Handler is called when user is connected and after logged in process
     */
    public void onConnected()
    {
        log.error("Used unimplemented function onConnected!");
    }


    /**
     * @deprecated use sendAdcAction() instead
     * @param rawCommand
     */
    @Deprecated
    public void sendRawCommand(String rawCommand)
    {
        log.error("Used unimplemented function sendRawCommand!");
    }


    public void sendAdcAction(AbstractAction action)
    {
        try
        {
            sendRawCommand(action.getRawCommand());
        }
        catch (CommandException e)
        {
            e.printStackTrace();
        }
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
}
