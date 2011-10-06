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
import ru.sincore.adc.State;

import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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

    private static ConcurrentHashMap<String, Client> clientsBySID;

    private static Vector<Client> uninitializedClients;

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
        int initialCapacity = ConfigurationManager.instance().getInt(ConfigurationManager.USER_INITIAL_CAPACITY);
        float loadFactor = ConfigurationManager.instance().getFloat(ConfigurationManager.USER_LOAD_FACTOR);

        clientsBySID    = new ConcurrentHashMap<String, Client>(initialCapacity, loadFactor);

        uninitializedClients = new Vector<Client>(ConfigurationManager.instance().getInt(
                ConfigurationManager.USER_CONNECTION_BUFFER_INITIAL_SIZE));
    }


    public void addClient (Client client)
    {
        clientsBySID.put(client.getClientHandler().getSID(), client);
    }


    public void addNewClient(Client client)
    {
        client.getClientHandler().setState(State.PROTOCOL);
        uninitializedClients.add(client);
    }

    synchronized public void moveClientToRegularMap(Client client)
    {
        if (!uninitializedClients.remove(client))
            log.error("Client was not found in uninitialized clients vector!");

        addClient(client);
    }

    synchronized public void removeAllClients()
    {
        // For all clients
        for (Client client : clientsBySID.values())
        {
            // Remove client attribute from all sessions
            client.getClientHandler().getSession().removeAttribute("client", client);
            // Close connection
            client.getClientHandler().getSession().close(true);
        }

        // Remove all clients from client lists
        clientsBySID.clear();
    }


    /**
     * Return collection of clients
     * @return collection of clients
     */
    public Collection<Client> getClients()
    {
        return clientsBySID.values();
    }


    /**
     * Return client with {@link ClientHandler#SID} equals to sid
     * @param sid Client SID {@link ClientHandler#SID}
     * @return Client
     */
    public Client getClientBySID (String sid)
    {
        return clientsBySID.get(sid);
    }


    public int getClientsCount()
    {
        return clientsBySID.size();
    }


    public int getValidatedClientsCount()
    {
        int count = 0;

        for (Client client : clientsBySID.values())
        {
            if (client.getClientHandler().isValidated())
                count++;
        }

        return count;
    }


    public long getTotalShare()
    {
        long ret = 0;
        for (Client client : clientsBySID.values())
        {
            try
            {
                ret += client.getClientHandler().getSS();
            }
            catch (ArithmeticException ae)
            {
                log.error("Exception in total share size calculation : " + ae);
            }
        }
        return ret;
    }


    public long getTotalFileCount()
    {
        long ret = 0;
        for (Client client : clientsBySID.values())
        {
            try
            {
                ret += client.getClientHandler().getSF();
            }
            catch (ArithmeticException ae)
            {
                log.error("Exception in total file count calculation : " + ae);
            }
        }
        return ret;
    }


    synchronized public boolean removeClient (Client client)
    {
        Client removedClient = clientsBySID.remove(client.getClientHandler().getSID());

        if (removedClient == null)
            log.debug("User with sid = \'" + client.getClientHandler().getSID() + "\' not in clientsBySID.");
        else
            log.debug("User with nick = \'" + removedClient.getClientHandler().getNI() +
                      "\' and sid = \'" + removedClient.getClientHandler().getSID() +
                      "\' was removed.");

        return removedClient != null;
    }

}
