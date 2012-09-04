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

import ru.sincore.adc.ClientType;
import ru.sincore.client.AbstractClient;

/**
 * Permanent thread that keeps clients connected ( meaning killing the ones who
 * are disconnected). Also sends delayed searches and will be used for cron-like jobs.
 *
 * @author Pietricica
 */
public class ClientAssasin extends Thread
{
    private boolean doRun = true;


    public ClientAssasin()
    {
        start();
    }


    public void stopClientAssasin()
    {
        doRun = false;
        interrupt(); // force exit from sleep()
    }


    /**
     * Disconnect clients which must be disconnected
     *
     * @param client client wich must be checked for disconnection
     * @return was client been disconnected
     */
    private boolean disconnectClient(AbstractClient client)
    {
        client.flushBuffer();

        // remove clients that must be disconnected
        if (client.isMustBeDisconnected())
        {
            client.removeSession(true);
            return true;
        }

        // remove clients wich have long keep alive timeout
        if (ConfigurationManager.getInstance()
                                .getBoolean(ConfigurationManager.DISCONNECT_BY_TIMEOUT) &&
            ((System.currentTimeMillis() - client.getLastKeepAlive()) >
             ConfigurationManager.getInstance().getLong(ConfigurationManager.MAX_KEEP_ALIVE_TIMEOUT) *
             1000) // from sec to ms
            &&
            ((client.getClientType() & ClientType.BOT) != ClientType.BOT) // don't remove bots
           )
        {
            client.removeSession(true);
            return true;
        }

        return false;
    }

    @Override
    public void run()
    {
        while (doRun)
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException ex)
            {
                // ignored
            }

            ConfigurationManager configurationManager = ConfigurationManager.getInstance();

            for (AbstractClient client : ClientManager.getInstance().getUninitializedClients())
            {
                if (System.currentTimeMillis() - client.getLastBufferFlushTime() >
                    configurationManager.getLong(ConfigurationManager.MESSAGE_BUFFER_FLUSH_PERIOD))
                {
                    client.flushBuffer();
                }

                if (disconnectClient(client))
                {
                    // do something with just disconnected clients
                    continue;
                }
            }


            for (AbstractClient client : ClientManager.getInstance().getClients())
            {
                long currentTime = System.currentTimeMillis();

                if (currentTime - client.getLastBufferFlushTime() >
                    configurationManager.getLong(ConfigurationManager.MESSAGE_BUFFER_FLUSH_PERIOD))
                {
                    client.flushBuffer();
                }

                if (disconnectClient(client))
                {
                    continue;
                }

                if (((client.getInQueueSearch() != null))
                    && (client.isValidated()))
                {
                    double xy = 1;
                    for (int i = 0; i < client.getSearchStep(); i++)
                    {
                        xy *= ((double) configurationManager.getInt(ConfigurationManager.SEARCH_BASE_INTERVAL)) / 1000;
                    }
                    xy *= 1000;
                    long xx = (long) xy;
                    if (client.getSearchStep() >= configurationManager.getInt(ConfigurationManager.SEARCH_STEPS))
                    {
                        xx = configurationManager.getInt(ConfigurationManager.SEARCH_SPAM_RESET) * 1000;
                    }
                    if ((currentTime - client.getLastSearch()) > xx)
                    {

                        Broadcast.getInstance().broadcast(client.getInQueueSearch(), client);

                        client.setInQueueSearch(null);
                        client.setLastSearch(currentTime);
                    }
                }
            }
        }
    }

}
