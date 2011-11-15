/*
 * Broadcast.java
 *
 * Created on 23 aprilie 2007, 19:05
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.client.AbstractClient;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Provides broadcasts and feature broadcasts constructors to all connected
 * clients.
 *
 * @author Pietricica
 *
 * @author Alexey 'lh' Antonov
 * @author Alexander 'hatred' Drozdov
 * @since 2011-09-06
 */
public class Broadcast
{
    private static final Logger log = LoggerFactory.getLogger(Broadcast.class);

    // un pool de threaduri ce este folosit pentru executia secventelor de
    // operatii corespunzatoare
    // conextiunilor cu fiecare client
    // final protected ExecutorService pool;
    // final private ThreadFactory tfactory;
    static Broadcast _instance = null;
    private final ExecutorService pool;


    private Broadcast()
    {
        pool = Executors.newCachedThreadPool();
    }


    public synchronized static Broadcast getInstance()
    {
        if (_instance == null)
        {
            _instance = new Broadcast();
        }
        return _instance;
    }


    /**
     * Creates a new instance of Broadcast , sends to all except the ClientNod
     * received as param.
     */
    public void broadcast(String message, AbstractClient fromClient)
    {
        for (AbstractClient toClient : ClientManager.getInstance().getClients())
        {
            pool.execute(new ClientSender(message, fromClient, toClient));
        }
    }


    public void broadcast(String message, int state)
    {
        broadcast(message, null);
    }


    public void broadcast(String message)
    {
        broadcast(message, null);
    }


    public void broadcastTextMessage(String message)
    {
        for (AbstractClient toClient : ClientManager.getInstance().getClients())
        {
            pool.execute(new ClientSender(message, toClient));
        }
    }

    /**
     * Send droadcast message depend on given features list
     *
     * @param message               message for broadcast
     * @param fromClient            client who send message
     * @param requiredFeatures      required features list
     * @param excludedFeatures      excluded features list
     */
    public void featuredBroadcast(String message,
                                  AbstractClient fromClient,
                                  List<String> requiredFeatures,
                                  List<String> excludedFeatures)
    {
        for (AbstractClient toClient : ClientManager.getInstance().getClients())
        {
            pool.execute(new ClientSender(message, fromClient, toClient, requiredFeatures, excludedFeatures));
        }
    }


    private class ClientSender implements Runnable
    {
        private String            message = "";
        private AbstractClient    fromClient = null;
        private AbstractClient    toClient = null;
        private boolean           featured = false;
        private List<String>      requiredFeatures = null;
        private List<String>      excludedFeatures = null;


        public ClientSender(String message, AbstractClient fromClient, AbstractClient toClient,
                            List<String> requiredFeatures, List<String> excludedFeatures)
        {
            this.message = message;
            this.fromClient = fromClient;
            this.toClient = toClient;

            this.featured = true;
            this.requiredFeatures = requiredFeatures;
            this.excludedFeatures = excludedFeatures;
        }


        public ClientSender(String message, AbstractClient fromClient, AbstractClient toClient)
        {
            this(message, fromClient, toClient, null, null);
            this.featured = false;
        }


        public ClientSender(String message, AbstractClient toClient)
        {
            this(message, null, toClient);
        }


        public void run()
        {
            boolean doSend = true;

            if (featured)
            {
                for (String feature : requiredFeatures)
                {
                    if (!toClient.isFeature(feature))
                    {
                        doSend = false;
                        break;
                    }
                }

                for (String feature : excludedFeatures)
                {
                    if (toClient.isFeature(feature))
                    {
                        doSend = false;
                        break;
                    }
                }
            }

            if (toClient.isActive() && doSend)
            {
                if (fromClient == null)
                {
                    toClient.sendMessageFromHub(message);
                }
                else
                {
                    if ((!message.startsWith("E") && !message.startsWith("B")) && toClient.equals(fromClient))
                        return;

                    toClient.sendRawCommand(message);
                }
            }
        }
    }
}
