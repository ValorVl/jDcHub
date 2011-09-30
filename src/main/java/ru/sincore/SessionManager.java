/*
 * SimpleHandler.java
 *
 * Created on 06 noiembrie 2007, 14:34
 *
 * DSHub ADC HubSoft
 * Copyright (C) 2007,2008  Eugen Hristev

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

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.Modules.Modulator;
import ru.sincore.Modules.Module;
import ru.sincore.TigerImpl.SIDGenerator;
import ru.sincore.util.STAError;

import java.util.Collection;
import java.util.StringTokenizer;

/**
 * @author Pietricica
 * @author Valor
 * @author Alexey 'lh' Antonov
 * @since 2011-09-06
 */
public class SessionManager extends IoHandlerAdapter
{
    public static final Logger log = LoggerFactory.getLogger(SessionManager.class);

    /**
     * Creates a new instance of SessionManager
     */
    public SessionManager()
    {
    }


    public void Disconnect(IoSession session)
    {
        session.close(false);
    }


    public void exceptionCaught(IoSession session, Throwable t)
            throws Exception
    {
        try
        {
            if (t instanceof java.io.IOException)
            {
                return;
            }
            if (t.getMessage().contains("java.nio.charset.MalformedInputException"))
            {
//                ((ClientHandler) (session.getAttribute("client")))
//                        .sendFromBot("Unicode Exception. Your client sent non-Unicode chars. Ignored.");
                return;
            }
            if ((t.getMessage().contains("BufferDataException: Line is too long")))
            {
                new STAError((Client) (session.getAttribute("client")), 100,
                             "Message exceeds buffer." + t.getMessage());
            }
            else
            {
                log.debug(t.toString());
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
        String rawMessage = (String) msg;
		log.debug("Incoming message : "+ rawMessage);

        try
        {
            Command.handle((Client) (session.getAttribute("client")), rawMessage);
        }
        catch (STAException stex)
        {
            if (stex.x < 200)
            {
                return;
            }
			log.debug(stex.toString());
        }
        catch (CommandException cfex)
        {
			log.debug(cfex.toString());
        }

    }

    public void messageSent(IoSession session, Object message)
            throws Exception
    {
        log.debug("Outgoing message from hub : \'" + message.toString() + "\'");
    }

    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception
    {
        //ok, we're in idle
    }


    public void sessionClosed(IoSession session)
            throws Exception
    {
        log.debug("Session closed.");
        Client currentClient = (Client)(session.getAttribute("client"));
        ClientManager.getInstance().removeClient(currentClient);

        if (currentClient.getClientHandler().isValidated() && !currentClient.getClientHandler().isKicked())
        {
            // broadcast client quited message
            Broadcast.getInstance().broadcast("IQUI " + currentClient.getClientHandler().getSID());
        }
        /** calling plugins...*/

        for (Module myMod : Modulator.myModules)
        {
            myMod.onClientQuit(currentClient.getClientHandler());
        }

        currentClient.getClientHandler().increaseTimeOnline(System.currentTimeMillis() -
                                                currentClient.getClientHandler().getLoggedAt());

        log.info(currentClient.getClientHandler().getNI() +
                 " with SID " +
                 currentClient.getClientHandler().getSID() +
                 " just quited.");
    }


    public void sessionOpened(IoSession session)
            throws Exception
    {
		Client newClient = new Client();

        session.setAttribute("client", newClient);

        newClient.getClientHandler().setSession(session);
        StringTokenizer ST = new StringTokenizer(newClient.getClientHandler().getSession().getRemoteAddress().toString(), "/:");

		newClient.getClientHandler().setRealIP(ST.nextToken());
        newClient.getClientHandler().setSID(SIDGenerator.generate());

        /**
         * Client will be moved from uninitialized to regular map after
         * handshacke will be done.
         * See {@link ru.sincore.adc.action.INF#parseIncoming()}
         */
        ClientManager.getInstance().addNewClient(newClient);
    }
}
