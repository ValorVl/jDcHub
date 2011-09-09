package ru.sincore;
/*
 * HubServer.java
 *
 * Created on 03 martie 2007, 23:00
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


import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import ru.sincore.Modules.Modulator;
import ru.sincore.adcs.CertManager;
import ru.sincore.adcs.SSLManager;
import ru.sincore.banning.bans;
import ru.sincore.util.ADC;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;


/**
 * Basic hub server listener and socket receiver, sends users to each's thread after connecting.
 * Handles the hub databases kept in files ( regs, config and bans).
 *
 * @author Pietricica
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-06
 */
public class HubServer extends Thread
{
    public static final Logger log = Logger.getLogger(HubServer.class);

    /**
     * Server main socket
     */
    // private ServerSocket MainSocket;

    public SSLManager sslmanager;

    InputStream  IS;
    OutputStream OS;
    int          port;

    ClientAssasin myAssasin;

    public boolean adcs_ok = false;

    //ClientHandler firstclient;

    private static RegConfig rcfg;
    private static bans      bcfg;

    public static boolean done_adcs = false;

    public static boolean restart;
    //public static IoServiceManager IOSM;
    // public static ServiceManager SM;

    //private SocketAcceptorConfig cfg;

    public IoAcceptor acceptor;
    // private ExecutorService x;//,y;
    // private InetSocketAddress address;
    public Calendar   MyCalendar;


    /**
     * Creates a new instance of HubServer
     */
    public HubServer()
    {
        start();
    }

    @Override
    public void run()
    {


        sslmanager = new SSLManager(new CertManager());
        SslFilter sslfilter = sslmanager.getSSLFilter();
        if (sslfilter != null)
        {
            adcs_ok = true;
        }
        /* if(adcs_ok)
             System.out.println("ADCS OK");
         else
             System.out.println("ADCS not OK");
        */
        try
        {
            File MotdFile = new File(Main.myPath + "motd");
            FileReader mr = new FileReader(MotdFile);
            BufferedReader mb = new BufferedReader(mr);
            Main.MOTD = "";
            while ((Main.auxhelp = mb.readLine()) != null)
            {
                Main.MOTD = Main.MOTD + Main.auxhelp + "\n";
            }
            mb.close();
            Main.MOTD = Main.MOTD.substring(0, Main.MOTD.length() - 1);
            ADC.MOTD = Main.MOTD;
        }
        catch (IOException e)
        {
            Main.MOTD = ADC.MOTD;

        }
        restart = false;

        // port=Vars.Default_Port;

        Modulator.findModules();
        try
        {
            this.sleep(500);
        }
        catch (Exception e)
        {
        }

        //   new Client();
        // ByteBuffer.setUseDirectBuffers(false);
        //   ByteBuffer.setAllocator(new PooledByteBufferAllocator());


        // x=Executors.newCachedThreadPool();
        //  y=Executors.newCachedThreadPool();

        acceptor = new NioSocketAcceptor();
        NioSocketAcceptor nsa = (NioSocketAcceptor) acceptor;
        nsa.setReuseAddress(true);


        //cfg = new SocketAcceptorConfig();
        // cfg.setThreadModel(ThreadModel.MANUAL);

        //cfg.getSessionConfig().setReceiveBufferSize(102400);
        // cfg.getSessionConfig().setSendBufferSize(102400);

        if (ConfigLoader.ENABLE_ADCS)
        {

            if (adcs_ok)
            {
                acceptor.getFilterChain()
                        .addLast("sslFilter", sslfilter);
            }
            else
            {
                System.out.println("Couldn't find suitable keys and certificate.\nPlease load them or regenerate.\n" +
                                "ADC Secure mode has been disabled.");
                ConfigLoader.ENABLE_ADCS = false;
            }

        }
        done_adcs = true;
        acceptor.getFilterChain().addLast("logger", new LoggingFilter());
        TextLineCodecFactory myx = new TextLineCodecFactory(Charset.forName("UTF-8"), "\n", "\n");
        myx.setDecoderMaxLineLength(64 * 1024);
        myx.setEncoderMaxLineLength(64 * 1024);
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(myx));
        MyCalendar = Calendar.getInstance();
        // DefaultIoFilterChainBuilder filterChainBuilder = cfg.getFilterChain();
        //  filterChainBuilder.addLast("threadPool", new ExecutorFilter(y));
        //cfg.getSessionConfig().setKeepAlive(true);

        //acceptor.getSessionConfig().setReadBufferSize(64 * 1024);
        //acceptor.getSessionConfig().WriteBufferSize( 2048000 );

        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 50);
        acceptor.setHandler(new SessionManager());
//System.out.println(acceptor.getSessionConfig().getWriteTimeout());
        // acceptor.setCloseOnDeactivation(true);
        //cfg.getSessionConfig().
        //System.out.println(cfg.getSessionConfig().getReceiveBufferSize());
        // IOSM=new IoServiceManager(acceptor);
        //SM=new ServiceManager(acceptor);
        //  IOSM.startCollectingStats(10000);
        // address=new InetSocketAddress(port);

		try
		{

			acceptor.bind(new InetSocketAddress(ConfigLoader.HUB_PORT));

		} catch (IOException e)
		{
			log.error(e);
			shutdown();
		}


        Date d = new Date(Main.curtime);
        log.info("Start Time:" + d.toString());
        System.out.print("\n>");

        myAssasin = new ClientAssasin();//temporary removed
        //  ClientExecutor myExecutor=new ClientExecutor();


    }








    public void shutdown()
    {

        acceptor.unbind();
        // x.shutdown();
    }


    // TODO Realize client add method
    public static Client AddClient()
    {
        if (restart)
        {
            return null;
        }

        Client newclient = new Client();

        synchronized (SessionManager.Users)
        {
            SessionManager.Users.put(newclient.getClientHandler().ID, newclient);
        }

        return newclient;
    }
}
