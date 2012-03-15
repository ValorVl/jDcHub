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
import ru.sincore.util.Constants;
import ru.sincore.util.ClientUtils;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-10-07
 */
public abstract class AbstractClient extends ClientInfo
{
    private static final Logger log = LoggerFactory.getLogger(AbstractClient.class);


    public boolean checkBannedByShare()
    {
        if (this.isBannedByShare())
        {
            // don't send same banned by share message to op chat
            if (! ((Boolean) this.getAdditionalStat(Constants.BANNED_BY_SHARE_MESSAGE_SENT)))
            {
                ClientUtils.sendMessageToOpChat(this.getNick() +
                                                 " was banned for share < " +
                                                 ConfigurationManager.getInstance()
                                                                     .getLong(ConfigurationManager.BAN_BY_SHARE_MIN_SHARE) +
                                                 " [client IP=\'" +
                                                 this.getRealIP() +
                                                 "\']");

                this.setAdditionalStat(Constants.BANNED_BY_SHARE_MESSAGE_SENT, new Boolean(true));
            }

            this.sendPrivateMessageFromHub("You was banned for share < " +
                                           ConfigurationManager.getInstance()
                                                               .getLong(ConfigurationManager.BAN_BY_SHARE_MIN_SHARE));

            return true;
        }

        return false;
    }


    public boolean checkMute()
    {
        return this.isMute();
    }


    public boolean checkNoTransfer()
    {
        return this.isNoTransfer();
    }


    public boolean checkNoSearch()
    {
        return this.isNoSearch();
    }


    public void sendPrivateMessageFromHub(String message)
    {
        this.sendRawCommand("EMSG " +
                            ConfigurationManager.getInstance()
                                                .getString(ConfigurationManager.HUB_SID) +
                            " " +
                            this.getSid() +
                            " " +
                            AdcUtils.toAdcString(message) +
                            " PM" +
                            ConfigurationManager.getInstance()
                                                .getString(ConfigurationManager.HUB_SID)
                           );
    }


    public void sendMessageFromHub(String message)
    {
        this.sendRawCommand("EMSG " +
                            ConfigurationManager.getInstance()
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
        log.debug("Used unimplemented function onLoggedIn!");
    }


    /**
     * Handler is called when user is connected and after logged in process
     */
    public void onConnected()
    {
        log.debug("Used unimplemented function onConnected!");
    }


    /**
     * @deprecated use sendAdcAction() instead
     * @param rawCommand
     */
    @Deprecated
    public void sendRawCommand(String rawCommand)
    {
        log.debug("Used unimplemented function sendRawCommand!");
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
        log.debug("Used unimplemented function loadInfo!");

        return false;
    }


    public void storeInfo()
            throws STAException
    {
        log.debug("Used unimplemented function storeInfo!");
    }
}
