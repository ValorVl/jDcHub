/*
 * SimpleHandler.java
 *
 * Created on 06 noiembrie 2007, 14:34
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

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.Modules.Modulator;
import ru.sincore.Modules.Module;
import ru.sincore.TigerImpl.Base32;
import ru.sincore.util.STAError;

import java.util.Collection;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Pietricica
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-06
 */
public class SessionManager extends IoHandlerAdapter
{
    public static final Logger log = Logger.getLogger(SessionManager.class);

    public static ConcurrentHashMap<String, Client> users;


    static
    {
        users = new ConcurrentHashMap<String, Client>(3000, (float) 0.75);
    }


    /**
     * Creates a new instance of SessionManager
     */
    public SessionManager()
    {
    }


    public void Disconnect(IoSession session)
    {
        ClientHandler cur_client = ((ClientHandler) (session.getAttribute("")));
        if (cur_client.closingwrite != null)
        {
            try
            {
                cur_client.closingwrite.await();
            }
            catch (InterruptedException e)
            {
                log.error(e);
            }
        }
        session.close(false);
    }


    public static synchronized Collection<Client> getUsers()
    {
        return users.values();
    }


    public void exceptionCaught(IoSession session, Throwable t)
            throws Exception
    {
        //System.out.println(t.getMessage());
        //t.printStackTrace();
        // if((t.getMessage().contains("IOException")))
        try
        {
            if (t instanceof java.io.IOException)
            {
                // Main.PopMsg(t.getMessage());
                //t.printStackTrace();
                //session.close(false);
                return;
            }
            if (t.getMessage().contains("java.nio.charset.MalformedInputException"))
            {
                ((ClientHandler) (session.getAttribute("")))
                        .sendFromBot(
                                "Unicode Exception. Your client sent non-Unicode chars. Ignored.");
                return;
            }
            if ((t.getMessage().contains("BufferDataException: Line is too long")))
            {
                new STAError((Client) (session.getAttribute("")), 100,
                             "Message exceeds buffer." + t.getMessage());
            }
            else
            {
                log.debug(t);
                //session.close(false);
            }
        }
        catch (Exception e)
        {
            log.debug("Funny exception in exceptionCaught(), here is the stack trace:" + e);
        }
    }


    public void messageReceived(IoSession session, Object msg)
            throws Exception
    {
        String str = (String) msg;
		log.debug("Incoming message : "+ str);

        try
        {
            new Command((Client) (session.getAttribute("")), str);
        }
        catch (STAException stex)
        {
            if (stex.x < 200)
            {
                return;
            }
            //session.close(false);
			log.info(stex);
        }
        catch (CommandException cfex)
        {
            //session.close(false);
			log.info(cfex);
        }

    }


    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception
    {
        //ok, we're in idle
		log.info(status);
    }


    public void sessionClosed(IoSession session)
            throws Exception
    {
        Client currentClient = (Client) (session.getAttribute(""));
        ClientHandler currentClientHandler = currentClient.getClientHandler();

        if (currentClientHandler.userok == 1 && currentClientHandler.kicked != 1)
        {
            // TODO COMMAND broadcast client quited message
            //Broadcast.getInstance().broadcast("IQUI " + currentClientHandler.SessionID, currentClient);
        }
        /** calling plugins...*/

        for (Module myMod : Modulator.myModules)
        {
            myMod.onClientQuit(currentClientHandler);
        }
        currentClientHandler.reg.TimeOnline += System.currentTimeMillis()
                                     - currentClientHandler.LoggedAt;

        log.info(currentClientHandler.NI+" with SID " + currentClientHandler.SessionID + " just quited.");

        SessionManager.users.remove(currentClientHandler.ID);
    }


    public void sessionOpened(IoSession session)
            throws Exception
    {

		Client currentClient = HubServer.AddClient();

		ClientHandler currentClientHandler = currentClient.getClientHandler();

        session.setAttribute("", currentClient);

        currentClientHandler.mySession = session;
        StringTokenizer ST = new StringTokenizer(currentClientHandler.mySession
                                                         .getRemoteAddress().toString(), "/:");

		currentClientHandler.RealIP = ST.nextToken();
        SID cursid = new SID(currentClientHandler);
        currentClientHandler.SessionID = Base32.encode(cursid.cursid).substring(0, 4);
        currentClientHandler.sid = cursid.cursid;

    }


    public static int getUserCount()
    {
        int ret = 0;
        for (Client client : SessionManager.getUsers())
        {

            if (client.getClientHandler().userok == 1)
            {
                ret++;
            }
        }
        return ret;
    }


    public static long getTotalShare()
    {
        long ret = 0;
        for (Client client : SessionManager.getUsers())
        {

            try
            {
                if (client.getClientHandler().userok == 1)
                {
                    if (client.getClientHandler().SS != null)
                    {
                        ret += Long.parseLong(client.getClientHandler().SS);
                    }
                }
            }
            catch (NumberFormatException ignored)
            {
            }
        }
        return ret;
    }


    public static long getTotalFileCount()
    {
        long ret = 0;
        for (Client client : SessionManager.getUsers())
        {

            try
            {
                if (client.getClientHandler().userok == 1)
                {
                    if (client.getClientHandler().SF != null)
                    {
                        ret += Long.parseLong(client.getClientHandler().SF);
                    }
                }
            }
            catch (NumberFormatException ignored)
            {
            }
        }
        return ret;
    }

}
