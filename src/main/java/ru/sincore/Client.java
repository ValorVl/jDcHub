/*
 * ClientNod.java
 *
 * Created on 29 octombrie 2007, 11:35
 *
 * DSHub ADC HubSoft
 * Copyright (C) 2007,2008  Eugen Hristev
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

package ru.sincore;

import org.apache.log4j.Logger;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import ru.sincore.banning.BanList;
import ru.sincore.util.AdcUtils;


/**
 * A class that contains client information and provides
 * functions to work with client.
 * Contains client information in {@link ru.sincore.ClientHandler}  class instance.
 *
 * @author Pietricica
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-06
 */
public class Client implements IoFutureListener<WriteFuture>
{
    public static final Logger log = Logger.getLogger(Client.class);

    /**
     * Handler to client info
     */
    private ClientHandler handler;

    /**
     * Creates a new instance of Client
     */
    public Client()
    {
        handler = new ClientHandler();
    }


    /**
     * Return ClientHandler
     * @return handler
     */
    public ClientHandler getClientHandler()
    {
        return handler;
    }


    public void operationComplete(WriteFuture future)
    {
        // if data was not written
        if (!future.isWritten())
            // close connection immediately
            handler.getSession().close(true);
    }


    // TODO [lh] Next two functions must be rewritten to remove code duplication
    public void kickMeOut(ClientHandler whokicked,
                          String kickmsg,
                          int bantype,
                          Long kicktime)
    {
        kickmsg = AdcUtils.retNormStr(kickmsg);

        if (!handler.isKickable())
        {
            whokicked.sendFromBot("" + handler.getNI() + " is unkickable.");
            return;
        }

        if (kicktime != -1)
            // convert it from sec to ms
            kicktime *= 1000;

        switch (bantype)
        {
            // ban by nick
            case 1:
                BanList.addban(bantype, handler.getNI(), kicktime, whokicked.getNI(), kickmsg);
                break;

            // ban by ip
            case 2:
                BanList.addban(bantype, handler.getRealIP(), kicktime, whokicked.getNI(), kickmsg);
                break;

            // ban by cid
            case 3:
                BanList.addban(bantype, handler.getID(), kicktime, whokicked.getNI(), kickmsg);
                break;
        }

        String brcast = "IQUI " +
                        handler.getSID() +
                        " ID" +
                        whokicked.getSID() +
                        " TL" +
                        Long.toString(kicktime);

        if (!kickmsg.equals(""))
            brcast = brcast + " MS" + AdcUtils.retADCStr(kickmsg);

        if (!ConfigLoader.REDIRECT_URL.equals(""))
            brcast = brcast + " RD" + AdcUtils.retADCStr(ConfigLoader.REDIRECT_URL);

        Broadcast.getInstance().broadcast(brcast);

        handler.setKicked();
        handler.getSession().close(true);


        whokicked.sendFromBot("Kicked user " +
                              handler.getNI() +
                              " with CID " +
                              handler.getID() +
                              " out in flames.");

        log.info(whokicked.getNI() +
                    " kicked user " +
                    handler.getNI() +
                    " with CID " +
                    handler.getID() +
                    " out in flames.");
    }


    public void kickMeByBot(String kickmsg, int bantype, Long kicktime)
    {
        kickmsg = AdcUtils.retNormStr(kickmsg);

        if (!handler.isKickable())
        {
            return;
        }

        if (kicktime != -1)
            kicktime *= 1000;

        switch (bantype)
        {
            // ban by nick
            case 1:
                BanList.addban(bantype, handler.getNI(), kicktime, ConfigLoader.BOT_CHAT_NAME, kickmsg);
                break;

            // ban by ip
            case 2:
                BanList.addban(bantype, handler.getRealIP(), kicktime, ConfigLoader.BOT_CHAT_NAME, kickmsg);
                break;

            // ban by cid
            case 3:
                BanList.addban(bantype, handler.getID(), kicktime, ConfigLoader.BOT_CHAT_NAME, kickmsg);
                break;
        }

        String brcast = "IQUI " +
                        handler.getSID() +
                        " ID" +
                        ConfigLoader.BOT_CHAT_SID +
                        " TL" + Long.toString(kicktime);

        if (!kickmsg.equals(""))
            brcast = brcast + " MS" + AdcUtils.retADCStr(kickmsg);

        if (!ConfigLoader.REDIRECT_URL.equals(""))
            brcast = brcast + " RD" + AdcUtils.retADCStr(ConfigLoader.REDIRECT_URL);

        Broadcast.getInstance().broadcast(brcast);

        handler.setKicked();
        this.handler.getSession().close(true);

        log.info(ConfigLoader.BOT_CHAT_NAME +
                 " kicked user " +
                 handler.getNI() +
                 " with CID " +
                 handler.getID() +
                 " out in flames.");
    }


    public void kickMeByBot(String kickmsg, int bantype)
    {
        kickMeByBot(kickmsg, bantype, Long.parseLong(Integer.toString(ConfigLoader.KICK_DURATION)));
    }


    public void kickMeOut(ClientHandler whokicked, String kickmsg, int bantype)
    {
        kickMeOut(whokicked, kickmsg, bantype, Long.parseLong(Integer.toString(ConfigLoader.KICK_DURATION)));
    }


    public void dropMeImGhost()
    {
        Broadcast.getInstance().broadcast("IQUI " + handler.getSID(), this);
        handler.setKicked();
        handler.getSession().close(true);
    }


    public void dropMe(ClientHandler whokicked)
    {
        if (!handler.isKickable())
        {
            whokicked.sendFromBot("" + handler.getNI() + " is undroppable.");
            return;
        }

        Broadcast.getInstance()
                 .broadcast("IQUI " + handler.getSID() + " ID" + whokicked.getSID());

        handler.setKicked();
        this.handler.getSession().close(true);

        whokicked.sendFromBot("Dropped user " +
                              handler.getNI() +
                              " with CID " +
                              handler.getID() +
                              " down from the sky.");
    }


    public void redirectMe(ClientHandler whokicked, String URL)
    {
        if (!handler.isKickable())
        {
            whokicked.sendFromBot("" + handler.getNI() + " is unredirectable.");
            return;
        }

        Broadcast.getInstance()
                 .broadcast("IQUI " +
                            handler.getSID() +
                            " ID" +
                            whokicked.getSID() +
                            " RD" +
                            URL);

        handler.setKicked();
        this.handler.getSession().close(true);


        whokicked.sendFromBot("Redirected user " +
                              handler.getNI() +
                              " with CID " +
                              handler.getID() +
                              " to " +
                              URL +
                              ".");
    }

}
