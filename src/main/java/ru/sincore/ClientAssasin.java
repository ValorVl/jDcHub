/*
 * ClientAssasin.java
 *
 * Created on 26 mai 2007, 19:18
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

package dshub;

import java.util.Map;

import dshub.conf.Vars;

/**
 * Permanent thread that keeps clients connected ( meaning killing the ones who
 * are disconnected). Also sends delayed searches and will be used for cronlike jobs.
 *
 * @author Pietricica
 */
public class ClientAssasin extends Thread
{

    /**
     * Creates a new instance of ClientAssasin
     */


    public ClientAssasin()
    {

        start();
    }


    public void run()
    {

        while (!Main.Server.restart)
        {

            if (SimpleHandler.Users.isEmpty())
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException ex)
                {

                }
                continue;
            }

            for (ClientNod temp : SimpleHandler.getUsers())
            {

                long curtime = System.currentTimeMillis();
                ClientNod x = temp;
                /*  synchronized (temp.cur_client.cur_inf)
                {
                if (((temp.cur_client.userok==1)
                        && (temp.cur_client.cur_inf!=null))
                        && (curtime-temp.cur_client.LastINF>(1000*120L)))
                {
                    Broadcast.getInstance().broadcast(temp.cur_client.cur_inf);
                    temp.cur_client.LastINF=curtime;
                    temp.cur_client.cur_inf=null;
        }
                }
                */

                if (((x.cur_client.kicked != 1)
                     && (x.cur_client.InQueueSearch != null))
                    && (x.cur_client.userok == 1))
                {

                    double xy = 1;
                    for (int i = 0; i < x.cur_client.search_step; i++)
                    {
                        xy *= ((double) Vars.search_log_base) / 1000;
                    }
                    xy *= 1000;
                    long xx = (long) xy;
                    if (x.cur_client.search_step >= Vars.search_steps)
                    {
                        xx = Vars.search_spam_reset * 1000;
                    }
                    // System.out.println(xx);
                    if ((curtime - x.cur_client.Lastsearch) > xx)
                    {

                        if (x.cur_client.InQueueSearch.startsWith("B"))
                        {
                            Broadcast.getInstance().broadcast(x.cur_client.InQueueSearch);
                        }
                        else
                        {
                            Broadcast.getInstance()
                                     .broadcast(x.cur_client.InQueueSearch, Broadcast.STATE_ACTIVE);
                        }
                        x.cur_client.InQueueSearch = null;
                        x.cur_client.Lastsearch = curtime;
                    }

                }


            }
            //temp.PS.printf
            // new Broadcast("");
            //System.out.println("gay.");
            //if(temp!=null)
            // new Broadcast("IMSG Debug Message Please Ignore, Check "+temp.NI);
            try
            {
                this.sleep(5000);
            }
            catch (Exception e)
            {
            }

        }
    }

}
