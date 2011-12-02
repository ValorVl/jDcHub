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
import ru.sincore.adc.ClientType;
import ru.sincore.adc.State;
import ru.sincore.client.AbstractClient;
import ru.sincore.client.Bot;
import ru.sincore.client.ChatRoom;
import ru.sincore.client.Client;

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

    private static ConcurrentHashMap<String, AbstractClient> clientsBySID;
    private static ConcurrentHashMap<String, String>         sidByNick;
    private static ConcurrentHashMap<String, String>         sidByCID;

    private static ConcurrentHashMap<String, AbstractClient> uninitializedClients;

    // *********************** Singleton implementation start ********************************
    private static volatile Strategy strategy = new CreateAndReturnStrategy();
    private static ClientManager instance;

    private ConfigurationManager configurationManager = ConfigurationManager.instance();


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
        int initialCapacity =
                ConfigurationManager.instance().getInt(ConfigurationManager.USER_INITIAL_CAPACITY);
        float loadFactor =
                ConfigurationManager.instance().getFloat(ConfigurationManager.USER_LOAD_FACTOR);

        clientsBySID = new ConcurrentHashMap<String, AbstractClient>(initialCapacity, loadFactor);
        sidByNick = new ConcurrentHashMap<String, String>(initialCapacity, loadFactor);
        sidByCID = new ConcurrentHashMap<String, String>(initialCapacity, loadFactor);

        uninitializedClients = new ConcurrentHashMap<String, AbstractClient>(
                ConfigurationManager.instance()
                                    .getInt(ConfigurationManager.USER_CONNECTION_BUFFER_INITIAL_SIZE),
                loadFactor);

        addBots();
        addChatRooms();
    }


    private void addBots()
    {
        // Create new Hub Bot
        Bot bot = new Bot();
        bot.setSid(configurationManager.getString(ConfigurationManager.HUB_SID));
        bot.setCid(configurationManager.getString(ConfigurationManager.SECURITY_CID));
        bot.setNick(configurationManager.getString(ConfigurationManager.HUB_NAME));
        bot.setDescription(configurationManager.getAdcString(ConfigurationManager.HUB_DESCRIPTION));

        // TODO [lh] Remove code duplication
        // duplicated code placed here: SUPHandler#sendClientInitializationInfo
        if (!ConfigurationManager.instance().getAdcString(ConfigurationManager.HUB_DESCRIPTION).isEmpty())
        {
            bot.setDescription(ConfigurationManager.instance().getAdcString(ConfigurationManager.HUB_DESCRIPTION));
        }

        bot.setWeight(100);
        bot.setClientType(ClientType.HUB | ClientType.BOT); // Client Type 32 is hub
        bot.setValidated();

        // load info about bot from db
        bot.loadInfo();

        // add bot to client list
        addClient(bot);
    }


    private void addChatRooms()
    {
        ChatRoom opChat = new ChatRoom();
        opChat.setSid(configurationManager.getString(ConfigurationManager.OP_CHAT_SID));
        opChat.setCid(configurationManager.getString(ConfigurationManager.OP_CHAT_CID));
        opChat.setNick(configurationManager.getString(ConfigurationManager.OP_CHAT_NAME));
        opChat.setDescription(configurationManager.getString(ConfigurationManager.OP_CHAT_DESCRIPTION));
        opChat.setWeight(configurationManager.getInt(ConfigurationManager.OP_CHAT_WEIGHT));
        opChat.setClientType(ClientType.BOT | ClientType.OPERATOR);
        opChat.setValidated();
        opChat.loadInfo();
        addClient(opChat);

        ChatRoom vipChat = new ChatRoom();
        vipChat.setSid(configurationManager.getString(ConfigurationManager.VIP_CHAT_SID));
        vipChat.setCid(configurationManager.getString(ConfigurationManager.VIP_CHAT_CID));
        vipChat.setNick(configurationManager.getString(ConfigurationManager.VIP_CHAT_NAME));
        vipChat.setDescription(configurationManager.getString(ConfigurationManager.VIP_CHAT_DESCRIPTION));
        vipChat.setWeight(configurationManager.getInt(ConfigurationManager.VIP_CHAT_WEIGHT));
        vipChat.setClientType(ClientType.BOT | ClientType.OPERATOR);
        vipChat.setValidated();
        vipChat.loadInfo();
        addClient(vipChat);

    }


    public void addClient(AbstractClient client)
    {
        clientsBySID.put(client.getSid(), client);
        sidByNick.put(client.getNick(), client.getSid());
        sidByCID.put(client.getCid(), client.getSid());
    }


    public void addNewClient(AbstractClient client)
    {
        client.setState(State.PROTOCOL);
        uninitializedClients.put(client.getSid(), client);
    }


    synchronized public void moveClientToRegularMap(AbstractClient client)
    {
        if (uninitializedClients.remove(client.getSid()) == null)
        {
            log.error("Client was not found in uninitialized clients vector!");
        }

        addClient(client);
    }


    public void removeAllClients()
    {
        // For all clients
        for (AbstractClient client : clientsBySID.values())
        {
            if (client instanceof Client)
            {
                Client tempClient = (Client) client;
                // Close connection
                tempClient.removeSession(true);
            }
        }

        // Remove all clients from client lists
        clientsBySID.clear();
        sidByNick.clear();
        sidByCID.clear();
    }


    /**
     * Return collection of clients
     *
     * @return collection of clients
     */
    public Collection<AbstractClient> getClients()
    {
        return clientsBySID.values();
    }


    public Collection<AbstractClient> getUninitializedClients()
    {
        return uninitializedClients.values();
    }


    public AbstractClient getClientByCID(String cid)
    {
        String sid = sidByCID.get(cid);
        if (sid == null)
        {
            return null;
        }

        return clientsBySID.get(sid);
    }


    /**
     * Return client with {@link Client#nick} equals to nick
     *
     * @param nick Client nick (NI)
     *
     * @return Client
     */
    public AbstractClient getClientByNick(String nick)
    {
        String sid = sidByNick.get(nick);
        if (sid == null)
        {
            return null;
        }

        return clientsBySID.get(sid);
    }


    /**
     * Return client with {@link Client#sid} equals to sid
     *
     * @param sid Client SID {@link ru.sincore.client.Client#sid}
     *
     * @return Client
     */
    public AbstractClient getClientBySID(String sid)
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

        for (AbstractClient client : clientsBySID.values())
        {
            if (client.isValidated())
            {
                count++;
            }
        }

        return count;
    }


    public long getTotalShare()
    {
        long ret = 0;
        for (AbstractClient client : clientsBySID.values())
        {
            try
            {
                ret += client.getShareSize();
            }
            catch (ArithmeticException ae)
            {
                log.error("Exception in total share size calculation : " + ae);
            }
            catch (NullPointerException ex)
            {
                log.debug("Client " + client.getNick() + " doesn\'t have share.");
            }
        }
        return ret;
    }


    public long getTotalFileCount()
    {
        long ret = 0;
        for (AbstractClient client : clientsBySID.values())
        {
            try
            {
                ret += client.getSharedFiles();
            }
            catch (ArithmeticException ae)
            {
                log.error("Exception in total file count calculation : " + ae);
            }
        }
        return ret;
    }


    synchronized public boolean removeClient(AbstractClient client)
    {
        if (uninitializedClients.remove(client.getSid()) != null)
        {
            log.debug("Uninitialized client with sid = \'" +
                      client.getSid() +
                      "\' was removed.");
            return true;
        }

        AbstractClient removedClient = clientsBySID.remove(client.getSid());

        if (removedClient == null)
        {
            log.debug("User with sid = \'" + client.getSid() + "\' not in clientsBySID.");
        }
        else
        {
            sidByNick.remove(removedClient.getNick());

            log.debug("User with nick = \'" + removedClient.getNick() +
                      "\' and sid = \'" + removedClient.getSid() +
                      "\' was removed.");
        }

        return removedClient != null;
    }


    public void sendClientsInfsToClient(AbstractClient client)
    {
        for (AbstractClient oldClient : clientsBySID.values())
        {
            if (oldClient.isValidated() && !oldClient.equals(client))
            {
                client.sendRawCommand(oldClient.getINF());
            }
        }
    }
}
