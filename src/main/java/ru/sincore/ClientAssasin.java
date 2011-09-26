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

package ru.sincore;

/**
 * Permanent thread that keeps clients connected ( meaning killing the ones who
 * are disconnected). Also sends delayed searches and will be used for cron-like jobs.
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


    @Override
    public void run()
    {

        while (!Main.server.restart)
        {

            if (ClientManager.getInstance().getClientsCount() == 0)
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException ex)
                {
                    // ignored
                }
                continue;
            }

            for (Client client : ClientManager.getInstance().getClients())
            {
                ClientHandler clientHandler = client.getClientHandler();

                long currentTime = System.currentTimeMillis();

                if (((clientHandler.kicked != 1)
                     && (clientHandler.inQueueSearch != null))
                    && (clientHandler.validated == 1))
                {

                    double xy = 1;
                    for (int i = 0; i < clientHandler.searchStep; i++)
                    {
                        xy *= ((double) ConfigLoader.SEARCH_BASE_INTERVAL) / 1000;
                    }
                    xy *= 1000;
                    long xx = (long) xy;
                    if (clientHandler.searchStep >= ConfigLoader.SEARCH_STEPS)
                    {
                        xx = ConfigLoader.SEARCH_SPAM_RESET * 1000;
                    }
                    if ((currentTime - clientHandler.lastSearch) > xx)
                    {

                        if (clientHandler.inQueueSearch.startsWith("B"))
                        {
                            Broadcast.getInstance().broadcast(clientHandler.inQueueSearch);
                        }
                        else
                        {
                            Broadcast.getInstance()
                                     .broadcast(clientHandler.inQueueSearch, Broadcast.STATE_ACTIVE);
                        }
                        clientHandler.inQueueSearch = null;
                        clientHandler.lastSearch = currentTime;
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
