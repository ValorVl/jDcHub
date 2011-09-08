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
import ru.sincore.TigerImpl.Base32;
import ru.sincore.banning.BanList;
import ru.sincore.cmd.GrantCmd;
import ru.sincore.cmd.PortCmd;
import ru.sincore.conf.Vars;
import ru.sincore.python.*;
import ru.sincore.util.ADC;
import ru.sincore.util.HostTester;
import ru.sincore.util.TimeConv;
import ru.sincore.i18n.Messages;

import java.io.*;
import java.util.*;
import java.net.*;

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
        log.debug(myPath);

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
        log.debug(myPath);

        pManager = new PythonManager();


    }


    public static void Exit()
    {
        Server.rewriteregs();
        Server.rewriteconfig();
        Server.rewritebans();

        //save Banned Words List
        listaBanate.printFile(Main.myPath + "banwlist.txt");

        log.warn(Messages.CLOSE_HUB);

        try
        {
            sleep(500);
        }
        catch (InterruptedException ex)
        {
            // ex.printStackTrace();
        }
        System.exit(0);
    }


    public void run()
    {
        log.warn(Messages.RESTART_HUB);
        Main.Server.rewriteregs();
        Main.Server.rewriteconfig();
        Main.Server.rewritebans();
        Main.Server.restart = true;

        BanList.First = null;
        SessionManager.Users.clear();

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


    public static void Reg(String aux)
    {
        if (aux.length() == 39) //possible CID, lets try
        {
            try
            {
                Base32.decode(aux);
                if (AccountsConfig.isReg(aux) > 0)
                {

                    log.info(AccountsConfig.getnod(aux).getRegInfo());
                    return;
                }

                for (Client client : SessionManager.getUsers())
                {
                    ClientHandler clientHandler = client.getClientHandler();

                    if (clientHandler.userok == 1)
                    {
                        if ((clientHandler.ID.equals(aux)))
                        {
                            //247
                            //log.info(Translation.getUserRegged(clientHandler.NI, aux));

                            AccountsConfig.addReg(clientHandler.ID, clientHandler.NI, "Server");
                            clientHandler.reg = AccountsConfig.getnod(clientHandler.ID);
                            clientHandler.can_receive_cmds = true;
                            clientHandler.sendFromBot(Messages.ACCOUNT_REGISTER);
                            clientHandler.putOpchat(true);
                            if (clientHandler.reg.key)
                            {
                                clientHandler.CT = "4";
                            }
                            else
                            {
                                clientHandler.CT = "2";
                            }


                            Broadcast.getInstance()
                                     .broadcast("BINF " +
                                                clientHandler.SessionID +
                                                " " +
                                                (clientHandler.reg.key ? "CT4" : "CT2"));

                            clientHandler.reg.isreg = true;
                            clientHandler.reg.LastIP = clientHandler.RealIP;
                            clientHandler.LoggedAt = System.currentTimeMillis();


                            Main.Server.rewriteregs();
                            return;
                        }
                    }
                }

                AccountsConfig.addReg(aux, null, "Server");
                Nod x = AccountsConfig.getnod(aux);
                x.isreg = true;
                log.info(Messages.REGISTER_CID);

            }
            catch (IllegalArgumentException iae)
            {
                //handler.sendFromBot("Not a CID, trying to add the "+aux+" nick.");
                for (Client client : SessionManager.getUsers())
                {
                    ClientHandler clientHandler = client.getClientHandler();

                    if (clientHandler.userok == 1)
                    {
                        if ((clientHandler.NI.toLowerCase().equals(aux.toLowerCase())))
                        {
                            if (AccountsConfig.isReg(clientHandler.ID) > 0)
                            {
                                System.out
                                        .println(AccountsConfig.getnod(clientHandler.ID)
                                                               .getRegInfo());

                                return;
                            }
                            AccountsConfig.addReg(clientHandler.ID, clientHandler.NI, "Server");
                            clientHandler.reg = AccountsConfig.getnod(clientHandler.ID);
                            clientHandler.can_receive_cmds = true;
                            clientHandler.sendFromBot(String.format(Messages.SERVER_MESSAGE_STUB,"Class Main : 303"));
                            clientHandler.putOpchat(true);
                            if (clientHandler.reg.key)
                            {
                                clientHandler.CT = "4";
                            }
                            else
                            {
                                clientHandler.CT = "2";
                            }


                            Broadcast.getInstance()
                                     .broadcast("BINF " +
                                                clientHandler.SessionID +
                                                " " +
                                                (clientHandler.reg.key ? "CT4" : "CT2"));

                            clientHandler.reg.isreg = true;
                            clientHandler.reg.LastIP = clientHandler.RealIP;
                            clientHandler.LoggedAt = System.currentTimeMillis();
                            log.info(Translation.getNotCid(aux) +
											 "\n" +
											 Translation.getUserRegged(clientHandler.NI,
																	   clientHandler.ID));

                            Main.Server.rewriteregs();
                            return;
                        }
                    }
                }

                log.info(Translation.getNotCid(aux) + "\n" + Translation.getString("no_user"));
            }
            catch (Exception e)
            {
                return;
            }
        }
        else
        {
            //handler.sendFromBot("Not a CID, trying to add the "+aux+" nick.");
            for (Client client : SessionManager.getUsers())
            {
                ClientHandler clientHandler = client.getClientHandler();

                if (clientHandler.userok == 1)
                {
                    if ((clientHandler.NI.toLowerCase().equals(aux.toLowerCase())))
                    {
                        if (AccountsConfig.isReg(clientHandler.ID) > 0)
                        {
                            System.out
                                    .println(AccountsConfig.getnod(clientHandler.ID)
                                                           .getRegInfo());

                            return;
                        }
                        AccountsConfig.addReg(clientHandler.ID, clientHandler.NI, "Server");
                        clientHandler.reg = AccountsConfig.getnod(clientHandler.ID);
                        clientHandler.can_receive_cmds = true;
                        clientHandler.sendFromBot(Translation.getString("reg_msg"));

                        clientHandler.putOpchat(true);
                        if (clientHandler.reg.key)
                        {
                            clientHandler.CT = "4";
                        }
                        else
                        {
                            clientHandler.CT = "2";
                        }


                        Broadcast.getInstance()
                                 .broadcast("BINF " +
                                            clientHandler.SessionID +
                                            " " +
                                            (clientHandler.reg.key ? "CT4" : "CT2"));

                        clientHandler.LoggedAt = System.currentTimeMillis();
                        clientHandler.reg.isreg = true;
                        clientHandler.reg.LastIP = clientHandler.RealIP;
                        log.info(Translation.getNotCid(aux) +
										 "\n" +
										 Translation.getUserRegged(clientHandler.NI, clientHandler.ID));

                        Main.Server.rewriteregs();
                        return;
                    }
                }
            }

            log.info(Translation.getNotCid(aux) + "\n" + Translation.getString("no_user"));
        }

        Main.Server.rewriteregs();
        //sendFromBot("Your password is now "+aux+".");
    }


    /**
     * @param args the command line arguments
     */

    public static void main(String[] args)
    {
        //

        curtime = System.currentTimeMillis();
        init();
        System.out.println(Translation.getString("startup"));

        //init banned words list
        Main.listaBanate = new BanWordsList();
        Main.listaBanate.loadFile(Main.myPath + "banwlist.txt");

        //System.out.println(Modulator.findModule("dshub.Main.class"));    

        /*     Tiger myTiger = new Tiger();
						
		              myTiger.engineReset();
		                myTiger.init();	
                               String result1="aabbcc";
                               byte [] b1=result1.getBytes();
                                
		         myTiger.engineUpdate(b1,0,b1.length);
				
		            byte[] final1 = myTiger.engineDigest();
		        System.out.println("all in one "+Base32.encode (final1));
                        
                        Tiger myTiger2=new Tiger();
                        myTiger2.engineReset();
                        myTiger2.init();
                        String resultx="aa";
                        String resulty="bbcc";
                        byte [] bx=resultx.getBytes();
                        myTiger.engineUpdate(bx,0,bx.length);
                        
                        byte [] blaa=myTiger.engineDigest();
                       
                        //myTiger.engineUpdate(blaa,0,blaa.length);
                        byte [] by=resulty.getBytes();
                        myTiger.engineUpdate(by,0,by.length);
                        byte[] final2 = myTiger.engineDigest();
		        System.out.println("separately "+Base32.encode (final2));
                        */
        /* try{
            long x;
        System.out.println(x=TimeConv.getLongTime("#5h"));
        System.out.println(TimeConv.getStrTime(x));
        
        ;}catch(Exception e){System.out.println(e);}
        */

        Server = new HubServer();


        log.info(Translation.getString("gpl1") + "\r\n" +
						 Translation.getString("gpl2") + "\r\n" +
						 Translation.getString("gpl3") + "\r\n" +
						 Translation.getString("gpl4"));

        Proppies = System.getProperties();

        log.info(Translation.getString("done"));
        System.out.println(Translation.getString("command_mode"));


        InputStreamReader b = new InputStreamReader(System.in);
        BufferedReader bla = new BufferedReader(b);


        try
        {

            while (true)
            {

                String recvbuf = bla.readLine();
                //System.out.println(recvbuf);
                //System.out.println(bla.readLine());
                // System.out.println(recvbuf);
                if (recvbuf == null)
                {
                    try
                    {
                        sleep(500);
                    }
                    catch (InterruptedException ex)
                    {
                    }
                    continue;
                }
                if (recvbuf.toLowerCase().equals("quit"))
                {
                    Exit();
                }

                if (recvbuf.toLowerCase().equals("help"))
                {
                    Nod ServerNode = new Nod();
                    ServerNode.myMask = new CommandMask(1);

                    System.out.printf(

                            ServerNode.myHelp.getHelp() + "\n",
                            Proppies.getProperty("os.name"),
                            Proppies.getProperty("os.version"),
                            Proppies.getProperty("os.arch"));

                }
                else if (recvbuf.toLowerCase().equals("restart"))
                {
                    Restart();
                }
                else if (recvbuf.toLowerCase().startsWith("unban "))
                {
                    StringTokenizer ST = new StringTokenizer(recvbuf);
                    ST.nextToken();
                    String aux = ST.nextToken(); //the thing to unban;
                    //al right,now must check if that is a nick, cid or ip
                    //first if its a cid...
                    try
                    {
                        Base32.decode(aux);
                        //ok if we got here it really is a CID so:
                        if (aux.length() != 39)
                        {
                            throw new IllegalArgumentException();
                        }
                        if (BanList.delban(3, aux))
                        {
                            System.out
                                    .println(Translation.getString("searching") +
                                             "\n" +
                                             Translation.getString("cid_unbanned"));


                        }
                        else
                        {
                            System.out
                                    .println(Translation.getString("searching") +
                                             "\n" +
                                             Translation.getString("cid_not_banned"));
                        }
                    }
                    catch (IllegalArgumentException iae)
                    {
                        //ok its not a cid, lets check if its some IP address...
                        System.out.println(Translation.getString("not_cid_searching"));
                        if (ADC.isIP(aux))
                        {
                            System.out.println(Translation.getString("is_ip_checking"));
                            if (BanList.delban(2, aux))
                            {
                                System.out.println(Translation.getFoundIPUnbanned(aux));
                            }
                            else
                            {
                                System.out.println(Translation.getFoundIPNoBan(aux));
                            }
                        }
                        else
                        {
                            System.out.println(Translation.getString("not_ip"));
                            if (BanList.delban(1, aux))
                            {
                                System.out.println(Translation.getFoundNickUnbanned(aux));
                            }
                            else
                            {
                                System.out.println(Translation.getFoundNickNoBan(aux));
                            }
                        }
                    }
                    System.out.println(Translation.getString("done"));
                    Main.Server.rewritebans();


                }
                else if (recvbuf.toLowerCase().equals("listreg"))
                {
                    String blah00 = Translation.getString("reg_list") + " \n";
                    Nod n = AccountsConfig.First;
                    while (n != null)
                    {
                        blah00 = blah00 + n.CID;
                        if (n.LastNI != null)
                        {
                            blah00 = blah00 + Translation.getLastNick(n.LastNI) + "\n";
                        }
                        else
                        {
                            blah00 = blah00 + Translation.getString("never_seen") + "\n";
                        }
                        n = n.Next;
                    }
                    blah00 = blah00.substring(0, blah00.length() - 1);
                    System.out.println(blah00);
                }
                else if (recvbuf.toLowerCase().startsWith("ureg "))
                {
                    StringTokenizer ST = new StringTokenizer(recvbuf);
                    ST.nextToken();
                    if (!ST.hasMoreTokens())
                    {
                        continue;
                    }
                    String aux = ST.nextToken();
                    try
                    {
                        if (aux.length() != 39)
                        {
                            throw new IllegalArgumentException();
                        }
                        Base32.decode(aux);
                        if (AccountsConfig.unreg(aux))
                        {
                            int found = 0;
                            for (Client temp : SessionManager.getUsers())
                            {
                                if (temp.handler.userok == 1)
                                {
                                    if ((temp.handler.ID.equals(aux)))
                                    {
                                        temp.handler
                                                .sendFromBot(Translation.getString("account_deleted"));
                                        temp.handler.putOpchat(false);
                                        temp.handler.CT = "0";

                                        Broadcast.getInstance()
                                                 .broadcast("BINF " +
                                                            temp.handler.SessionID +
                                                            " CT");
                                        temp.handler.reg = new Nod();
                                        System.out
                                                .println(Translation.getUserDeleted(temp.handler.NI,
                                                                                    aux));
                                        temp.handler.can_receive_cmds = false;
                                        Main.Server.rewriteregs();
                                        found = 1;
                                    }
                                }
                            }
                            if (found == 0)
                            {
                                System.out.println(Translation.getString("reg_deleted"));
                            }
                        }
                        else
                        {
                            System.out.println();
                        }
                    }
                    catch (IllegalArgumentException iae)
                    {
                        System.out.println(Translation.getString("not_cid_check"));
                        int found = 0;
                        for (Client temp : SessionManager.getUsers())
                        {

                            if (temp.handler.userok == 1)
                            {
                                if ((temp.handler.NI.toLowerCase().equals(aux.toLowerCase())))
                                {
                                    AccountsConfig.unreg(temp.handler.ID);
                                    System.out
                                            .println(Translation.getUserDeleted(temp.handler.NI,
                                                                                temp.handler.ID));
                                    temp.handler
                                            .sendFromBot(Translation.getString("account_deleted"));
                                    temp.handler.putOpchat(false);
                                    temp.handler.CT = "0";
                                    temp.handler.can_receive_cmds = false;
                                    Broadcast.getInstance()
                                             .broadcast("BINF " +
                                                        temp.handler.SessionID +
                                                        " CT");
                                    temp.handler.reg = new Nod();
                                }
                            }
                            Main.Server.rewriteregs();
                            found = 1;
                        }
                        if (found == 0)
                        {
                            System.out.println(Translation.getString("no_user"));
                        }


                    }
                    Main.Server.rewriteregs();

                }
                else if (recvbuf.toLowerCase().startsWith("reg "))
                {
                    StringTokenizer ST = new StringTokenizer(recvbuf);
                    ST.nextToken();
                    if (!ST.hasMoreTokens())
                    {
                        return;
                    }
                    String aux = ST.nextToken();
                    Reg(aux);


                }
                else if (recvbuf.toLowerCase().startsWith("grant"))
                {
                    new GrantCmd(recvbuf);


                }
                else if (recvbuf.toLowerCase().startsWith("cfg"))
                {
                    if (recvbuf.equals("cfg"))
                    {
                        System.out.println("Usage: cfg <varname> <newval>. cfg list to see all.");
                        //break;
                    }
                    else
                    {
                        StringTokenizer ST = new StringTokenizer(recvbuf);
                        ST.nextToken();
                        String aux = ST.nextToken();
                        if (aux.toLowerCase().equals("timeout_login"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                int aucsy = Vars.Timeout_Login;
                                Vars.Timeout_Login = Integer.parseInt(aux);
                                System.out
                                        .printf(Translation.getCfgChanged("Timeout_Login",
                                                                          Integer.toString(aucsy),
                                                                          aux) + "\n");
                                Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("hub_name"))
                        {
                            //  String ucsy=Vars.HubName;
                            String new_name = ST.nextToken();
                            while (ST.hasMoreTokens())
                            {
                                new_name = new_name + " " + ST.nextToken();
                            }

                            System.out.printf(Translation.getCfgChanged("Hub_name",
                                                                        Vars.HubName, new_name) +
                                              "\n");

                            Vars.HubName = new_name;
                            Server.rewriteconfig();
                            Broadcast.getInstance().broadcast("IINF NI" + Vars.HubName);

                        }

                        else if (aux.toLowerCase().equals("hub_host"))
                        {

                            String new_name = ST.nextToken();

                            int x = new_name.indexOf(':');
                            if (x == -1 || x > new_name.length() - 1)
                            {
                                System.out.println(Translation.getString("invalid_host"));
                                continue;
                            }

                            System.out.println(Translation.getString("scanning_host"));
                            if (!(HostTester.hostOK(new_name)))
                            {
                                System.out.printf(Translation.getString("bad_host") + "\n");
                                continue;
                            }
                            System.out.printf(Translation.getCfgChanged("Hub_host",
                                                                        Vars.Hub_Host, new_name) +
                                              "\n");


                            Vars.Hub_Host = new_name;
                            Server.rewriteconfig();


                        }
                        else if (aux.toLowerCase().equals("proxy_host"))
                        {

                            if (Vars.Proxy_Port == 0)
                            {
                                System.out.printf(Translation.getString("set_port") + "\n");
                                continue;
                            }
                            String new_name = ST.nextToken();
                            try
                            {
                                Proxy x = new Proxy(Proxy.Type.HTTP,
                                                    new InetSocketAddress(new_name,
                                                                          Vars.Proxy_Port));
                            }
                            catch (Exception e)
                            {
                                System.out.println(Translation.getString("invalid_proxy"));
                            }
                            System.out.printf(Translation.getCfgChanged("Proxy_Host",
                                                                        Vars.Proxy_Host, new_name) +
                                              "\n");

                            Vars.Proxy_Host = new_name;
                            Server.rewriteconfig();


                        }
                        else if (aux.toLowerCase().equals("proxy_port"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                long aucsy = Vars.Proxy_Port;
                                int x = Integer.parseInt(aux);
                                if (x < 1 || x > 65355)
                                {
                                    throw new NumberFormatException();
                                }
                                Vars.Proxy_Port = x;
                                System.out.printf(Translation.getCfgChanged("Proxy_port",
                                                                            Long.toString(aucsy),
                                                                            aux) + "\n");
                                Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("redirect_url"))
                        {

                            if (!ST.hasMoreTokens())
                            {

                                System.out.printf(Translation.getString("redirect_deleted"));

                                Vars.redirect_url = "";
                                Server.rewriteconfig();
                                continue;
                            }
                            String new_name = ST.nextToken();

                            System.out.printf(Translation.getCfgChanged("Redirect_url",
                                                                        Vars.redirect_url,
                                                                        new_name) + "\n");

                            Vars.redirect_url = new_name;
                            Server.rewriteconfig();


                        }
                        else if (aux.toLowerCase().equals("max_ni"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                int aucsy = Vars.max_ni;
                                Vars.max_ni = Integer.parseInt(aux);
                                System.out.printf(Translation.getCfgChanged("Max_NI",
                                                                            Integer.toString(aucsy),
                                                                            aux) + "\n");
                                Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("min_ni"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                int aucsy = Vars.min_ni;
                                Vars.min_ni = Integer.parseInt(aux);
                                System.out.printf(Translation.getCfgChanged("Min_NI",
                                                                            Integer.toString(aucsy),
                                                                            aux) + "\n");
                                Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("max_de"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                int aucsy = Vars.max_de;
                                Vars.max_de = Integer.parseInt(aux);
                                System.out.printf(Translation.getCfgChanged("Max_DE",
                                                                            Integer.toString(aucsy),
                                                                            aux) + "\n");
                                Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("max_share"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                long aucsy = Vars.max_share;
                                Vars.max_share = Long.parseLong(aux);
                                System.out.printf(Translation.getCfgChanged("Max_share",
                                                                            (Long.toString(aucsy)),
                                                                            aux) + "\n");
                                Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("min_share"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                long aucsy = Vars.min_share;
                                Vars.min_share = Long.parseLong(aux);
                                System.out.printf(Translation.getCfgChanged("Min_share",
                                                                            (Long.toString(aucsy)),
                                                                            aux) + "\n");
                                Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("max_sl"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                int aucsy = Vars.max_sl;
                                Vars.max_sl = Integer.parseInt(aux);
                                System.out.printf(Translation.getCfgChanged("Max_sl",
                                                                            (Integer.toString(aucsy)),
                                                                            aux) + "\n");
                                Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("min_sl"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                long aucsy = Vars.max_sl;
                                Vars.min_sl = Integer.parseInt(aux);
                                System.out.printf(Translation.getCfgChanged("Min_sl",
                                                                            (Long.toString(aucsy)),
                                                                            aux) + "\n");
                                Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("max_em"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                long aucsy = Vars.max_em;
                                Vars.max_em = Integer.parseInt(aux);
                                System.out.printf(Translation.getCfgChanged("Max_em",
                                                                            (Long.toString(aucsy)),
                                                                            aux) + "\n");
                                Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("max_hubs_op"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                long aucsy = Vars.max_hubs_op;
                                Vars.max_hubs_op = Integer.parseInt(aux);
                                System.out.printf(Translation.getCfgChanged("Max_hubs_op",
                                                                            (Long.toString(aucsy)),
                                                                            aux) + "\n");
                                Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("max_hubs_reg"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                long aucsy = Vars.max_hubs_reg;
                                Vars.max_hubs_reg = Integer.parseInt(aux);
                                System.out.printf(Translation.getCfgChanged("Max_hubs_reg",
                                                                            (Long.toString(aucsy)),
                                                                            aux) + "\n");
                                Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("max_hubs_user"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                long aucsy = Vars.max_hubs_user;
                                Vars.max_hubs_user = Integer.parseInt(aux);
                                System.out.printf(Translation.getCfgChanged("Max_hubs_user",
                                                                            (Long.toString(aucsy)),
                                                                            aux) + "\n");
                                Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("max_sch_chars"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                long aucsy = Vars.max_sch_chars;
                                Vars.max_sch_chars = Integer.parseInt(aux);
                                System.out.printf(Translation.getCfgChanged("Max_sch_chars",
                                                                            (Long.toString(aucsy)),
                                                                            aux) + "\n");
                                Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("min_sch_chars"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                long aucsy = Vars.min_sch_chars;
                                Vars.min_sch_chars = Integer.parseInt(aux);
                                System.out.printf(Translation.getCfgChanged("Min_sch_chars",
                                                                            (Long.toString(aucsy)),
                                                                            aux) + "\n");
                                Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("max_chat_msg"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                long aucsy = Vars.max_chat_msg;
                                Vars.max_chat_msg = Integer.parseInt(aux);
                                System.out.printf(Translation.getCfgChanged("Max_chat_msg",
                                                                            (Long.toString(aucsy)),
                                                                            aux) + "\n");
                                Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }

                        else if (aux.toLowerCase().equals("history_lines"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                int aucsy = Vars.history_lines;
                                if (Integer.parseInt(aux) < 10)
                                {
                                    Vars.history_lines = 10;
                                }
                                else if (Integer.parseInt(aux) > 3000)
                                {
                                    Vars.history_lines = 3000;
                                }
                                else
                                {
                                    Vars.history_lines = Integer.parseInt(aux);
                                }

                                System.out.printf(Translation.getCfgChanged("History_lines",
                                                                            Integer.toString(aucsy),
                                                                            aux) + "\n");
                                Main.Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("opchat_name"))
                        {
                            //  String ucsy=Vars.HubName;
                            String new_name = ST.nextToken();

                            if (!Vars.ValidateNick(new_name))
                            {
                                System.out.println(Translation.getString("invalid_nick"));
                                return;
                            }

                            for (Client tempy : SessionManager.getUsers())
                            {
                                if (tempy.handler.userok == 1)
                                {
                                    if (tempy.handler.NI.equalsIgnoreCase(new_name))
                                    {
                                        System.out.println(Translation.getString("nick_taken"));
                                        return;
                                    }
                                }
                            }

                            if (new_name.equalsIgnoreCase(Vars.bot_name))
                            {
                                System.out.println(Translation.getString("nick_taken"));
                                return;
                            }


                            System.out.println(Translation.getCfgChanged("Opchat_name",
                                                                         Vars.Opchat_name,
                                                                         new_name));

                            Vars.Opchat_name = new_name;
                            Main.Server.rewriteconfig();
                            Broadcast.getInstance()
                                     .broadcast("BINF ABCD NI" + Vars.Opchat_name,
                                                Broadcast.STATE_ALL_KEY);

                        }
                        else if (aux.toLowerCase().equals("bot_name"))
                        {
                            //  String ucsy=Vars.HubName;
                            String new_name = ST.nextToken();

                            if (!Vars.ValidateNick(new_name))
                            {
                                System.out.println(Translation.getString("invalid_nick"));
                                return;
                            }
                            for (Client tempy : SessionManager.getUsers())
                            {
                                if (tempy.handler.userok == 1)
                                {
                                    if (tempy.handler.NI.equalsIgnoreCase(new_name))
                                    {
                                        System.out.println(Translation.getString("nick_taken"));
                                        return;
                                    }
                                }
                            }

                            if (new_name.equalsIgnoreCase(Vars.Opchat_name))
                            {
                                System.out.println(Translation.getString("nick_taken"));
                                return;
                            }

                            System.out.println(Translation.getCfgChanged("Bot_name",
                                                                         Vars.bot_name, new_name));

                            Vars.bot_name = new_name;
                            Main.Server.rewriteconfig();
                            Broadcast.getInstance().broadcast("BINF DCBA NI" + Vars.bot_name);

                        }
                        else if (aux.toLowerCase().equals("opchat_desc"))
                        {
                            //  String ucsy=Vars.HubName;
                            String new_name = ST.nextToken();
                            while (ST.hasMoreTokens())
                            {
                                new_name = new_name + " " + ST.nextToken();
                            }

                            System.out.println(Translation.getCfgChanged("Opchat_desc",
                                                                         Vars.Opchat_desc,
                                                                         new_name));

                            Vars.Opchat_desc = new_name;
                            Main.Server.rewriteconfig();
                            Broadcast.getInstance()
                                     .broadcast("BINF ABCD DE" + Vars.Opchat_desc,
                                                Broadcast.STATE_ALL_KEY);

                        }
                        else if (aux.toLowerCase().equals("bot_desc"))
                        {
                            //  String ucsy=Vars.HubName;
                            String new_name = ST.nextToken();
                            while (ST.hasMoreTokens())
                            {
                                new_name = new_name + " " + ST.nextToken();
                            }

                            System.out.println(Translation.getCfgChanged("Bot_desc",
                                                                         Vars.bot_desc, new_name));

                            Vars.bot_desc = new_name;
                            Main.Server.rewriteconfig();
                            Broadcast.getInstance().broadcast("BINF DCBA DE" + Vars.bot_desc);

                        }
                        else if (aux.toLowerCase().equals("kick_time"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                int aucsy = Vars.kick_time;
                                if (Integer.parseInt(aux) < 0)
                                {
                                    System.out.println(Translation.getString("invalid_num"));
                                    continue;
                                }
                                Vars.kick_time = Integer.parseInt(aux);

                                System.out.printf(Translation.getCfgChanged("Kick_time",
                                                                            Integer.toString(aucsy),
                                                                            aux) + "\n");
                                Main.Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("msg_banned"))
                        {
                            //  String ucsy=Vars.HubName;
                            String new_name = ST.nextToken();
                            while (ST.hasMoreTokens())
                            {
                                new_name = new_name + " " + ST.nextToken();
                            }

                            System.out.println(Translation.getCfgChanged("Msg_Banned",
                                                                         Vars.Msg_Banned,
                                                                         new_name));

                            Vars.Msg_Banned = new_name;
                            Main.Server.rewriteconfig();


                        }


                        else if (aux.toLowerCase().equals("reg_only"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                int aucsy = Vars.reg_only;
                                Vars.reg_only = Integer.parseInt(aux);
                                System.out.printf(Translation.getCfgChanged("Reg_only",
                                                                            Integer.toString(aucsy),
                                                                            aux) + "\n");
                                Main.Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("command_pm"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                int aucsy = Vars.command_pm;
                                Vars.command_pm = Integer.parseInt(aux);
                                System.out.printf(Translation.getCfgChanged("Command_PM",
                                                                            Integer.toString(aucsy),
                                                                            aux) + "\n");
                                Main.Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("nick_chars"))
                        {
                            //  String ucsy=Vars.HubName;
                            String new_name = ST.nextToken();
                            //while(ST.hasMoreTokens ())
                            //    new_name=new_name+" "+ST.nextToken ();

                            if (new_name.length() < 2)
                            {
                                System.out.println(Translation.getString("nick_chars_short"));
                                continue;
                            }

                            System.out.println(Translation.getCfgChanged("Nick_chars",
                                                                         Vars.nick_chars,
                                                                         new_name));

                            Vars.nick_chars = new_name;
                            Main.Server.rewriteconfig();
                            //new Broadcast ("IINF NI"+Vars.HubName);

                        }

                        else if (aux.toLowerCase().equals("msg_full"))
                        {
                            //  String ucsy=Vars.HubName;
                            String new_name = ST.nextToken();
                            while (ST.hasMoreTokens())
                            {
                                new_name = new_name + " " + ST.nextToken();
                            }

                            System.out.println(Translation.getCfgChanged("Msg_Full",
                                                                         Vars.Msg_Full, new_name));

                            Vars.Msg_Full = new_name;
                            Main.Server.rewriteconfig();
                            //new Broadcast ("IINF NI"+Vars.HubName);

                        }
                        else if (aux.toLowerCase().equals("max_users"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                int aucsy = Vars.max_users;
                                Vars.max_users = Integer.parseInt(aux);
                                System.out.println(Translation.getCfgChanged("Max_Users",
                                                                             Integer.toString(aucsy),
                                                                             aux));
                                Main.Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("chat_interval"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                int aucsy = Vars.chat_interval;
                                int blahhh = Integer.parseInt(aux);
                                if (blahhh < 20)
                                {
                                    System.out.println(Translation.getString("chat_interval"));
                                    continue;
                                }
                                Vars.chat_interval = blahhh;


                                System.out.println(Translation.getCfgChanged("Chat_Interval",
                                                                             Integer.toString(aucsy),
                                                                             aux));
                                Main.Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }

                        else if (aux.toLowerCase().equals("save_logs"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                int aucsy = Vars.savelogs;
                                Vars.savelogs = Integer.parseInt(aux);
                                System.out.println(Translation.getCfgChanged("Save_logs",
                                                                             Integer.toString(aucsy),
                                                                             aux));
                                Main.Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }

                        else if (aux.toLowerCase().equals("automagic_search"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                int aucsy = Vars.automagic_search;
                                int x = Integer.parseInt(aux);

                                Vars.automagic_search = x;
                                System.out.println(Translation.getCfgChanged("Automagic_search",
                                                                             Integer.toString(aucsy),
                                                                             aux));
                                Main.Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("search_log_base"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                int aucsy = Vars.search_log_base;
                                int x = Integer.parseInt(aux);

                                Vars.search_log_base = x;
                                System.out.println(Translation.getCfgChanged("Search_log_base",
                                                                             Integer.toString(aucsy),
                                                                             aux));
                                Main.Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("search_steps"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                int aucsy = Vars.search_steps;
                                int x = Integer.parseInt(aux);

                                Vars.search_steps = x;
                                System.out.println(Translation.getCfgChanged("Search_steps",
                                                                             Integer.toString(aucsy),
                                                                             aux));
                                Main.Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("search_spam_reset"))
                        {
                            aux = ST.nextToken();
                            try
                            {
                                int aucsy = Vars.search_spam_reset;
                                int x = Integer.parseInt(aux);

                                Vars.search_spam_reset = x;
                                System.out.println(Translation.getCfgChanged("Search_spam_reset",
                                                                             Integer.toString(aucsy),
                                                                             aux));
                                Main.Server.rewriteconfig();
                            }

                            catch (NumberFormatException nfe)
                            {
                                System.out.println(Translation.getString("invalid_num"));
                            }
                        }
                        else if (aux.toLowerCase().equals("msg_search_spam"))
                        {

                            String new_name = ST.nextToken();
                            while (ST.hasMoreTokens())
                            {
                                new_name = new_name + " " + ST.nextToken();
                            }

                            System.out.println(Translation.getCfgChanged("Msg_search_spam",

                                                                         Vars.Msg_Search_Spam,
                                                                         new_name));

                            Vars.Msg_Search_Spam = new_name;
                            Main.Server.rewriteconfig();
                            //new Broadcast ("IINF NI"+Vars.HubName);

                        }
                        else if (aux.toLowerCase().equals("list"))
                        {
                            System.out.println(Translation.getString("cfg_list") + ": \n" +
                                               "   timeout_login           " +
                                               Integer.toString(Vars.Timeout_Login) +
                                               "         -- " +
                                               Translation.getString("timeout_login") +
                                               "\n"
                                               +
                                               "   hub_name                " +
                                               Vars.HubName +
                                               "         -- " +
                                               Translation.getString("hub_name") +
                                               "\n"
                                               +
                                               "   hub_host                " +
                                               Vars.Hub_Host +
                                               "         -- " +
                                               Translation.getString("hub_host") +
                                               "\n"
                                               +
                                               "   proxy_host                " +
                                               Vars.Proxy_Host +
                                               "         -- " +
                                               Translation.getString("proxy_port") +
                                               "\n"
                                               +
                                               "   proxy_port                " +
                                               Vars.Proxy_Port +
                                               "         -- " +
                                               Translation.getString("proxy_port") +
                                               "\n"
                                               +
                                               "   redirect_url              " +
                                               Vars.redirect_url +
                                               "         -- " +
                                               Translation.getString("redirect_url") +
                                               "\n"
                                               +
                                               "   max_ni                  " +
                                               Vars.max_ni +
                                               " -- " +
                                               Translation.getString("max_ni") +
                                               "\n"
                                               +
                                               "   min_ni                  " +
                                               Vars.min_ni +
                                               " -- " +
                                               Translation.getString("min_ni") +
                                               "\n"
                                               +
                                               "   max_de                  " +
                                               Vars.max_de +
                                               " -- " +
                                               Translation.getString("max_de") +
                                               "\n"
                                               +
                                               "   max_share               " +
                                               Vars.max_share +
                                               " -- " +
                                               Translation.getString("max_share") +
                                               "\n"
                                               +
                                               "   min_share               " +
                                               Vars.min_share +
                                               " -- " +
                                               Translation.getString("min_share") +
                                               "\n"
                                               +
                                               "   max_sl                  " +
                                               Vars.max_sl +
                                               " -- " +
                                               Translation.getString("max_sl") +
                                               "\n"
                                               +
                                               "   min_sl                  " +
                                               Vars.min_sl +
                                               " -- " +
                                               Translation.getString("min_sl") +
                                               "\n"
                                               +
                                               "   max_em                  " +
                                               Vars.max_em +
                                               " -- " +
                                               Translation.getString("max_em") +
                                               "\n"
                                               +
                                               "   max_hubs_op             " +
                                               Vars.max_hubs_op +
                                               " -- " +
                                               Translation.getString("max_hubs_op") +
                                               "\n"
                                               +
                                               "   max_hubs_reg            " +
                                               Vars.max_hubs_reg +
                                               " -- " +
                                               Translation.getString("max_hubs_reg") +
                                               "\n"
                                               +
                                               "   max_hubs_user           " +
                                               Vars.max_hubs_user +
                                               " -- " +
                                               Translation.getString("max_hubs_user") +
                                               "\n"
                                               +
                                               "   max_sch_chars           " +
                                               Vars.max_sch_chars +
                                               " -- " +
                                               Translation.getString("max_search_chars") +
                                               "\n"
                                               +
                                               "   min_sch_chars           " +
                                               Vars.min_sch_chars +
                                               " -- " +
                                               Translation.getString("min_search_chars") +
                                               "\n"
                                               +
                                               "   max_chat_msg            " +
                                               Vars.max_chat_msg +
                                               " -- " +
                                               Translation.getString("max_chat_msg") +
                                               "\n"
                                               +
                                               "   max_users               " +
                                               Vars.max_users +
                                               " -- " +
                                               Translation.getString("max_users") +
                                               "\n"
                                               +
                                               "   history_lines           " +
                                               Vars.history_lines +
                                               " -- Number of lines to keep in chat history.\n"
                                               +
                                               "   opchat_name             " +
                                               Vars.Opchat_name +
                                               " -- The Operator Chat Bot Nick.\n"
                                               +
                                               "   opchat_desc             " +
                                               Vars.Opchat_desc +
                                               " -- The Operator Chat Bot Description.\n"
                                               +
                                               "   kick_time               " +
                                               Vars.kick_time +
                                               " -- The time to ban a user with a kick, in seconds.\n"
                                               +
                                               "   msg_banned              " +
                                               Vars.Msg_Banned +
                                               " -- The aditional message to show to banned users when connecting.\n"

                                               +
                                               "   msg_full                " +
                                               Vars.Msg_Full +
                                               " -- Message to be shown to connecting users when hub full.\n"
                                               +
                                               "   reg_only                " +
                                               Vars.reg_only +
                                               " -- 1 = registered only hub. 0 = otherwise.\n"
                                               +
                                               "   command_pm              " +
                                               Vars.command_pm +
                                               "         -- If set to 1, the bot's responses are sent to PM.\n"
                                               +
                                               "   nick_chars              " +
                                               Vars.nick_chars +
                                               " -- Regular Expression that a nick needs to match,  String.\n"
                                               +
                                               "   chat_interval           " +
                                               Vars.chat_interval +
                                               "         -- Interval between chat lines, millis, Integer.\n"
                                               +
                                               "   save_logs               " +
                                               Vars.savelogs +
                                               "         -- 1 = logs are saved to file, 0 otherwise.\n"
                                               +
                                               "   automagic_search        " +
                                               Vars.automagic_search +
                                               "         -- Interval between automagic searches for each user, seconds, Integer.\n"
                                               +
                                               "   search_log_base         " +
                                               Vars.search_log_base +
                                               "         -- Logarithmic base for user searches interval,millis, Integer.\n"
                                               +
                                               "   search_steps            " +
                                               Vars.search_steps +
                                               "         -- Maximum nr of search steps allowed until reset needed, Integer.\n"
                                               +
                                               "   search_spam_reset       " +
                                               Vars.search_spam_reset +
                                               "         -- Interval until search_steps is being reset, seconds, Integer.\n"
                                               +
                                               "   msg_search_spam         " +
                                               Vars.Msg_Search_Spam +
                                               "         -- Message that appears as a result when search is delayed, String.\n"
                                               +
                                               "   bot_name                " +
                                               Vars.bot_name +
                                               "         -- Hub security bot name, String.\n"
                                               +
                                               "   bot_desc                " +
                                               Vars.bot_desc +
                                               "         -- Hub security bot description, String."
                                              );
                        }
                        else
                        {
                            System.out
                                    .println("Invalid cfg variable. Use \"cfg list\" to see all.");
                        }
                    }
                }
                else if (recvbuf.toLowerCase().startsWith("topic"))
                {
                    if (recvbuf.toLowerCase().equals("topic"))
                    {

                        Broadcast.getInstance().broadcast("IINF DE");
                        if (!Vars.HubDE.equals(""))
                        {
                            System.out.println("Topic \"" + Vars.HubDE + "\" deleted.");
                            Broadcast.getInstance().broadcast("IMSG Topic was deleted by Server.");
                        }
                        else
                        {
                            System.out.println("There wasn't any topic anyway.");
                        }
                        Vars.HubDE = "";


                    }
                    else
                    {
                        String auxbuf = recvbuf.substring(6);


                        Vars.HubDE = Vars.HubDE.replaceAll("\\ ", " ");
                        System.out
                                .println("Topic changed from \"" +
                                         Vars.HubDE +
                                         "\" " +
                                         "to \"" +
                                         auxbuf +
                                         "\".");
                        auxbuf = auxbuf;
                        Vars.HubDE = auxbuf;

                        Broadcast.getInstance().broadcast("IINF DE" + auxbuf);
                        Broadcast.getInstance()
                                 .broadcast("IMSG Topic was changed by Server to \"" +
                                            Vars.HubDE +
                                            "\"");

                    }
                }
                else if (recvbuf.toLowerCase().startsWith("port"))
                {

                    new PortCmd(null, recvbuf);

                }
                else if (recvbuf.toLowerCase().equals("usercount"))
                {
                    int i = 0, j = 0;
                    for (Client temp : SessionManager.getUsers())
                    {
                        if (temp.handler.userok == 1)
                        {
                            i++;
                        }
                        else
                        {
                            j++;
                        }

                    }
                    System.out.printf("Current user count: %d. In progress users: %d.\n", i, j);
                }
                else if (recvbuf.toLowerCase().equals("sessions"))
                {

                }
                else if (recvbuf.toLowerCase().equals("about"))
                {
                    System.out.println(Vars.About);
                }
                else if (recvbuf.toLowerCase().equals("stats"))
                {
                    Runtime myRun = Runtime.getRuntime();

                    //Proppies.getProperty();
                    int i = 0, j = 0;
                    for (Client temp : SessionManager.getUsers())
                    {
                        if (temp.getClientHandler().userok == 1)
                        {
                            i++;
                        }
                        else
                        {
                            j++;
                        }

                    }

                    long up = System.currentTimeMillis() - curtime; //uptime in millis


                    System.out.printf(
                            "Death Squad Hub. Version " + Vars.HubVersion + ".\n" +
                            "  Running on %s Version %s on Architecture %s\n" +
                            "  Java Runtime Environment %s from %s\n" +
                            "  Java Virtual Machine %s\n" +
                            "  Available CPU's to JVM %d\n" +
                            "  Available Memory to JVM: %s Bytes, where free: %s Bytes\n" +
                            "Hub Statistics:\n" +
                            "  Online users: %d\n" +
                            "  Connecting users: %d\n" +
                            "  Uptime: %s\n"
                            //    +
                            //   "\n  Bytes read per second: "+Main.Server.acceptor.getReadBytesThroughput()+
                            //  "\n  Bytes written per second: "+Main.Server.acceptor.getWrittenBytesThroughput()


                            ,
                            Proppies.getProperty("os.name"),
                            Proppies.getProperty("os.version"),
                            Proppies.getProperty("os.arch"),
                            Proppies.getProperty("java.version"),
                            Proppies.getProperty("java.vendor"),
                            Proppies.getProperty("java.vm.specification.version"),
                            myRun.availableProcessors(),
                            Long.toString(myRun.maxMemory()),
                            Long.toString(myRun.freeMemory()),
                            i,
                            j,
                            TimeConv.getStrTime(up)
                                     );
                }
                else if (recvbuf.equals(""))
                {
                    ;
                }
                else
                {
                    System.out.println("Unknown Command. Type help for info, quit for quit");
                }
                System.out.print(">");
            }
        }
        catch (IOException bl)
        {

        }
    }


}




