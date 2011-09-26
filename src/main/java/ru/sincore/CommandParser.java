/*
 * CommandParser.java
 *
 * Created on 29 aprilie 2007, 11:55
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
import ru.sincore.Modules.Modulator;
import ru.sincore.Modules.Module;
import ru.sincore.TigerImpl.Base32;
import ru.sincore.adcs.AdcsCommand;
import ru.sincore.banning.BanList;
import ru.sincore.cmd.*;
import ru.sincore.cmd.ExtendedCmds.*;
import ru.sincore.util.AdcUtils;
import ru.sincore.util.TimeConv;

import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Basic client commands sent to hub via mainchat or pm to hub bot.
 * Provides configuration in hub functionality.
 *
 * @author Pietricica
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-08
 */
public class CommandParser
{
    public static final Logger log = Logger.getLogger(CommandParser.class);
    Client client;
    String command;
    /**
     * for !cmdhistory
     */
    List<String> history;
    boolean done = false;

	private BigTextManager bigTextManager = new BigTextManager();

    /**
     * Creates a new instance of CommandParser
     */
    public CommandParser(Client client, String command)
    {
        this.client = client;
        this.command = command;
        //this.setPriority (NORM_PRIORITY);
        run();
    }


    int commandOK = 0;


    public void run()
    {
        runx();
        /* while(!done)
               {
               try
               {
          Thread.sleep(200);
               } catch (Exception exception)
               {
               }
           }*/

        String STR = command;
        String NI = client.getClientHandler().NI;
        String recvbuf = AdcUtils.retNormStr(command.substring(1));

        for (Module myMod : Modulator.myModules)
        {
            int result = 0;
            if (client.getClientHandler().reg.additionalModules) //only if hes allowed to use modules
            {
                result = myMod.onCommand(client.getClientHandler(), recvbuf);
            }

            if (commandOK != 2)
            {
                if (result != 0)
                {
                    commandOK = result;
                }
                if (result == 2)
                {
                    commandOK = result;
                }
            }
        }

        if (commandOK == 0)
        {
            client.getClientHandler().sendFromBot("Unknown Command. Type !help for info.");
        }
        if (commandOK != 2)
        {
            history.add("[" +
                        Calendar.getInstance().getTime().toString() +
                        "] <" +
                        NI +
                        "> " +
                        STR +
                        "\n");
        }
    }


    public void runx()
    {

        String recvbuf = AdcUtils.retNormStr(command.substring(1));
        //	String STR = command;
        //	String NI = handler.NI;


        if (recvbuf.toLowerCase().equals("quit"))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.quit)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }

            client.getClientHandler().sendFromBot("Closing down hub...");
            //	Main.server.rewriteregs();
            //	Main.server.rewriteconfig();
            //	Main.server.rewritebans();
            //save Banned Words List
            //Main.listaBanate.printFile(Main.myPath + "banwlist.txt");

            log.warn("Hub is being shut down by " + client.getClientHandler().NI);

            for (Module x : Modulator.myModules)
            {

                x.onCommand(client.getClientHandler(), recvbuf);
                x.close();
            }

            Main.Exit();
            done = true;
            return;

        }

        else if (recvbuf.toLowerCase().equals("restart"))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.restart)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            client.getClientHandler().sendFromBot("Restarting.... Wait 5 seconds...");

            // TODO remove this crapy code !!!
            client.dropMe(client.getClientHandler());
            //Main.server.rewriteregs();
            //Main.server.rewriteconfig();
            //Main.server.rewritebans();
            //Main.server.restart = true;
            //AccountsConfig.First = null;
            //	BanList.First = null;

            //SessionManager.users.clear();
            log.warn("Hub restarted by " + client.getClientHandler().NI);

            for (Module x : Modulator.myModules)
            {
                x.onCommand(client.getClientHandler(), recvbuf);
                x.close();
            }

            Main.Restart();
            done = true;
            return;
        }
        if (recvbuf.toLowerCase().startsWith("password"))
        {
            commandOK = 2;
            if (!client.getClientHandler().reg.myMask.password)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            StringTokenizer ST = new StringTokenizer(recvbuf);
            ST.nextToken();
            String aux = ST.nextToken();

            Nod temp = AccountsConfig.getnod(client.getClientHandler().ID);
            temp.Password = aux;
            client.getClientHandler().reg.Password = aux;

            // TODO save new passwork to db

            client.getClientHandler().sendFromBot("Your password is now " + aux + ".");

        }
        else if (recvbuf.toLowerCase().startsWith("grant"))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.grant)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            //new GrantCmd(client.getClientHandler(), recvbuf);

        }
		//TODO remove "backup" command
        else if (recvbuf.toLowerCase().startsWith("adcs"))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.adcs)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            new AdcsCommand(client.getClientHandler(), recvbuf);
        }
        else if (recvbuf.toLowerCase().startsWith("hideme"))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.hideme)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            if (!client.getClientHandler().reg.HideMe)
            {
                Broadcast.getInstance().broadcast(
                        "BINF " + client.getClientHandler().SID + " HI1");
                client.getClientHandler()
                        .sendFromBot("You are now hidden, not appearing in userlist no more.");
                client.getClientHandler().reg.HideMe = true;
            }
            else
            {
                Broadcast.getInstance().broadcast(
                        "BINF " + client.getClientHandler().SID + " HI");
                client.getClientHandler()
                        .sendFromBot("You are now revealed, appearing again in userlist.");
                client.getClientHandler().reg.HideMe = false;
            }

        }
        else if (recvbuf.toLowerCase().equals("listreg"))
        {
            // TODO is CID unique only in session ???

            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.listreg)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            String resultMessage = "List :\n";

            for (Nod nod : AccountsConfig.nods.values())
            {
                resultMessage = resultMessage + nod.CID;
                if (nod.LastNI != null)
                {
                    resultMessage = resultMessage + " Last nick: " + nod.LastNI + "\n";
                }
                else
                {
                    resultMessage = resultMessage + " Never seen online." + "\n";
                }
            }
            resultMessage = resultMessage.substring(0, resultMessage.length() - 1);
            client.getClientHandler().sendFromBot(resultMessage);

        }
        else if (recvbuf.toLowerCase().equals("listban"))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.listban)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            String resultMessage = "Ban List :\n";

            // TODO get ban list from db
