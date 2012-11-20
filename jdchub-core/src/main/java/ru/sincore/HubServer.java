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
import com.adamtaft.eb.EventHandler;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.adc.Features;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.action.actions.SUP;
import ru.sincore.cmd.CommandEngine;
import ru.sincore.cmd.handlers.*;
import ru.sincore.events.AdcExtNoMoreSupported;
import ru.sincore.events.AdcExtSupported;
import ru.sincore.events.HubShutdown;
import ru.sincore.events.HubStartup;
import ru.sincore.modules.ModulesManager;
import ru.sincore.pipeline.PipelineFactory;
import ru.sincore.script.ScriptEngine;
import ru.sincore.util.Constants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Date;


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
    private static final Logger         log    = LoggerFactory.getLogger(HubServer.class);
    private final        String         marker = Marker.ANY_MARKER;

    private              SUP            sup;
    private              ClientAssasin  assasin = null;
    private              IoAcceptor     acceptor = null;
    private              CommandEngine  commandEngine = null;
    private              ScriptEngine   scriptEngine = null;


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
        initHubFeatureList();

        // initialize command engine
        initCommandEngine();

        // initialize (or reinitialize) pipeline factory (fileter message for forbidden words)
        PipelineFactory.initialize();

        // initialize module manager and modules
        ModulesManager.instance().loadModules();

        initScriptEngine();
        scriptEngine.start();

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

        acceptor.getFilterChain().addLast(Constants.LOGGER_FILTER, new LoggingFilter());
        TextLineCodecFactory myx = new TextLineCodecFactory(Charset.forName("UTF-8"), "\n", "\n");
        myx.setDecoderMaxLineLength(64 * 1024);
        myx.setEncoderMaxLineLength(64 * 1024);
        acceptor.getFilterChain().addLast(Constants.CODEC_FILTER, new ProtocolCodecFilter(myx));

        // TODO Uncomment this when it will be added to config file
        //acceptor.getSessionConfig().setReadBufferSize(64 * 1024);
        //acceptor.getSessionConfig().WriteBufferSize( 2048000 );
        //acceptor.setCloseOnDeactivation(true);

        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 50);
        acceptor.setHandler(new SessionManager());

        try
        {
            int hubPort = ConfigurationManager.getInstance().getInt(ConfigurationManager.HUB_PORT);
            acceptor.bind(new InetSocketAddress(hubPort));
            log.info("Hub successfully binded on port : " + hubPort);
        }
        catch (IOException e)
        {
            log.error(marker, e);
            shutdown();
        }


        Date d = new Date(Main.getStartTime());
        log.info("Start Time: " + d.toString());
        System.out.print("\n>");

        // Publish startup event
        EventBusService.publish(new HubStartup());

        assasin = new ClientAssasin();
    }


    private void initHubFeatureList()
    {
        log.info("Initializing HubFeatureList...");
        sup = new SUP();
        try
        {
            sup.setMessageType(MessageType.I);
            sup.getFeatures().put(Features.BASE,  true);
            sup.getFeatures().put(Features.BAS0,  true);
            sup.getFeatures().put(Features.TIGER, true);
            sup.getFeatures().put(Features.UCM0,  true);
            sup.getFeatures().put(Features.ADC0,  true);
            // Extended
            sup.getFeatures().put(Features.PING,  true);
            sup.getFeatures().put(Features.SEGA,  true);
            sup.getFeatures().put(Features.RXTX,  true);
            sup.getFeatures().put(Features.NATT,  true);
            // Test feature
            sup.getFeatures().put(Features.ZLIF,  true);
        }
        catch (Exception e)
        {
            log.error("Can\'t init hub SUP field.\n" + e.toString());
        }

        EventBusService.subscribe(this);
    }


    private void initScriptEngine()
    {
        log.info("Initializing ScriptEngine...");

        if (scriptEngine != null)
        {
            scriptEngine.stopEngines();
        }

        scriptEngine = new ScriptEngine();
        scriptEngine.initialize(commandEngine);
    }


    /**
     * Initialize actionName engine with default handlers
     */
    private void initCommandEngine()
    {
        log.info("Initializing CommandEngine...");

        if (commandEngine != null)
        {
            // remove all commands (it must be done, if server was restarted)
            commandEngine.removeAllCommands();
        }

        commandEngine = new CommandEngine();

        commandEngine.registerCommand("about",      new AboutCommand());
        commandEngine.registerCommand("help",       new HelpCommand());
        commandEngine.registerCommand("module",     new ModuleCommand());
        commandEngine.registerCommand("reload",     new ReloadCommand());
        commandEngine.registerCommand("restart",    new RestartCommand());
        commandEngine.registerCommand("shutdown",   new ShutdownCommand());
    }


    public void shutdown()
    {
        assasin.stopClientAssasin();

        // TODO: [hatred] send server shutdown message before
        //TODO realize correctly shutdown server,notify clients, store all collection containers, drop buffers and caches.
        acceptor.unbind();

        EventBusService.publish(new HubShutdown());
    }


    public SUP getSup()
    {
        return sup;
    }


    public CommandEngine getCommandEngine()
    {
        return commandEngine;
    }


    public ScriptEngine getScriptEngine()
    {
        return scriptEngine;
    }
    
    
    @EventHandler
    public void handleAdcExtSupportedEvent(AdcExtSupported event)
    {
        if (sup == null)
        {
            return;
        }

        try
        {
            sup.getFeatures().put(event.getExtensionName(), true);
        }
        catch (Exception e)
        {
            log.error(e.toString());
        }

        log.info("ADC extension ADDED : " + event.getExtensionName());
    }


    @EventHandler
    public void handleAdcExtNoMoreSupportedEvent(AdcExtNoMoreSupported event)
    {
        if (sup == null)
        {
            return;
        }

        try
        {
            sup.getFeatures().remove(event.getExtensionName());
        }
        catch (Exception e)
        {
            log.error(e.toString());
        }

        //TODO [lh] Add code with extension remove SUP (BSUP HUB_SID RMFEED)


        log.info("ADC extension REMOVED : " + event.getExtensionName());
    }
}
