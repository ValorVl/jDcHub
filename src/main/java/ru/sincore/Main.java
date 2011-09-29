package ru.sincore;
/*
 * Main.java
 *
 * Created on 03 martie 2007, 22:57
 *
 * DSHub AdcUtils HubSoft
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

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import ru.sincore.cmd.CmdContainer;
import ru.sincore.db.HibernateUtils;
import ru.sincore.i18n.Messages;

import java.util.Properties;

/**
 * DSHub main class, contains main function ( to call when start application )
 * Listens to System.in in tty for commands ( if run via java command line )
 *
 * @author Pietricica
 */


public class Main extends Thread
{
    public static final Logger log = Logger.getLogger(Main.class);

    public static HubServer server;
    public static Properties 	proppies;
    public static String        auxhelp;
    public static BanWordsList  listaBanate;
    public static String        MOTD = "";
    public static long          curtime;
    public static String        myPath;

    public static void init()
    {
		PropertyConfigurator.configure("./etc/log4j.properties");
		//ConfigLoader.init();
        ConfigurationManager.instance();
		HibernateUtils.getSessionFactory();
		CmdContainer container = CmdContainer.getInstance();
		container.buildList();
    }


    public static void Exit()
    {
        log.warn(Messages.get(Messages.CLOSE_HUB));

        try
        {
            sleep(500);
        }
        catch (InterruptedException ex)
        {
        }
        System.exit(0);
    }


    public void run()
    {
        log.warn(Messages.get(Messages.RESTART_HUB));

        ClientManager.getInstance().removeAllClients();

        server.shutdown();
        System.gc(); //calling garbage collectors
        Main.server = new HubServer();
        Main.curtime = System.currentTimeMillis();
        Main.proppies = System.getProperties();
    }


    public static void Restart()
    {
        new Main().start();

    }
    /**
     * @param args the command line arguments (Not used)
     */

    public static void main(String[] args)
    {
        init();
        curtime = System.currentTimeMillis();

        log.info(Messages.get(Messages.SERVER_STARTUP));

        server = new HubServer();

        proppies = System.getProperties();

        log.info(Messages.get(Messages.SERVER_STARTUP));
    }


}