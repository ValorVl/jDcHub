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
 * A class that keeps a ClientHandler thread instance and connections to other nods
 *
 * @author Pietricica
 */
public class ClientNod implements IoFutureListener<WriteFuture>
{
    public static final Logger log = Logger.getLogger(ClientNod.class);

    public ClientHandler cur_client;

    /**
     * Creates a new instance of ClientNod
     */
    public ClientNod()
    {
        //  NextClient=null;
        cur_client = new ClientHandler();
        //  if(FirstClient==null)
        //      FirstClient=this;
        cur_client.myNod = this;

    }


    public ClientNod(int x)
    {

    }


    public void operationComplete(WriteFuture future)
    {
        if (!future.isWritten())
        {
            cur_client.mySession.close();
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
        if (!cur_client.reg.kickable)
        {
            whokicked.sendFromBot("" + cur_client.NI + " is unkickable.");
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
                BanList.addban(bantype, cur_client.ID, 1000 * kicktime, whokicked.NI, kickmsg);
            }
            else if (bantype == 2)
            {
                BanList.addban(bantype, cur_client.RealIP, 1000 * kicktime, whokicked.NI, kickmsg);
            }
            else if (bantype == 1)
            {
                BanList.addban(bantype, cur_client.NI, 1000 * kicktime, whokicked.NI, kickmsg);
            }
        }
        else
        {

            if (bantype == 3)
            {
                BanList.addban(bantype, cur_client.ID, kicktime, whokicked.NI, kickmsg);
            }
            else if (bantype == 2)
            {
                BanList.addban(bantype, cur_client.RealIP, kicktime, whokicked.NI, kickmsg);
            }
            else if (bantype == 1)
            {
                BanList.addban(bantype, cur_client.NI, kicktime, whokicked.NI, kickmsg);
            }

        }
        String brcast = "IQUI " +
                        cur_client.SessionID +
                        " ID" +
                        whokicked.SessionID +
                        " TL" +
                        Long.toString(kicktime);
        cur_client.reg.TimeOnline += System.currentTimeMillis() - cur_client.LoggedAt;
        if (!kickmsg.equals(""))
        {
            brcast = brcast + " MS" + ADC.retADCStr(kickmsg);
        }
        if (!Vars.redirect_url.equals(""))
        {
            brcast = brcast + " RD" + ADC.retADCStr(Vars.redirect_url);
        }
        Broadcast.getInstance().broadcast(brcast);

        cur_client.kicked = 1;
        this.cur_client.mySession.close();


        whokicked.sendFromBot("Kicked user " +
                              cur_client.NI +
                              " with CID " +
                              cur_client.ID +
                              " out in flames.");
        log.info(whokicked.NI +
                    " kicked user " +
                    cur_client.NI +
                    " with CID " +
                    cur_client.ID +
                    " out in flames.");
        Main.Server.rewritebans();
    }


    public void kickMeByBot(String kickmsg, int bantype, Long kicktime)
    {
        kickmsg = ADC.retNormStr(kickmsg);
        if (!cur_client.reg.kickable)
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
                BanList.addban(bantype, cur_client.ID, 1000 * kicktime, Vars.bot_name, kickmsg);
            }
            else if (bantype == 2)
            {
                BanList.addban(bantype, cur_client.RealIP, 1000 * kicktime, Vars.bot_name, kickmsg);
            }
            else if (bantype == 1)
            {
                BanList.addban(bantype, cur_client.NI, 1000 * kicktime, Vars.bot_name, kickmsg);
            }
        }
        else
        {

            if (bantype == 3)
            {
                BanList.addban(bantype, cur_client.ID, kicktime, Vars.bot_name, kickmsg);
            }
            else if (bantype == 2)
            {
                BanList.addban(bantype, cur_client.RealIP, kicktime, Vars.bot_name, kickmsg);
            }
            else if (bantype == 1)
            {
                BanList.addban(bantype, cur_client.NI, kicktime, Vars.bot_name, kickmsg);
            }

        }
        String brcast = "IQUI " + cur_client.SessionID + " IDDCBA TL" + Long.toString(kicktime);
        cur_client.reg.TimeOnline += System.currentTimeMillis() - cur_client.LoggedAt;
        if (!kickmsg.equals(""))
        {
            brcast = brcast + " MS" + ADC.retADCStr(kickmsg);
        }
        if (!Vars.redirect_url.equals(""))
        {
            brcast = brcast + " RD" + ADC.retADCStr(Vars.redirect_url);
        }
        Broadcast.getInstance().broadcast(brcast);

        cur_client.kicked = 1;
        this.cur_client.mySession.close();


        log.info(Vars.bot_name +
                    " kicked user " +
                    cur_client.NI +
                    " with CID " +
                    cur_client.ID +
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
        if (cur_client.inside)
        {
            Broadcast.getInstance().broadcast("IQUI " + cur_client.SessionID, cur_client.myNod);

            //    cur_client.reg.TimeOnline+=System.currentTimeMillis()-cur_client.LoggedAt;
        }
        cur_client.kicked = 1;
        cur_client.inside = false;
        this.cur_client.mySession.close();
    }


    public void dropMe(ClientHandler whokicked)
    {
        if (!cur_client.reg.kickable)
        {
            whokicked.sendFromBot("" + cur_client.NI + " is undroppable.");
            return;
        }


        Broadcast.getInstance()
                 .broadcast("IQUI " + cur_client.SessionID + " ID" + whokicked.SessionID);

        //  cur_client.reg.TimeOnline+=System.currentTimeMillis()-cur_client.LoggedAt;

        cur_client.kicked = 1;
        this.cur_client.mySession.close();


        whokicked.sendFromBot("Dropped user " +
                              cur_client.NI +
                              " with CID " +
                              cur_client.ID +
                              " down from the sky.");
        //  Main.Server.rewritebans ();
    }


    public void redirectMe(ClientHandler whokicked, String URL)
    {
        if (!cur_client.reg.kickable)
        {
            whokicked.sendFromBot("" + cur_client.NI + " is unredirectable.");
            return;
        }


        Broadcast.getInstance()
                 .broadcast("IQUI " +
                            cur_client.SessionID +
                            " ID" +
                            whokicked.SessionID +
                            " RD" +
                            URL);

        //   cur_client.reg.TimeOnline+=System.currentTimeMillis()-cur_client.LoggedAt;

        cur_client.kicked = 1;
        this.cur_client.mySession.close();


        whokicked.sendFromBot("Redirected user " +
                              cur_client.NI +
                              " with CID " +
                              cur_client.ID +
                              " to " +
                              URL +
                              ".");
        //  Main.Server.rewritebans ();
    }


}
