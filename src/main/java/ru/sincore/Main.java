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
import ru.sincore.TigerImpl.Base32;
import ru.sincore.banning.BanList;
import ru.sincore.i18n.Messages;
import ru.sincore.python.PythonManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
                            log.info(String.format(Messages.NOT_CID, clientHandler.NI).concat("\n")
											 .concat(String.format(Messages.USER_REGISTER, clientHandler.NI, clientHandler.ID)));

                            Main.Server.rewriteregs();
                            return;
                        }
                    }
                }

            	//TODO add error message in log stream. Cause, no user online.
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
                        clientHandler.sendFromBot(Messages.REG_MESSAGE);

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
                        log.info(Messages.NOT_CID.concat("\n").concat(String.format(Messages.USER_REGISTER, clientHandler.NI, clientHandler.ID)));

                        Main.Server.rewriteregs();
                        return;
                    }
                }
            }

            //TODO add error message in log stream. Cause, no user online.
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
        System.out.println(Messages.SERVER_STARTUP);

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


        Proppies = System.getProperties();

        log.info(Messages.SERVER_STARTUP_DONE);

        InputStreamReader b = new InputStreamReader(System.in);
        BufferedReader bla = new BufferedReader(b);


        try
        {

            while (true)
            {

                String recvbuf = bla.readLine();

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
							log.info(Messages.SEARCHING_IN_PROGRESS.concat("\n").concat(Messages.CID_UNBANNED));
                        }
                        else
                        {
                            log.info(Messages.SEARCHING_IN_PROGRESS.concat("\n").concat(Messages.CID_NOT_BANNED));
                        }
                    }
                    catch (IllegalArgumentException iae)
                    {
                       //TODO logic on exceptions, imho bad idea
                       /*
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
                        */
                    }
                    log.info(Messages.DONE);

                    Main.Server.rewritebans();


                }
                else if (recvbuf.toLowerCase().equals("listreg"))
                {
                    //TODO rewrite this.. is needed ?
                    /*
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
					 */
                }

				//TODO  command "ureg","reg","grant","cfg" - add implemented
                else if (recvbuf.toLowerCase().startsWith("ureg "))
                {

                }
                else if (recvbuf.toLowerCase().startsWith("reg "))
                {

                }
                else if (recvbuf.toLowerCase().startsWith("grant"))
                {

                }
                else if (recvbuf.toLowerCase().startsWith("cfg"))
                {

                }
                else if (recvbuf.toLowerCase().startsWith("topic"))
                {

                }
                else if (recvbuf.toLowerCase().startsWith("port"))
                {

                }
                else if (recvbuf.toLowerCase().equals("usercount"))
                {

                }
                else if (recvbuf.toLowerCase().equals("sessions"))
                {

                }
                else if (recvbuf.toLowerCase().equals("about"))
                {

                }
                else if (recvbuf.toLowerCase().equals("stats"))
                {

                }
                else if (recvbuf.equals(""))
                {

                }
                else
                {
                    System.out.println("Unknown Command. Type help for info, quit for quit");
                }
            }
        }
        catch (IOException bl)
        {
		   log.error(bl);
        }
    }


}