//                nick (ip, cid - depends on bantype)
//                ban time
//                banop
//                banreason
//                permanentrly or not

            resultMessage = resultMessage.substring(0, resultMessage.length() - 1);
            client.getClientHandler().sendFromBot(resultMessage);

        }
        else if (recvbuf.toLowerCase().startsWith("ureg"))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.ureg)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            StringTokenizer ST = new StringTokenizer(recvbuf);
            ST.nextToken();
            if (!ST.hasMoreTokens())
            {
                done = true;
                return;
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
                    for (Client temp : SessionManager.getUsers())
                    {
                        if (temp.getClientHandler().validated == 1)
                        {
                            if ((temp.getClientHandler().ID.equals(aux)))
                            {
                                temp.getClientHandler()
                                        .sendFromBot(""
                                                     +
                                                     "Your account has been deleted. From now on you are a simple user.");
                                temp.getClientHandler().putOpchat(false);
                                temp.getClientHandler().CT = "0";
                                temp.getClientHandler().can_receive_cmds = false;
                                Broadcast.getInstance().broadcast(
                                        "BINF " + temp.getClientHandler().SID
                                        + " CT");
                                temp.getClientHandler().reg = new Nod();
                                client.getClientHandler().sendFromBot("User "
                                                                      +
                                                                      temp.getClientHandler().NI +
                                                                      " with CID "
                                                                      +
                                                                      aux +
                                                                      " found, deleted.");

                                return;
                            }
                        }

                    }

                    client.getClientHandler().sendFromBot("Reg deleted.");
                    log.info(client.getClientHandler().NI + " deleted the reg " + aux);

                }
                else
                {
                    client.getClientHandler().sendFromBot("Reg not found.");
                }
            }
            catch (IllegalArgumentException iae)
            {
                client.getClientHandler()
                        .sendFromBot("Not a valid CID, checking for possible users...");
                for (Client temp : SessionManager.getUsers())
                {
                    if (temp.getClientHandler().validated == 1)
                    {
                        if ((temp.getClientHandler().NI.toLowerCase().equals(aux
                                                                             .toLowerCase())))
                        {
                            if (!temp.getClientHandler().reg.isreg)
                            {
                                client.getClientHandler()
                                        .sendFromBot("Client exists but not registered.");
                            }
                            else
                            {
                                AccountsConfig.unreg(temp.getClientHandler().ID);
                                log.info(client.getClientHandler().NI + " deleted the reg "
                                         + temp.getClientHandler().ID);
                                client.getClientHandler().sendFromBot("User "
                                                                      +
                                                                      temp.getClientHandler().NI +
                                                                      " deleted.");
                                temp.getClientHandler()
                                        .sendFromBot(
                                                "Your account has been deleted. From now on you are a simple user.");
                                temp.getClientHandler().putOpchat(false);
                                temp.getClientHandler().CT = "0";
                                temp.getClientHandler().can_receive_cmds = false;

                                Broadcast.getInstance().broadcast(
                                        "BINF " + temp.getClientHandler().SID
                                        + " CT");

                                temp.getClientHandler().reg = new Nod();
                                return;
                            }
                        }
                    }
                }

                client.getClientHandler().sendFromBot("No such client online.");

            }

        }
        else if (recvbuf.toLowerCase().startsWith("reg"))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.reg)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            StringTokenizer ST = new StringTokenizer(recvbuf);
            ST.nextToken();
            if (!ST.hasMoreTokens())
            {
                done = true;
                return;
            }
            String aux = ST.nextToken();
            if (aux.length() == 39) //possible CID, lets try
            {
                try
                {
                    Base32.decode(aux);
                    if (AccountsConfig.isReg(aux) > 0)
                    {

                        client.getClientHandler().sendFromBot(""
                                                              +
                                                              AccountsConfig.getnod(aux)
                                                                            .getRegInfo());
                        done = true;
                        return;
                    }
                    for (Client temp : SessionManager.getUsers())
                    {
                        if (temp.getClientHandler().validated == 1)
                        {
                            if ((temp.getClientHandler().ID.equals(aux)))
                            {
                                AccountsConfig.addReg(temp.getClientHandler().ID,
                                                      temp.getClientHandler().NI, client.getClientHandler().NI);
                                temp.getClientHandler().reg = AccountsConfig
                                        .getnod(temp.getClientHandler().ID);
                                client.getClientHandler()
                                        .sendFromBot("User "
                                                     + temp.getClientHandler().NI
                                                     + " found with CID "
                                                     + aux
                                                     +
                                                     ", added. No password set, login does not require pass, however, its recomandable to set one...");
                                temp.getClientHandler()
                                        .sendFromBot("You have been registered by "
                                                     + client.getClientHandler().NI
                                                     +
                                                     " . No password set, login does not require pass, however, its recomandable you to set one...");
                                temp.getClientHandler().putOpchat(true);
                                temp.getClientHandler().CT = "2";

                                Broadcast.getInstance().broadcast("BINF "
                                                                  +
                                                                  temp.getClientHandler().SID +
                                                                  " CT2");
                                temp.getClientHandler().can_receive_cmds = true;
                                temp.getClientHandler().reg.isreg = true;
                                temp.getClientHandler().loggedAt = System
                                        .currentTimeMillis();
                                temp.getClientHandler().reg.LastIP = temp.getClientHandler().realIP;

                                return;
                            }
                        }
                    }

                    AccountsConfig.addReg(aux, null, client.getClientHandler().NI);
                    client.getClientHandler()
                            .sendFromBot(
                                    "CID added. No password set, login does not require pass, however, its recomandable to set one...");

                    log.info(client.getClientHandler().NI + " regged the CID " + aux);

                }
                catch (IllegalArgumentException iae)
                {
                    //handler.sendFromBot("Not a CID, trying to add the "+aux+" nick.");
                    for (Client temp : SessionManager.getUsers())
                    {
                        if (temp.getClientHandler().validated == 1)
                        {
                            if ((temp.getClientHandler().NI.toLowerCase().equals(aux
                                                                                 .toLowerCase())))
                            {
                                if (AccountsConfig.isReg(temp.getClientHandler().ID) > 0)
                                {
                                    client.getClientHandler().sendFromBot(""
                                                                          + AccountsConfig.getnod(
                                            temp.getClientHandler().ID)
                                                                                          .getRegInfo());
                                    done = true;
                                    return;
                                }
                                AccountsConfig.addReg(temp.getClientHandler().ID,
                                                      temp.getClientHandler().NI, client.getClientHandler().NI);
                                temp.getClientHandler().reg = AccountsConfig
                                        .getnod(temp.getClientHandler().ID);
                                client.getClientHandler()
                                        .sendFromBot("Not a CID, trying to add the "
                                                     + aux + " nick.");
                                client.getClientHandler()
                                        .sendFromBot("User "
                                                     + temp.getClientHandler().NI
                                                     + " found with CID "
                                                     + temp.getClientHandler().ID
                                                     +
                                                     ", added. No password set, login does not require pass, however, its recomandable to set one...");
                                temp.getClientHandler()
                                        .sendFromBot("You have been registered by "
                                                     + client.getClientHandler().NI
                                                     +
                                                     " . No password set, login does not require pass, however, its recomandable you to set one...");
                                temp.getClientHandler().putOpchat(true);
                                temp.getClientHandler().CT = "2";
                                temp.getClientHandler().can_receive_cmds = true;
                                Broadcast.getInstance().broadcast("BINF "
                                                                  +
                                                                  temp.getClientHandler().SID +
                                                                  " CT2");

                                temp.getClientHandler().reg.isreg = true;
                                temp.getClientHandler().loggedAt = System
                                        .currentTimeMillis();
                                temp.getClientHandler().reg.LastIP = temp.getClientHandler().realIP;
                                log.info(client.getClientHandler().NI + " regged the CID "
                                         + temp.getClientHandler().ID);

                                return;
                            }
                        }
                    }

                    client.getClientHandler().sendFromBot("Not a CID, trying to add the "
                                                          + aux + " nick.");
                    client.getClientHandler().sendFromBot("No such client online.");

                }
                catch (Exception e)
                {
                    return;
                }
            }
            else
            {
                //handler.sendFromBot("Not a CID, trying to add the "+aux+" nick.");
                for (Client temp : SessionManager.getUsers())
                {
                    if (temp.getClientHandler().validated == 1)
                    {
                        if ((temp.getClientHandler().NI.toLowerCase().equals(aux
                                                                             .toLowerCase())))
                        {
                            if (AccountsConfig.isReg(temp.getClientHandler().ID) > 0)
                            {
                                client.getClientHandler().sendFromBot(""
                                                                      + AccountsConfig.getnod(
                                        temp.getClientHandler().ID)
                                                                                      .getRegInfo());
                                done = true;
                                return;
                            }
                            AccountsConfig.addReg(temp.getClientHandler().ID,
                                                  temp.getClientHandler().NI, client.getClientHandler().NI);
                            temp.getClientHandler().reg = AccountsConfig
                                    .getnod(temp.getClientHandler().ID);
                            client.getClientHandler()
                                    .sendFromBot("Not a CID, trying to add the "
                                                 + aux + " nick.");
                            client.getClientHandler()
                                    .sendFromBot("User "
                                                 + temp.getClientHandler().NI
                                                 + " found with CID "
                                                 + temp.getClientHandler().ID
                                                 +
                                                 ", added. No password set, login does not require pass, however, its recomandable to set one...");
                            temp.getClientHandler()
                                    .sendFromBot("You have been registered by "
                                                 + client.getClientHandler().NI
                                                 +
                                                 " . No password set, login does not require pass, however, its recomandable you to set one...");
                            temp.getClientHandler().putOpchat(true);
                            temp.getClientHandler().CT = "2";

                            Broadcast.getInstance().broadcast("BINF " + temp.getClientHandler().SID
                                                              + " CT2");
                            temp.getClientHandler().can_receive_cmds = true;
                            temp.getClientHandler().loggedAt = System
                                    .currentTimeMillis();
                            temp.getClientHandler().reg.LastIP = temp.getClientHandler().realIP;
                            log.info(client.getClientHandler().NI + " regged the CID "
                                     + temp.getClientHandler().ID);

                            return;
                        }
                    }
                }
                client.getClientHandler().sendFromBot("Not a CID, trying to add the " + aux
                                                      + " nick.");
                client.getClientHandler().sendFromBot("No such client online.");

            }

        }
        else if (recvbuf.toLowerCase().equals("help"))
        {
            commandOK = 1;

            if (!client.getClientHandler().reg.myMask.help)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }

            client.getClientHandler().sendFromBot("TODO: show help ");

        }
        else if (recvbuf.toLowerCase().startsWith("info "))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.info)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            new ExtInfo(client.getClientHandler(), recvbuf);

        }

        else if (recvbuf.toLowerCase().startsWith("mass"))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.mass)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            new ExtMass(client.getClientHandler(), recvbuf);

        }
        else if (recvbuf.toLowerCase().startsWith("redirect"))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.redirect)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            new ExtRedirect(client.getClientHandler(), recvbuf);

        }
        else if (recvbuf.toLowerCase().startsWith("mynick "))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.mynick)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            StringTokenizer ST = new StringTokenizer(AdcUtils.retNormStr(recvbuf));
            ST.nextToken();
            String aux = "";
            while (ST.hasMoreTokens())
            {
                aux = aux + ST.nextToken() + " "; //new nick
            }
            if (!(aux.equals("")))
            {
                aux = aux.substring(0, aux.length() - 1);
            }
            aux = AdcUtils.retADCStr(aux);
            if (aux.length() < ConfigLoader.MIN_NICK_SIZE)
            {
                {
                    client.getClientHandler()
                            .sendFromBot("Nick too small, please choose another.");
                    done = true;
                    return;
                }
            }
            if (aux.length() > ConfigLoader.MAX_NICK_SIZE)
            {
                {
                    client.getClientHandler()
                            .sendFromBot("Nick too large, please choose another.");
                    {
                        done = true;
                        return;
                    }
                }
            }
