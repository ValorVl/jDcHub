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

/**
 * Provides broadcasts and feature broadcasts constructors to all connected
 * clients.
 *
 * @author Pietricica
 *
 * @author Alexey 'lh' Antonov
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
    public void broadcast(String message, Client fromClient)
    {
        for (Client toClient : ClientManager.getInstance().getClients())
        {
            Thread sendThread = new Thread(new ClientSender(message, fromClient, toClient));
            sendThread.start();
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


    private class ClientSender implements Runnable
    {
        private final String    message;
        private final Client    fromClient;
        private final Client    toClient;


        public ClientSender(String message, Client fromClient, Client toClient)
        {
            this.message = message;
            this.fromClient = fromClient;
            this.toClient = toClient;
        }


        public void run()
        {
            ClientHandler toClientHandler = toClient.getClientHandler();

            if (toClientHandler.isActive())
            {
                // TODO may be buggie
                if (!message.startsWith("E") && toClient.equals(fromClient))
                {
                }
                else if (message.startsWith("I"))
                {
                    toClient.getClientHandler().getSession().write(message);
                }
                else
                {
                    toClientHandler.sendToClient(message);
                }
            }
        }
    }
}
