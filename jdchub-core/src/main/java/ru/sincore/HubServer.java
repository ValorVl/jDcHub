package ru.sincore;
/*
 * HubServer.java
 *
 * Created on 03 martie 2007, 23:00
 *
 * jDcHub
 * Copyright (C) 2007,2008  Eugen Hristev
 * Copyright (C) 2011 Valor, Alexey 'lh' Antonov, Alexander 'hatred' Drozdov
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


import com.adamtaft.eb.EventBusService;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.cmd.CommandEngine;
import ru.sincore.cmd.handlers.*;
import ru.sincore.events.HubShutdownEvent;
import ru.sincore.events.HubStartupEvent;
import ru.sincore.modules.ModulesManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Date;
import javax.net.ssl.SSLContext;


/**
 * Basic hub server listener and socket receiver, sends users to each's thread after connecting.
 * Handles the hub databases kept in files ( regs, config and bans).
 *
 * @author Pietricica
 *
 * @author Alexey 'lh' Antonov
 * @author Alexander 'hatred' Drozdov
 * @since 2011-09-06
 */
public class HubServer
{
    private static final Logger        log    = LoggerFactory.getLogger(HubServer.class);
    private final        String        marker = Marker.ANY_MARKER;

    private              ClientAssasin assasin;
    private              IoAcceptor    acceptor;
    private              CommandEngine commandEngine;
    /**
     * Creates a new instance of HubServer
     */
    public HubServer()
    {
        init();
    }


    /**
     * Initialize hub server
     */
    private void init()
    {
        initCommandEngine();

        ModulesManager.instance().loadModules();

        try
        {
            Thread.sleep(500);
        }
        catch (Exception e)
        {
            // ignored
        }

        NioSocketAcceptor nsa = new NioSocketAcceptor();
        acceptor = nsa;
        nsa.setReuseAddress(true);

        acceptor.getFilterChain().addLast("logger", new LoggingFilter());
        TextLineCodecFactory myx = new TextLineCodecFactory(Charset.forName("UTF-8"), "\n", "\n");
        myx.setDecoderMaxLineLength(64 * 1024);
        myx.setEncoderMaxLineLength(64 * 1024);
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(myx));

        // TODO Uncomment this when it will be added to config file
        //acceptor.getSessionConfig().setReadBufferSize(64 * 1024);
        //acceptor.getSessionConfig().WriteBufferSize( 2048000 );
        //acceptor.setCloseOnDeactivation(true);

        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 50);
        acceptor.setHandler(new SessionManager());

		try
		{
            int hubPort = ConfigurationManager.instance().getInt(ConfigurationManager.HUB_PORT);
			acceptor.bind(new InetSocketAddress(hubPort));
            log.info("Hub successfully binded on port : " + hubPort);
		}
        catch (IOException e)
		{
			log.error(marker,e);
			shutdown();
		}


        Date d = new Date(Main.getStartTime());
        log.info("Start Time: " + d.toString());
        System.out.print("\n>");

        // Publish startup event
        EventBusService.publish(new HubStartupEvent());

        assasin = new ClientAssasin();//temporary removed
    }


    /**
     * Initialize actionName engine with default handlers
     */
    private void initCommandEngine()
    {
        log.debug("Initializing CommandEngine...");

        if (commandEngine != null)
        {
            // remove all commands (it must be done, if server was restarted)
            commandEngine.removeAllCommands();
        }

        commandEngine = new CommandEngine();

        // register default handlers
        commandEngine.registerCommand("about",      new AboutCommand());
        commandEngine.registerCommand("help",       new HelpCommand());
        commandEngine.registerCommand("regme",      new RegmeCommand());
        commandEngine.registerCommand("info",       new InfoCommand());
        commandEngine.registerCommand("kick",       new KickCommand());
        commandEngine.registerCommand("grant",      new GrantCommand());
        commandEngine.registerCommand("reload",     new ReloadCommand());
        commandEngine.registerCommand("restart",    new RestartCommand());
        commandEngine.registerCommand("shutdown",   new ShutdownCommand());
        commandEngine.registerCommand("broadcast",  new BroadcastCommand());
        commandEngine.registerCommand("changepass", new ChangePassCommand());
        commandEngine.registerCommand("topic",      new TopicCommand());
    }


    public void shutdown()
    {
        assasin.stopClientAssasin();

        // TODO: [hatred] send server shutdown message before
        //TODO realize correctly shutdown server,notify clients, store all collection containers, drop buffers and caches.
        acceptor.unbind();

        EventBusService.publish(new HubShutdownEvent());
    }
}
