/*
 * ClientManager.java
 *
 * Created on 12 september 2011, 15:30
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

package ru.sincore;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides user management control functionality
 * It is a Singleton. It's implementation describes in
 * http://www.javenue.info/post/83 (Russian)
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-12
 */
public final class ClientManager
{
    private static final Logger log = LoggerFactory.getLogger(ClientManager.class);

    public static ConcurrentHashMap<String, Client> clientsByCID;
    private static ConcurrentHashMap<String, Client> clientsByNick;
    private static ConcurrentHashMap<String, Client> clientsBySID;

// *********************** Singleton implementation start ********************************
    private static volatile Strategy strategy = new CreateAndReturnStrategy();
    private static ClientManager instance;


    private static interface Strategy
    {
        ClientManager getInstance();
    }


    private static class ReturnStrategy implements Strategy
    {
        public final ClientManager getInstance()
        {
            return instance;
        }
    }


    private static class CreateAndReturnStrategy implements Strategy
    {
        public final ClientManager getInstance()
        {
            synchronized (ClientManager.class)
            {
                if (instance == null)
                {
                    instance = new ClientManager();
                    strategy = new ReturnStrategy();
                }
            }
            return instance;
        }
    }


    public static ClientManager getInstance()
    {
        return strategy.getInstance();
    }
// *********************** Singleton implementation end ********************************


    private ClientManager()
    {
        // TODO move initial capacity and load factor to config
        clientsByCID    = new ConcurrentHashMap<String, Client>(3000, (float) 0.75);
        clientsByNick   = new ConcurrentHashMap<String, Client>(3000, (float) 0.75);
        clientsBySID    = new ConcurrentHashMap<String, Client>(3000, (float) 0.75);
    }


    synchronized public void addClient (Client client)
    {
        clientsByCID.put(client.getClientHandler().ID, client);
        clientsByNick.put(client.getClientHandler().NI, client);
        clientsBySID.put(client.getClientHandler().SID, client);
    }


    synchronized public void removeAllClients()
    {
        // For all clients
        for (Client client : clientsByCID.values())
        {
            // Remove client attribute from all sessions
            client.getClientHandler().session.removeAttribute("client", client);
            // Close connection
            client.getClientHandler().session.close(true);
        }

        // Remove all clients from client lists
        clientsByCID.clear();
        clientsByNick.clear();
        clientsBySID.clear();
    }


    /**
     * Return collection of clients
     * @return collection of clients
     */
    public Collection<Client> getClients()
    {
        return clientsByCID.values();
    }


    /**
     * Return client with {@link ClientHandler#ID} equals cid
     * @param cid Client ID {@link ClientHandler#ID}
     * @return Client
     */
    public Client getClientByCID (String cid)
    {
        return clientsByCID.get(cid);
    }


    public Client getClientBySID (String sid)
    {
        return clientsBySID.get(sid);
    }


    public int getClientsCount()
    {
        if (clientsByCID.size() != clientsByNick.size() ||
            clientsByNick.size() != clientsBySID.size())
            log.error("Sizes of hashmaps with links to clients not equal!");

        return clientsByCID.size();
    }


    public int getValidatedClientsCount()
    {
        int count = 0;

        for (Client client : clientsByCID.values())
        {
            if (client.getClientHandler().validated == 1)
                count++;
        }

        return count;
    }


    public long getTotalShare()
    {
        long ret = 0;
        for (Client client : clientsByCID.values())
        {
            try
            {
                if (client.getClientHandler().SS != null)
                {
                    ret += Long.parseLong(client.getClientHandler().SS);
                }
            }
            catch (NumberFormatException ignored)
            {
            }
        }
        return ret;
    }


    public long getTotalFileCount()
    {
        long ret = 0;
        for (Client client : clientsByCID.values())
        {
            try
            {
                if (client.getClientHandler().SF != null)
                {
                    ret += Long.parseLong(client.getClientHandler().SF);
                }
            }
            catch (NumberFormatException ignored)
            {
            }
        }
        return ret;
    }


    public boolean removeClient (Client client)
    {
        Client removedClient = clientsByCID.remove(client.getClientHandler().ID);
        clientsByNick.remove(client.getClientHandler().NI);
        clientsBySID.remove(client.getClientHandler().SID);

        if (removedClient == null)
            log.debug("User with cid = \'" + client.getClientHandler().ID + "\' not in clientsByCID.");
        else
            log.debug("User with cid = \'" + removedClient.getClientHandler().ID +
                      "\' and nick = \'" + removedClient.getClientHandler().NI +
                      "\' was removed.");

        return removedClient != null;
    }


    public void removeClientByCID (String cid)
    {
        Client client = clientsByCID.remove(cid);
        if (client == null)
            log.debug("User with cid = \'" + cid + "\' not in clientsByCID.");

        if (client != null)
        {
            clientsBySID.remove(client.getClientHandler().ID);
            clientsByNick.remove(client.getClientHandler().NI);

            log.debug("User with cid = \'" + cid + "\' and nick = \'" + client.getClientHandler().NI + "\' was removed.");
        }
    }


    public boolean containClientByCID(String cid)
    {
        return clientsByCID.containsKey(cid);
    }
}
