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

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.client.AbstractClient;

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

    /**
     * Contains message history without duplicates
     * and Info messages
     */
    public static StringBuffer history;

    static int size = 0;

    // un pool de threaduri ce este folosit pentru executia secventelor de
    // operatii corespunzatoare
    // conextiunilor cu fiecare client
    // final protected ExecutorService pool;
    // final private ThreadFactory tfactory;

    static Broadcast _instance = null;


    private Broadcast()
    {
        // tfactory = new DaemonThreadFactory();
        // pool = Executors.newSingleThreadScheduledExecutor(tfactory);
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
            (new Thread(new ClientSender(message, fromClient, toClient))).start();
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
        Collection<AbstractClient> clients = ClientManager.getInstance().getClients();

        for (final AbstractClient client : clients)
        {
            boolean doSend = true;
            for (String feature : requiredFeatures)
            {
                if (!client.isFeature(feature))
                {
                    doSend = false;
                    break;
                }
            }

            for (String feature : excludedFeatures)
            {
                if (client.isFeature(feature))
                {
                    doSend = false;
                    break;
                }
            }

            if (doSend)
            {
                log.debug("Send to client: " + client.getNick() + "/" + client.getSid());
                (new Thread(new ClientSender(message, fromClient, client))).start();
            }
        }

    }


    private class ClientSender implements Runnable
    {
        private final String    message;
        private final AbstractClient    fromClient;
        private final AbstractClient    toClient;


        public ClientSender(String message, AbstractClient fromClient, AbstractClient toClient)
        {
            this.message = message;
            this.fromClient = fromClient;
            this.toClient = toClient;
        }


        public void run()
        {
            if (toClient.isActive())
            {
                if (!message.startsWith("E") && toClient.equals(fromClient))
                    return;

                toClient.sendRawCommand(message);
            }
        }
    }
}