// TODO validate nick
/*
            if (! Nick.validateNick(aux))
            {
                client.getClientHandler()
                        .sendFromBot("Nick not valid, please choose another.");
                System.out.println(aux);
                {
                    done = true;
                    return;
                }
            }
*/
            if (AccountsConfig.nickReserved(aux, client.getClientHandler().ID))
            {

                client.getClientHandler().sendFromBot("Nick reserved. Please choose another.");
                {
                    done = true;
                    return;
                }
            }

            for (Client tempy : SessionManager.getUsers())
            {
                if (tempy.getClientHandler().validated == 1)
                {
                    if ((tempy.getClientHandler().NI.toLowerCase().equals(aux.toLowerCase())))
                    {
                        client.getClientHandler()
                                .sendFromBot("Nick taken, please choose another.");
                        {
                            done = true;
                            return;
                        }
                    }
                }

            }

            if (aux.equalsIgnoreCase(ConfigLoader.OP_CHAT_NAME))
            {
                client.getClientHandler().sendFromBot("Nick taken, please choose another.");
                {
                    done = true;
                    return;
                }
            }
            if (aux.equalsIgnoreCase(ConfigLoader.BOT_CHAT_NAME))
            {
                client.getClientHandler().sendFromBot("Nick taken, please choose another.");
                {
                    done = true;
                    return;
                }
            }

            Broadcast.getInstance().broadcast("BINF " + client.getClientHandler().SID + " NI" + aux);

            Broadcast.getInstance().broadcast("IMSG " + client.getClientHandler().NI + " is now known as " + aux);
            client.getClientHandler().NI = aux;

        }
        else if (recvbuf.toLowerCase().startsWith("rename "))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.rename)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            //handler.sendFromBot(""+AdcUtils.retADCStr("Sorry but renaming features are temporary disabled until DC++ has UCMD's ( because !rename mister bla new nick has 4 entities and its quite hard to guess what is first nick and what is 2nd nick."));
            StringTokenizer ST = new StringTokenizer(recvbuf);
            ST.nextToken();
            String aux = ST.nextToken(); //the nick to rename;
            // aux=AdcUtils.retADCStr(aux);
            for (Client temp : SessionManager.getUsers())
            {
                if (temp.getClientHandler().validated == 1)
                {
                    if ((temp.getClientHandler().NI.toLowerCase().equals(aux
                                                                         .toLowerCase())))
                    {
                        if (!temp.getClientHandler().reg.renameable)
                        {
                            client.getClientHandler()
                                    .sendFromBot("This registered user cannot be renamed.");
                        }

                        else
                        {
                            //actual renaming.
                            String newnick = ST.nextToken();
                            if (newnick.length() < ConfigLoader.MIN_NICK_SIZE)
                            {
                                {
                                    client.getClientHandler()
                                            .sendFromBot("Nick too small, please choose another.");
                                    done = true;
                                    return;
                                }
                            }
                            if (newnick.length() > ConfigLoader.MAX_NICK_SIZE)
                            {
                                {
                                    client.getClientHandler()
                                            .sendFromBot("Nick too large, please choose another.");
                                    done = true;
                                    return;
                                }
                            }
// TODO validate nick
/*
                            if (!Nick.validateNick(newnick))
                            {
                                client.getClientHandler()
                                        .sendFromBot("Nick not valid, please choose another.");
                                done = true;
                                return;
                            }
*/
                            if (AccountsConfig.nickReserved(newnick,
                                                            temp.getClientHandler().ID))
                            {

                                client.getClientHandler()
                                        .sendFromBot("Nick reserved. Please choose another.");
                                done = true;
                                return;
                            }
                            for (Client tempy : SessionManager.getUsers())
                            {
                                if (tempy.getClientHandler().validated == 1)
                                {
                                    if ((tempy.getClientHandler().NI.toLowerCase()
                                                            .equals(newnick.toLowerCase())))
                                    {
                                        client.getClientHandler()
                                                .sendFromBot("Nick taken, please choose another.");
                                        done = true;
                                        return;
                                    }
                                }

                            }

                            if (newnick.equals(ConfigLoader.OP_CHAT_NAME))
                            {
                                client.getClientHandler()
                                        .sendFromBot("Nick taken, please choose another.");
                                done = true;
                                return;
                            }
                            if (newnick.equals(ConfigLoader.BOT_CHAT_NAME))
                            {
                                client.getClientHandler()
                                        .sendFromBot("Nick taken, please choose another.");
                                done = true;
                                return;
                            }
                            Broadcast.getInstance().broadcast("BINF " + temp.getClientHandler().SID
                                                              + " NI" + newnick);

                            client.getClientHandler().sendFromBot("Renamed user "
                                                                  +
                                                                  temp.getClientHandler().NI +
                                                                  " to " +
                                                                  newnick);
                            Broadcast.getInstance().broadcast("IMSG " + temp.getClientHandler().NI
                                                              + " is now known as " + newnick);
                            temp.getClientHandler().NI = newnick;
                            return;

                        }
                    }
                }

            }

            client.getClientHandler().sendFromBot("No such user online.");

        }
        else if (recvbuf.toLowerCase().startsWith("kick"))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.kick)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            new ExtKick(client.getClientHandler(), recvbuf);

        }
        else if (recvbuf.toLowerCase().startsWith("plugmin"))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.plugmin)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            //new PlugminCmd(client.getClientHandler(), recvbuf);

        }
        else if (recvbuf.toLowerCase().startsWith("chatcontrol"))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.chatcontrol)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            new ChatControlCmd(client.getClientHandler(), recvbuf);
        }
        else if (recvbuf.toLowerCase().startsWith("drop"))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.drop)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            new ExtDrop(client.getClientHandler(), recvbuf);
        }
        else if (recvbuf.toLowerCase().startsWith("unban"))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.unban)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            StringTokenizer ST = new StringTokenizer(AdcUtils.retNormStr(recvbuf));
            ST.nextToken();
            if (!ST.hasMoreTokens())
            {
                client.getClientHandler().sendFromBot("Nothing specified for unbanning.");
                done = true;
                return;
            }
            String aux = ST.nextToken(); //the thing to unban;
            //al right,now must check if that is a nick, cid or ip
            //first if its a cid...
            aux = AdcUtils.retADCStr(aux);
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
                    client.getClientHandler().sendFromBot("Searching...");
                    client.getClientHandler().sendFromBot("Found CID " + aux + ", unbanned.");

                }
                else
                {
                    client.getClientHandler().sendFromBot("Searching...");
                    client.getClientHandler().sendFromBot("Found CID " + aux
                                                          + ", not banned nothing to do.");
                }
            }
            catch (IllegalArgumentException iae)
            {
                //ok its not a cid, lets check if its some IP address...
                client.getClientHandler().sendFromBot("Not a CID, Searching...");
                if (AdcUtils.isIP(aux))
                {
                    client.getClientHandler().sendFromBot("Is IP ...checking if banned...");
                    if (BanList.delban(2, aux))
                    {
                        client.getClientHandler().sendFromBot("Found IP address " + aux
                                                              + ", unbanned.");
                    }
                    else
                    {
                        client.getClientHandler().sendFromBot("Found IP address " + aux
                                                              +
                                                              ", but is not banned, nothing to do.");
                    }
                }
                else
                {
                    client.getClientHandler().sendFromBot("Is not IP...Checking for nick...");
                    if (BanList.delban(1, aux))
                    {
                        client.getClientHandler().sendFromBot("Found nick " + aux
                                                              + ", unbanned.");
                    }
                    else
                    {
                        client.getClientHandler().sendFromBot("Nick " + aux
                                                              + " is not banned, nothing to do.");
                    }
                }
            }
            client.getClientHandler().sendFromBot("Done.");

        }
        else if (recvbuf.toLowerCase().startsWith("bancid "))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.bancid)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            StringTokenizer ST = new StringTokenizer(AdcUtils.retNormStr(recvbuf));
            ST.nextToken();
            if (!ST.hasMoreTokens())
            {
                done = true;
                return;
            }
            String aux = ST.nextToken(); //the thing to Ban;
            aux = AdcUtils.retADCStr(aux);
            //al right,now must check if that is a nick, cid or ip
            //first if its a cid...
            String reason = "";
            while (ST.hasMoreTokens())
            {
                reason = reason + ST.nextToken() + " ";
            }

            if (!reason.equals(""))

            {
                reason = reason.substring(0, reason.length() - 1);
            }
            reason = AdcUtils.retADCStr(reason);
            // System.out.println (reason);
            if (AdcUtils.isCID(aux))
            {
                //ok if we got here it really is a CID so:
                // boolean banned=false;
                for (Client temp : SessionManager.getUsers())
                {
                    if (temp.getClientHandler().validated == 1)
                    {
                        if ((temp.getClientHandler().ID.toLowerCase().equals(aux
                                                                             .toLowerCase())))
                        {
                            if (!temp.getClientHandler().reg.kickable)
                            {
                                client.getClientHandler().sendFromBot("Searching...");
                                client.getClientHandler().sendFromBot("Found CID " + aux
                                                                      +
                                                                      " belonging to" +
                                                                      temp.getClientHandler().NI
                                                                      +
                                                                      ", but is not kickable.");
                            }

                            else
                            {

                                client.getClientHandler().sendFromBot("Searching...");
                                client.getClientHandler().sendFromBot("Found CID " + aux
                                                                      + ", banning..");
                                temp.kickMeOut(client.getClientHandler(), reason, 3, -1L);
                                return;
                            }
                        }
                    }
                }
                if ((AccountsConfig.getnod(aux)) == null)
                {
                    client.getClientHandler().sendFromBot("Searching...");
                    client.getClientHandler()
                            .sendFromBot("Found CID " + aux + ", banning....");

                    BanList.addban(3, aux, -1, client.getClientHandler().NI, reason);
                }
                else if (!(AccountsConfig.getnod(aux).kickable))
                {
                    client.getClientHandler().sendFromBot("Searching...");
                    client.getClientHandler().sendFromBot("Found CID " + aux + " belonging to"
                                                          + AccountsConfig.getnod(aux).LastNI
                                                          + ", but is not kickable.");
                }
                else
                {
                    client.getClientHandler().sendFromBot("Searching...");
                    client.getClientHandler()
                            .sendFromBot("Found CID " + aux + ", banning....");

                    BanList.addban(3, aux, -1, client.getClientHandler().NI, reason);
                }

            }
            else
            {
                //ok its not a cid, lets check if its some IP address...
                client.getClientHandler().sendFromBot("Not a CID, Searching for a nick...");
                for (Client temp : SessionManager.getUsers())
                {
                    if (temp.getClientHandler().validated == 1)
                    {
                        if ((temp.getClientHandler().NI.toLowerCase().equals(aux
                                                                             .toLowerCase())))
                        {
                            if (!(temp.getClientHandler().reg.kickable))
                            {
                                client.getClientHandler().sendFromBot("Found user "
                                                                      +
                                                                      temp.getClientHandler().NI +
                                                                      " with CID "
                                                                      +
                                                                      temp.getClientHandler().ID
                                                                      +
                                                                      ", but its unkickable.");
                            }
                            else
                            {
                                //BanList.addban (3,temp.ID,-1,handler.NI,reason);
                                client.getClientHandler().sendFromBot("Found user " + aux
																			  + ", banning..");
                                temp.kickMeOut(client.getClientHandler(), reason, 3, -1L);
                                client.getClientHandler().sendFromBot("Done.");
                                return;
                            }
                        }
                    }
                }

                client.getClientHandler().sendFromBot("No user found with nick " + aux
                                                      + ". Not banned.");

            }//end catch
            client.getClientHandler().sendFromBot("Done.");

        }
        else if (recvbuf.toLowerCase().startsWith("bannick "))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.bannick)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                return;
            }
            StringTokenizer ST = new StringTokenizer(AdcUtils.retNormStr(recvbuf));
            ST.nextToken();
            if (!ST.hasMoreTokens())
            {
                done = true;
                return;
            }
            String aux = ST.nextToken(); //the thing to Ban;
            aux = AdcUtils.retADCStr(aux);
            //al right,now must check if that is a nick online of offline

            String reason = "";
            while (ST.hasMoreTokens())
            {
                reason = reason + ST.nextToken() + " ";
            }

            if (!reason.equals(""))

            {
                reason = reason.substring(0, reason.length() - 1);
            }
            reason = AdcUtils.retADCStr(reason);
            for (Client temp : SessionManager.getUsers())
            {
                if (temp.getClientHandler().validated == 1)
                {
                    if ((temp.getClientHandler().NI.toLowerCase().equals(aux
                                                                         .toLowerCase())))
                    {
                        if (!temp.getClientHandler().reg.kickable)
                        {
                            client.getClientHandler().sendFromBot("Searching...");
                            client.getClientHandler().sendFromBot("Found Nick " + aux
                                                                  +
                                                                  " but it belongs to an unkickable reg.");
                        }
                        else
                        {
                            //BanList.addban (1,aux,-1,handler.NI,reason);
                            client.getClientHandler().sendFromBot("Searching...");
                            client.getClientHandler().sendFromBot("Found Nick " + aux
                                                                  + ", banning..");

                            temp.kickMeOut(client.getClientHandler(), reason, 1, -1L);
                            client.getClientHandler().sendFromBot("Done.");
                            return;

                        }
                    }
                }
            }

            client.getClientHandler().sendFromBot("Searching...");
            client.getClientHandler().sendFromBot("Found Nick " + aux + ", banning....");
            BanList.addban(1, aux, -1, client.getClientHandler().NI, reason);

            client.getClientHandler().sendFromBot("Done.");
        }

        else if (recvbuf.toLowerCase().startsWith("banip "))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.banip)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                return;
            }
            StringTokenizer ST = new StringTokenizer(AdcUtils.retNormStr(recvbuf));
            ST.nextToken();
            if (!ST.hasMoreTokens())
            {
                done = true;
                return;
            }
            String aux = ST.nextToken(); //the thing to Ban;
            aux = AdcUtils.retADCStr(aux);
            //al right,now must check if that is a  ip or nick
            //first if its a ip...
            String reason = "";
            while (ST.hasMoreTokens())
            {
                reason = reason + ST.nextToken() + " ";
            }
            if (!reason.equals(""))

            {
                reason = reason.substring(0, reason.length() - 1);
            }
            reason = AdcUtils.retADCStr(reason);
            if (AdcUtils.isIP(aux))
            {
                //ok if we got here it really is a IP so:

                for (Client temp : SessionManager.getUsers())
                {
                    if (temp.getClientHandler().validated == 1)
                    {
                        if (temp.getClientHandler().realIP.equals(aux))
                        {
                            if (!temp.getClientHandler().reg.kickable)
                            {
                                client.getClientHandler().sendFromBot("Searching...");
                                client.getClientHandler().sendFromBot("Found IP " + aux
                                                                      +
                                                                      " belonging to " +
                                                                      temp.getClientHandler().NI
                                                                      +
                                                                      ", but its unkickable. Not banned.");
                                client.getClientHandler().sendFromBot("Done.");
                                done = true;
                                return;
                            }
                        }
                    }

                }

                int kickedsome = 0;
                for (Client temp : SessionManager.getUsers())
                {
                    if (temp.getClientHandler().validated == 1)
                    {
                        if (temp.getClientHandler().realIP.equals(aux))
                        {

                            temp.kickMeOut(client.getClientHandler(), reason, 2, -1L);
                        }
                    }

                }

                if (kickedsome == 0)
                {
                    client.getClientHandler().sendFromBot("Searching...");
                    client.getClientHandler().sendFromBot("Found IP " + aux + ", banning....");
                    BanList.addban(2, aux, -1, client.getClientHandler().NI, reason);
                }

            }
            else
            {
                //ok its not a ip, lets check if its some nick...
                client.getClientHandler().sendFromBot("Not a IP, Searching for a nick...");
                for (Client temp : SessionManager.getUsers())
                {
                    if (temp.getClientHandler().validated == 1)
                    {
                        if (temp.getClientHandler().NI.toLowerCase().equals(
                                aux.toLowerCase()))
                        {
                            if (!temp.getClientHandler().reg.kickable)
                            {
                                client.getClientHandler().sendFromBot("Found user "
                                                                      +
                                                                      temp.getClientHandler().NI +
                                                                      " with IP "
                                                                      +
                                                                      temp.getClientHandler().realIP
                                                                      +
                                                                      ", but its unkickable.Not banned.");
                            }
                            else
                            {

                                client.getClientHandler().sendFromBot("Found user " + aux
                                                                      +
                                                                      " with IP " +
                                                                      temp.getClientHandler().realIP
                                                                      +
                                                                      ", banning..");

                                temp.kickMeOut(client.getClientHandler(), reason, 2, -1L);
                                client.getClientHandler().sendFromBot("Done.");
                                return;
                            }
                        }
                    }
                }

                client.getClientHandler().sendFromBot("No user found with nick " + aux
                                                      + ". Not banned.");

            }//end catch
            client.getClientHandler().sendFromBot("Done.");

        }
		//TODO add implementation this command as needed
