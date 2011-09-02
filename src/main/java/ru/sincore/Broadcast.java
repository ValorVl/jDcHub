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

package dshub;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import dshub.conf.Vars;

class line
{
    public String curline;

    line Next;


    public line(String str)
    {

        Next = null;
        curline = str;
    }
}


/**
 * Provides broadcasts and feature broadcasts constructors to all connected
 * clients.
 *
 * @author Pietricica
 */
public class Broadcast
{

    public static final int STATE_ALL    = 0;
    public static final int STATE_ACTIVE = 1;

    public static final int STATE_ALL_KEY = 10;

    static line First = null;
    static line Last  = null;

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
    public void broadcast(String STR, ClientNod cur_client)
    {
        run(0, STR, cur_client);
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


    public void execute(int state, String STR, ClientNod cur_client,
                        ClientNod CH)
    {
        ClientThread ct = new ClientThread(state, STR, cur_client, CH);
        ct.run();
    }


    private class ClientThread
    {

        private final int       state;
        private final String    STR;
        private final ClientNod cur_client;
        private final ClientNod CH;


        public ClientThread(int state, String STR, ClientNod cur_client,
                            ClientNod CH)
        {
            this.state = state;
            this.STR = STR;
            this.cur_client = cur_client;
            this.CH = CH;
        }


        public void run()
        {
            String NI = "";
            // x[i]= ((IoSession) (x[i]));
            // ClientNod CH=((ClientHandler)(x[i].getAttachment())).myNod;
            if (STR.startsWith("BMSG ") || STR.startsWith("IMSG "))
            {
                NI = Vars.bot_name;
                if (CH.cur_client.userok == 1)

                {
                    if (STR.startsWith("BMSG "))
                    {
                        if (CH.cur_client.SessionID.equals(STR.substring(5, 9)))
                        {
                            NI = CH.cur_client.NI;
                        }
                    }
                }

                if ((STR.startsWith("BMSG ") && CH.cur_client.SessionID
                        .equals(STR.substring(5, 9)))
                    || STR.startsWith("IMSG "))
                {
                    if (First == null)
                    {
                        line bla;
                        if (STR.startsWith("BMSG "))
                        {
                            bla = new line("["
                                           + Calendar.getInstance().getTime()
                                                     .toString() + "] <" + NI + "> "
                                           + STR.substring(10) + "\n");
                        }
                        else
                        {
                            bla = new line("["
                                           + Calendar.getInstance().getTime()
                                                     .toString() + "] <" + NI + "> "
                                           + STR.substring(5) + "\n");
                        }
                        Last = bla;
                        First = Last;
                        size++;
                    }

                    else
                    {
                        line bla;
                        // BMSG AAAA message
                        if (STR.startsWith("BMSG "))
                        {
                            bla = new line("["
                                           + Calendar.getInstance().getTime()
                                                     .toString() + "] <" + NI + "> "
                                           + STR.substring(10) + "\n");
                        }
                        else
                        {
                            bla = new line("["
                                           + Calendar.getInstance().getTime()
                                                     .toString() + "] <" + NI + "> "
                                           + STR.substring(5) + "\n");
                        }

                        if (!(Last.curline.equals(bla.curline) && STR
                                .startsWith("IMSG ")))
                        {
                            Last.Next = bla;

                            size++;

                            Last = bla;

                            while (size >= Vars.history_lines)
                            {
                                First = First.Next;
                                size--;
                            }
                        }
                    }
                }
            }

            if ((CH.cur_client.userok == 1 && CH != cur_client)
                || (CH != cur_client && CH.cur_client.userok == 1
                    && state == 1 && CH.cur_client.ACTIVE == 1))
            {
                if (state == STATE_ALL_KEY && !CH.cur_client.reg.key)
                {
                    return;
                }
                if (CH.cur_client.ACTIVE != 1 && state == STATE_ACTIVE)
                {
                    return;
                }
                if (!STR.startsWith("E") && CH == cur_client)
                {
                    return;
                }
                if (STR.startsWith("IMSG "))
                {
                    CH.cur_client.sendFromBot(STR.substring(5));
                }
                else
                {
                    CH.cur_client.sendToClient(STR);
                }
            }
        }
    }


    public void sendToAll(int state, String STR, ClientNod cur_client)
    {
        for (ClientNod CH : SimpleHandler.getUsers())
        {
            execute(state, STR, cur_client, CH);
        }
    }


    public void run(int state, String STR, ClientNod cur_client)
    {
        sendToAll(state, STR, cur_client);
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
