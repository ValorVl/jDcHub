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

    public static final int STATE_ALL    = 0;
    public static final int STATE_ACTIVE = 1;

    public static final int STATE_ALL_KEY = 10;

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
    public void broadcast(String STR, Client client)
    {
        run(0, STR, client);
    }


    /**
     * state =STATE_ALL normal, to all state=STATE_ACTIVE only to active state=
     * STATE_ALL_KEY to all ops;
     */
    public void broadcast(String STR, int state)
    {
        // STATE_ALL_KEY - ops only
        run(state, STR, null);
    }


    public void broadcast(String STR)
    {
        run(0, STR, null);
    }


    public void execute(int state, String STR, Client fromClient,
                        Client toClient)
    {
        ClientThread ct = new ClientThread(state, STR, fromClient, toClient);
        ct.run();
    }


    private class ClientThread
    {

        private final int       state;
        private final String    STR;
        private final Client    fromClient;
        private final Client    toClient;


        public ClientThread(int state, String STR, Client fromClient,
                            Client toClient)
        {
            this.state = state;
            this.STR = STR;
            this.fromClient = fromClient;
            this.toClient = toClient;
        }


        public void run()
        {
            ClientHandler toClientHandler = toClient.getClientHandler();

            if ((toClientHandler.validated == 1 && toClient != fromClient
                    && state == 1 && toClientHandler.active == 1))
            {
                // TODO may be buggie
                if (!STR.startsWith("E") && toClient.equals(fromClient))
                {
                    return;
                }
                if (STR.startsWith("IMSG "))
                {
                    toClientHandler.sendFromBot(STR.substring(5));
                }
                else
                {
                    toClientHandler.sendToClient(STR);
                }
            }
        }
    }


    public void sendToAll(int state, String STR, Client fromClient)
    {
        for (Client toClient : SessionManager.getUsers())
        {
            execute(state, STR, fromClient, toClient);
        }
    }


    public void run(int state, String STR, Client client)
    {
        sendToAll(state, STR, client);
    }

    /**
     * Custom thread factory used in connection pool
     */
    /*private final class DaemonThreadFactory implements ThreadFactory
        {
            public Thread newThread(Runnable r)
            {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        }
    */
}