//        else if (recvbuf.toLowerCase().startsWith("cfg"))
//        {
//            commandOK = 1;
//            if (!client.getClientHandler().reg.myMask.cfg)
//            {
//                client.getClientHandler().sendFromBot("Access denied.");
//                done = true;
//                return;
//            }
//            new CFGConfig(client.getClientHandler(), recvbuf);
//        }
        else if (recvbuf.toLowerCase().startsWith("topic"))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.topic)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            if (recvbuf.toLowerCase().equals("topic"))
            {
                if (!ConfigLoader.HUB_DESCRIPTION.isEmpty())
                {
                    Broadcast.getInstance().broadcast("IINF DE");
                    client.getClientHandler().sendFromBot("Topic \"" + ConfigLoader.HUB_DESCRIPTION
                                                          + "\" deleted.");
                    Broadcast.getInstance().broadcast("IMSG Topic was deleted by " + client.getClientHandler().NI, client);
                }
                else
                {
                    client.getClientHandler().sendFromBot("There wasn't any topic anyway.");
                }
				//TODO OMFG O_O!
                ConfigLoader.HUB_DESCRIPTION = "";

            }
            else
            {
                String auxbuf = recvbuf.substring(6);

                // Vars.HubDE=Vars.HubDE.replaceAll("\\ "," ");
                client.getClientHandler().sendFromBot("Topic changed from \"" + ConfigLoader.HUB_DESCRIPTION
                                                      + "\" " + "to \"" + auxbuf + "\".");
                auxbuf = auxbuf.replaceAll(" ", "\\ ");
                ConfigLoader.HUB_DESCRIPTION = auxbuf;

                Broadcast.getInstance().broadcast("IINF DE" + AdcUtils.retADCStr(auxbuf));
                Broadcast.getInstance().broadcast("IMSG Topic was changed by " + client.getClientHandler().NI
                                                  + " to \"" + ConfigLoader.HUB_DESCRIPTION + "\"");

            }
            // TODO remove save "topic" parameter in old config store.

        }
        else if (recvbuf.toLowerCase().startsWith("port"))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.port)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            //new PortCmd(client.getClientHandler(), recvbuf);
            /*try
               {
               int x=Integer.parseInt(recvbuf.substring(5));
               if(x<1 || x>65000)
               {
               handler.sendFromBot("What kinda port is that ?");
               done=true;
               return;
               }
               handler.sendFromBot("New default port change from "+Vars.Default_Port+" to "+recvbuf.substring(5)+". Restart for settings to take effect.");
               Vars.Default_Port=x;

               Main.server.rewriteconfig();;
               }
               catch(NumberFormatException nfe)
               {
                   handler.sendFromBot("Invalid port number");
               }*/
        }
        else if (recvbuf.toLowerCase().equals("usercount"))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.usercount)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            int i = 0, j = 0;
            for (Client temp : SessionManager.getUsers())
            {
                if (temp.getClientHandler().validated == 1)
                {
                    i++;
                }
                else
                {
                    j++;
                }

            }
            client.getClientHandler().sendFromBot("Current user count: " + Integer.toString(i)
                                                  +
                                                  ". In progress users: " +
                                                  Integer.toString(j) +
                                                  ".");
        }
        else if (recvbuf.toLowerCase().equals("about"))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.about)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            client.getClientHandler().sendFromBot("\n"
														  +
														  bigTextManager.getABOUT()
																  .replaceAll(" ", "\\ ")
																  .replaceAll("\\x0a",
																			  "\\\n"));
        }
        else if (recvbuf.toLowerCase().equals("history"))
        {
            // TODO rewrite to normal chat message history returning
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.history)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            String resultMessage = "History:\n" + Broadcast.history.toString();

            client.getClientHandler().sendFromBot(resultMessage.substring(0, resultMessage.length() - 1));
        }
        else if (recvbuf.toLowerCase().equals("cmdhistory"))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.cmdhistory)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            String blah00 = "Command History:\n";

            for (String historyCommand : history)
            {
                blah00 = blah00 + AdcUtils.retNormStr(historyCommand);
            }

            client.getClientHandler().sendFromBot(blah00.substring(0, blah00.length() - 1));
        }
        else if (recvbuf.toLowerCase().equals("stats"))
        {
            commandOK = 1;
            if (!client.getClientHandler().reg.myMask.stats)
            {
                client.getClientHandler().sendFromBot("Access denied.");
                done = true;
                return;
            }
            Runtime myRun = Runtime.getRuntime();

            int i = 0, j = 0;
            for (Client client : SessionManager.getUsers())
            {
                if (client.getClientHandler().validated == 1)
                {
                    i++;
                }
                else
                {
                    j++;
                }

            }

            long up = System.currentTimeMillis() - Main.curtime; //uptime in millis
            String blah = "Death Squad Hub. Version "
                          + ConfigLoader.HUB_VERSION
                          + ".\n"
                          + "  Running on "
                          + Main.proppies.getProperty("os.name")
                          + " Version "
                          + Main.proppies.getProperty("os.version")
                          + " on Architecture "
                          + Main.proppies.getProperty("os.arch")
                          + "\n"
                          + "  Java Runtime Environment "
                          + Main.proppies.getProperty("java.version")
                          + " from "
                          + Main.proppies.getProperty("java.vendor")
                          + "\n"
                          + "  Java Virtual Machine "
                          + Main.proppies
                    .getProperty("java.vm.specification.version")
                          + "\n" + "  Available CPU's to JVM "
                          + Integer.toString(myRun.availableProcessors()) + "\n"
                          + "  Available Memory to JVM: "
                          + Long.toString(myRun.maxMemory()) + " Bytes, where free: "
                          + Long.toString(myRun.freeMemory()) + " Bytes\n"
                          + "Hub Statistics:\n" + "  Online users: "
                          + Integer.toString(i) + "\n" + "  Connecting users: "
                          + Integer.toString(j) + "\n" + "  Uptime: "
                          + TimeConv.getStrTime(up) //+//+ "\n  Bytes read per second: "
                    //	+ Main.server.IOSM.getTotalByteReadThroughput()
                    //	    "\n  Bytes read per second: "+Main.server.acceptor.getReadBytesThroughput()+
                    //    "\n  Bytes written per second: "+Main.server.acceptor.getWrittenBytesThroughput()

                    //+ "\n  Bytes written per second: "
                    //+ Main.server.IOSM.getTotalByteWrittenThroughput()

                    ;

            client.getClientHandler().sendFromBot("" + blah);
        }
        else if (recvbuf.equals(""))
        {
            commandOK = 1;
        }

        done = true;

    }

}
