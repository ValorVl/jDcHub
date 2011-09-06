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
import ru.sincore.conf.Vars;
import ru.sincore.util.ADC;


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
        {
            // close connection immediately
            handler.mySession.close(true);
        }
    }


    public void kickMeOut(ClientHandler whokicked, String kickmsg, int bantype, Long kicktime)
    {
        kickMeOut(whokicked, kickmsg, bantype, kicktime, "");
    }


    public void kickMeOut(ClientHandler whokicked,
                          String kickmsg,
                          int bantype,
                          Long kicktime,
                          String extraStr)
    {
        kickmsg = ADC.retNormStr(kickmsg);
        if (!handler.reg.kickable)
        {
            whokicked.sendFromBot("" + handler.NI + " is unkickable.");
            return;
        }
        //ClientHandler tempy=FirstClient;
        // while(tempy.NextClient!=this)
        //   tempy=tempy.NextClient;
        // ClientHandler temp=tempy.NextClient;
        if (kicktime != -1)
        {
            if (bantype == 3)
            {
                BanList.addban(bantype, handler.ID, 1000 * kicktime, whokicked.NI, kickmsg);
            }
            else if (bantype == 2)
            {
                BanList.addban(bantype, handler.RealIP, 1000 * kicktime, whokicked.NI, kickmsg);
            }
            else if (bantype == 1)
            {
                BanList.addban(bantype, handler.NI, 1000 * kicktime, whokicked.NI, kickmsg);
            }
        }
        else
        {

            if (bantype == 3)
            {
                BanList.addban(bantype, handler.ID, kicktime, whokicked.NI, kickmsg);
            }
            else if (bantype == 2)
            {
                BanList.addban(bantype, handler.RealIP, kicktime, whokicked.NI, kickmsg);
            }
            else if (bantype == 1)
            {
                BanList.addban(bantype, handler.NI, kicktime, whokicked.NI, kickmsg);
            }

        }
        String brcast = "IQUI " +
                        handler.SessionID +
                        " ID" +
                        whokicked.SessionID +
                        " TL" +
                        Long.toString(kicktime);
        handler.reg.TimeOnline += System.currentTimeMillis() - handler.LoggedAt;
        if (!kickmsg.equals(""))
        {
            brcast = brcast + " MS" + ADC.retADCStr(kickmsg);
        }
        if (!Vars.redirect_url.equals(""))
        {
            brcast = brcast + " RD" + ADC.retADCStr(Vars.redirect_url);
        }
        Broadcast.getInstance().broadcast(brcast);

        handler.kicked = 1;
        this.handler.mySession.close();


        whokicked.sendFromBot("Kicked user " +
                              handler.NI +
                              " with CID " +
                              handler.ID +
                              " out in flames.");
        log.info(whokicked.NI +
                    " kicked user " +
                    handler.NI +
                    " with CID " +
                    handler.ID +
                    " out in flames.");
        Main.Server.rewritebans();
    }


    public void kickMeByBot(String kickmsg, int bantype, Long kicktime)
    {
        kickmsg = ADC.retNormStr(kickmsg);
        if (!handler.reg.kickable)
        {

            return;
        }
        //ClientHandler tempy=FirstClient;
        // while(tempy.NextClient!=this)
        //   tempy=tempy.NextClient;
        // ClientHandler temp=tempy.NextClient;
        if (kicktime != -1)
        {
            if (bantype == 3)
            {
                BanList.addban(bantype, handler.ID, 1000 * kicktime, Vars.bot_name, kickmsg);
            }
            else if (bantype == 2)
            {
                BanList.addban(bantype, handler.RealIP, 1000 * kicktime, Vars.bot_name, kickmsg);
            }
            else if (bantype == 1)
            {
                BanList.addban(bantype, handler.NI, 1000 * kicktime, Vars.bot_name, kickmsg);
            }
        }
        else
        {

            if (bantype == 3)
            {
                BanList.addban(bantype, handler.ID, kicktime, Vars.bot_name, kickmsg);
            }
            else if (bantype == 2)
            {
                BanList.addban(bantype, handler.RealIP, kicktime, Vars.bot_name, kickmsg);
            }
            else if (bantype == 1)
            {
                BanList.addban(bantype, handler.NI, kicktime, Vars.bot_name, kickmsg);
            }

        }
        String brcast = "IQUI " + handler.SessionID + " IDDCBA TL" + Long.toString(kicktime);
        handler.reg.TimeOnline += System.currentTimeMillis() - handler.LoggedAt;
        if (!kickmsg.equals(""))
        {
            brcast = brcast + " MS" + ADC.retADCStr(kickmsg);
        }
        if (!Vars.redirect_url.equals(""))
        {
            brcast = brcast + " RD" + ADC.retADCStr(Vars.redirect_url);
        }
        Broadcast.getInstance().broadcast(brcast);

        handler.kicked = 1;
        this.handler.mySession.close();


        log.info(Vars.bot_name +
                 " kicked user " +
                 handler.NI +
                 " with CID " +
                 handler.ID +
                 " out in flames.");
        Main.Server.rewritebans();
    }


    public void kickMeByBot(String kickmsg, int bantype)
    {
        kickMeByBot(kickmsg, bantype, Long.parseLong(Integer.toString(Vars.kick_time)));
    }


    public void kickMeOut(ClientHandler whokicked, String kickmsg, int bantype)
    {
        kickMeOut(whokicked, kickmsg, bantype, Long.parseLong(Integer.toString(Vars.kick_time)));
    }


    public void dropMeImGhost()
    {
        if (handler.inside)
        {
            Broadcast.getInstance().broadcast("IQUI " + handler.SessionID, this);

            //    handler.reg.TimeOnline+=System.currentTimeMillis()-handler.LoggedAt;
        }
        handler.kicked = 1;
        handler.inside = false;
        this.handler.mySession.close();
    }


    public void dropMe(ClientHandler whokicked)
    {
        if (!handler.reg.kickable)
        {
            whokicked.sendFromBot("" + handler.NI + " is undroppable.");
            return;
        }


        Broadcast.getInstance()
                 .broadcast("IQUI " + handler.SessionID + " ID" + whokicked.SessionID);

        //  handler.reg.TimeOnline+=System.currentTimeMillis()-handler.LoggedAt;

        handler.kicked = 1;
        this.handler.mySession.close();


        whokicked.sendFromBot("Dropped user " +
                              handler.NI +
                              " with CID " +
                              handler.ID +
                              " down from the sky.");
        //  Main.Server.rewritebans ();
    }


    public void redirectMe(ClientHandler whokicked, String URL)
    {
        if (!handler.reg.kickable)
        {
            whokicked.sendFromBot("" + handler.NI + " is unredirectable.");
            return;
        }


        Broadcast.getInstance()
                 .broadcast("IQUI " +
                            handler.SessionID +
                            " ID" +
                            whokicked.SessionID +
                            " RD" +
                            URL);

        //   handler.reg.TimeOnline+=System.currentTimeMillis()-handler.LoggedAt;

        handler.kicked = 1;
        this.handler.mySession.close();


        whokicked.sendFromBot("Redirected user " +
                              handler.NI +
                              " with CID " +
                              handler.ID +
                              " to " +
                              URL +
                              ".");
        //  Main.Server.rewritebans ();
    }


}
