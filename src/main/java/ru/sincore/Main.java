package ru.sincore;
/*
 * Main.java
 *
 * Created on 03 martie 2007, 22:57
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
import org.apache.log4j.PropertyConfigurator;
import ru.sincore.banning.BanList;
import ru.sincore.cmd.CmdContainer;
import ru.sincore.db.HibernateUtils;
import ru.sincore.i18n.Messages;
import ru.sincore.python.PythonManager;

import java.util.Properties;
import java.util.StringTokenizer;

/**
 * DSHub main class, contains main function ( to call when start application )
 * Listens to System.in in tty for commands ( if run via java command line )
 *
 * @author Pietricica
 */


public class Main extends Thread
{
    public static final Logger log = Logger.getLogger(Main.class);

    public static HubServer     Server;
    public static Properties    Proppies;
    public static String        auxhelp;
    public static BanWordsList  listaBanate;
    public static String        MOTD = "";
    public static long          curtime;
    public static String        myPath;
    public static PythonManager pManager;

    public static void init()
    {
		PropertyConfigurator.configure("./etc/log4j.properties");
		ConfigLoader.init();
		Messages.loadClientMessages();
		Messages.loadServerMessages();
		HibernateUtils.getSessionFactory();
		CmdContainer container = CmdContainer.getInstance();
		container.buildList();


		ClassLoader cl = ClassLoader.getSystemClassLoader();
        String javaClassPath = System.getProperty("java.class.path");
        String userDirectory = System.getProperty("user.dir");

        String separator = System.getProperty("file.separator");
        String pathSeparator = System.getProperty("path.separator");

        javaClassPath = javaClassPath.replace('\\', separator.charAt(0));
        javaClassPath = javaClassPath.replace('/', separator.charAt(0));
        if (!userDirectory.endsWith(separator))
        {
            userDirectory = userDirectory + separator;
        }

        myPath = userDirectory + javaClassPath;
        //log.debug(myPath);

        int x = myPath.lastIndexOf(separator.charAt(0));
        if (x != -1)
        {
            myPath = myPath.substring(0, x + 1);
        }


        if (javaClassPath.matches("[a-zA-Z]:\\\\.*"))
        {
            int y = javaClassPath.indexOf(';');
            while (y != -1)
            {
                javaClassPath = javaClassPath.substring(y + 1, javaClassPath.length());
                y = javaClassPath.indexOf(';');
            }

            myPath = javaClassPath;
            if (myPath.endsWith(".jar") || myPath.endsWith(".jar" + separator))
            {
                myPath = myPath.substring(0, myPath.lastIndexOf(separator));
            }
            if (!myPath.endsWith(separator))
            {
                myPath = myPath + separator;
            }

            log.debug(myPath);
            if (myPath.equals("\\"))
            {
                myPath = "";
            }
        }
        if (System.getProperty("os.name").equalsIgnoreCase("Linux"))
        {
            if (!javaClassPath.startsWith(separator))
            {
                StringTokenizer st1 = new StringTokenizer(myPath, pathSeparator);
                String aux = st1.nextToken();
                log.debug(myPath);
                while (!(aux.toLowerCase().contains("dshub.jar".toLowerCase())) &&
                       st1.hasMoreTokens())
                {
                    aux = st1.nextToken();
                }
                // if(!st1.hasMoreTokens())

                //  Main.PopMsg("FAIL. Java Classpath Error.");
                //   return;

                myPath = aux;
            }
            else
            {
                if (javaClassPath.toLowerCase().endsWith("/dshub.jar"))
                {
                    myPath = javaClassPath.substring(0, javaClassPath.length() - 9);
                }
                else
                {
                    myPath = javaClassPath;
                }
            }

        }

        if (System.getProperty("os.name").equalsIgnoreCase("sunos"))
        {
            if (javaClassPath.startsWith(separator))
            {
                if (javaClassPath.toLowerCase().endsWith("/dshub.jar"))
                {
                    myPath = javaClassPath.substring(0, javaClassPath.length() - 9);
                }
                else
                {
                    myPath = javaClassPath;
                }
            }
        }
        //log.debug(myPath);

        pManager = new PythonManager();


    }


    public static void Exit()
    {

        log.warn(Messages.CLOSE_HUB);

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
        log.warn(Messages.RESTART_HUB);

        BanList.First = null;
        ClientManager.getInstance().removeAllClients();

        Server.shutdown();
        System.gc(); //calling garbage collectors
        Main.Server = new HubServer();
        Main.curtime = System.currentTimeMillis();
        Main.Proppies = System.getProperties();
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

		log.info(Messages.SERVER_STARTUP);

        Server = new HubServer();

        Proppies = System.getProperties();

        log.info(Messages.SERVER_STARTUP_DONE);
    }


}