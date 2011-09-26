package ru.sincore;
/*
 * Command.java
 *
 * Created on 06 martie 2007, 16:20
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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.Modules.Modulator;
import ru.sincore.Modules.Module;
import ru.sincore.ProtoCmds.*;
import ru.sincore.TigerImpl.Base32;
import ru.sincore.TigerImpl.Tiger;
import ru.sincore.adc.State;
import ru.sincore.banning.BanList;
import ru.sincore.i18n.Messages;
import ru.sincore.util.AdcUtils;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

import java.util.StringTokenizer;

/**
 * Provides a parsing for each AdcUtils command received from client, and makes the states transitions
 * Updates all information and ensures stability.
 *
 * @author Eugen Hristev
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-06
 */

public class Command
{
	private static final Logger log = LoggerFactory.getLogger(Command.class);

    Client currentClient;
    String command;
    int    state;
	private BigTextManager bigTextManager = new BigTextManager();


    private void sendUsersInfs()
    {
        for (Client client : ClientManager.getInstance().getClients())
        {
            if (client.getClientHandler().validated == 1 && !client.equals(currentClient))
            {
                currentClient.getClientHandler()
                             .sendToClient(client.getClientHandler().getINF());
            }
        }
    }


    private boolean pushUser()
    {
        if (ClientManager.getInstance().containClientByCID(currentClient.getClientHandler().ID))
        {
            Client client = ClientManager.getInstance().getClientByCID(currentClient.getClientHandler().ID);
            client.dropMeImGhost();
        }


        ClientManager.getInstance().addClient(currentClient);
        currentClient.getClientHandler().inside = true;
        return true;
    }


    void completeLogIn()
            throws STAException
    {
        // must check if its op or not and move accordingly
        if (!currentClient.getClientHandler().reg.key) //DO NOT increase HR count and put RG field to 1
        {
            //  handler.HR=String.valueOf(Integer.parseInt(handler.HR)+1);
            currentClient.getClientHandler().CT = "2";
        }
        else //DO NOT increase HO count and put OP field to 1
        {
            //   handler.HO=String.valueOf(Integer.parseInt(handler.HO)+1);
            currentClient.getClientHandler().CT = "4";
        }


        boolean ok = pushUser();

        if (!ok)
        {
            new STAError(currentClient,
                         200 + Constants.STA_CID_TAKEN,
                         "CID taken. Please go to Settings and pick new PID.");
            return;
        }


        //ok now sending infs of all others to the handler
        sendUsersInfs();

        currentClient.getClientHandler().sendToClient("BINF DCBA ID" +
															  ConfigLoader.SECURITY_CID +
															  " NI" +
															  AdcUtils.retADCStr(ConfigLoader.BOT_CHAT_NAME)
															  +
															  " CT5 DE" +
															  AdcUtils.retADCStr(ConfigLoader.BOT_CHAT_DESCRIPTION));
		log.info(ConfigLoader.SECURITY_CID);
        currentClient.getClientHandler().putOpchat(true);
        currentClient.getClientHandler().sendToClient(currentClient.getClientHandler().getINF());  //sending inf about itself too



        //ok now must send INF to all clientsByCID
        Broadcast.getInstance().broadcast(currentClient.getClientHandler().getINF(), currentClient);
        currentClient.getClientHandler().validated = 1; //user is OK, logged in and cool.
        currentClient.getClientHandler().reg.LastLogin = System.currentTimeMillis();
        currentClient.getClientHandler().sendFromBot(bigTextManager.getMOTD(currentClient));
        currentClient.getClientHandler().sendFromBot(currentClient.getClientHandler().reg.HideMe ? "You are currently hidden." : "");

        currentClient.getClientHandler().loggedAt = System.currentTimeMillis();
        currentClient.getClientHandler().state = State.NORMAL;


        /** calling plugins...*/
        for (Module myMod : Modulator.myModules)
        {
            myMod.onConnect(currentClient.getClientHandler());
        }
        //handler.sendFromBot( AdcUtils.MOTD);
        currentClient.getClientHandler().can_receive_cmds = true;


    }


    boolean ValidateField(String str)
    {
        return Main.listaBanate.isOK(str) == -1;
    }


    void handleINF() throws CommandException, STAException
    {

        if (command.length() < 10)
        {
            new STAError(currentClient,
                         100 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Incorrect protocol command");
            return;
        }
        command = command.substring(4);
        StringTokenizer tok = new StringTokenizer(command);

        String cur_inf = "BINF " + currentClient.getClientHandler().SID;

        String thesid = tok.nextToken();
        if (!thesid.equals(currentClient.getClientHandler().SID))
        {
            new STAError(currentClient,
                         200 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Protocol Error.Wrong SID supplied.");
            return;
        }

        tok = new StringTokenizer(command);
        tok.nextToken();

        while (tok.hasMoreElements())
        {
            String aux = tok.nextToken();

            if (aux.startsWith("ID"))//meaning we have the ID thingy
            {

                if (state != State.PROTOCOL)
                {
                    new STAError(currentClient, 100, "Can't change CID while connected.");
                    return;
                }

                currentClient.getClientHandler().ID = aux.substring(2);
                cur_inf = cur_inf + " ID" + currentClient.getClientHandler().ID;
            }
            else if (aux.startsWith("NI"))
            {
                // TODO validate nick
/*
                if (! Nick.validateNick(aux.substring(2)))
                {
                    new STAError(currentClient,
                                 200 + Constants.STA_NICK_INVALID,
                                 "Nick not valid, please choose another");
                    return;
                }
*/

                currentClient.getClientHandler().NI = aux.substring(2);

                if (state != State.PROTOCOL)
                {
                    if (currentClient.getClientHandler().reg.isreg)
                    {
                        currentClient.getClientHandler().reg.LastNI = currentClient.getClientHandler().NI;
                    }
                }

                cur_inf = cur_inf + " NI" + currentClient.getClientHandler().NI;
            }
            else if (aux.startsWith("PD"))//the PiD
            {
                if (state != State.PROTOCOL)
                {
                    new STAError(currentClient, 100, "Can't change PID while connected.");
                    return;
                }

                currentClient.getClientHandler().PD = aux.substring(2);
            }
            else if (aux.startsWith("I4"))
            {
                currentClient.getClientHandler().I4 = aux.substring(2);

                if (aux.substring(2).equals("0.0.0.0") ||
                    aux.substring(2).equals("localhost"))//only if active client
                {
                    currentClient.getClientHandler().I4 = currentClient.getClientHandler().realIP;
                }
                else if (!aux.substring(2).equals(currentClient.getClientHandler().realIP) &&
                         !aux.substring(2).equals("") &&
                         !currentClient.getClientHandler().realIP.equals("127.0.0.1"))
                {
                    new STAError(currentClient,
                                 200 + Constants.STA_INVALID_IP,
                                 "Wrong IP address supplied.",
                                 "I4",
                                 currentClient.getClientHandler().realIP);
                    return;
                }

                cur_inf = cur_inf + " I4" + currentClient.getClientHandler().I4;
            }
            else if (aux.startsWith("I6"))
            {
                currentClient.getClientHandler().I6 = aux.substring(2);
                cur_inf = cur_inf + " I6" + currentClient.getClientHandler().I6;
            }
            else if (aux.startsWith("U4"))
            {
                currentClient.getClientHandler().U4 = aux.substring(2);
                cur_inf = cur_inf + " U4" + currentClient.getClientHandler().U4;
            }
            else if (aux.startsWith("U6"))
            {
                currentClient.getClientHandler().U6 = aux.substring(2);
                cur_inf = cur_inf + " U6" + currentClient.getClientHandler().U6;
            }
            else if (aux.startsWith("SS"))
            {
                currentClient.getClientHandler().SS = aux.substring(2);
                cur_inf = cur_inf + " SS" + currentClient.getClientHandler().SS;
            }
            else if (aux.startsWith("SF"))
            {
                currentClient.getClientHandler().SF = aux.substring(2);
                cur_inf = cur_inf + " SF" + currentClient.getClientHandler().SF;
            }
            else if (aux.startsWith("VE"))
            {
                currentClient.getClientHandler().VE = aux.substring(2);
                cur_inf = cur_inf + " VE" + currentClient.getClientHandler().VE;
            }
            else if (aux.startsWith("US"))
            {
                currentClient.getClientHandler().US = aux.substring(2);
                cur_inf = cur_inf + " US" + currentClient.getClientHandler().US;
            }
            else if (aux.startsWith("DS"))
            {
                currentClient.getClientHandler().DS = aux.substring(2);
                cur_inf = cur_inf + " DS" + currentClient.getClientHandler().DS;
            }
            else if (aux.startsWith("SL"))
            {
                currentClient.getClientHandler().SL = aux.substring(2);
                cur_inf = cur_inf + " SL" + currentClient.getClientHandler().SL;
            }
            else if (aux.startsWith("AS"))
            {
                currentClient.getClientHandler().AS = aux.substring(2);
                cur_inf = cur_inf + " AS" + currentClient.getClientHandler().AS;
            }
            else if (aux.startsWith("AM"))
            {
                currentClient.getClientHandler().AM = aux.substring(2);
                cur_inf = cur_inf + " AM" + currentClient.getClientHandler().AM;
            }
            else if (aux.startsWith("EM"))
            {
                currentClient.getClientHandler().EM = aux.substring(2);
                cur_inf = cur_inf + " EM" + currentClient.getClientHandler().EM;
            }

            else if (aux.startsWith("DE"))
            {
                currentClient.getClientHandler().DE = aux.substring(2);
                cur_inf = cur_inf + " DE" + currentClient.getClientHandler().DE;
            }
            else if (aux.startsWith("HN"))
            {
                currentClient.getClientHandler().HN = aux.substring(2);

                if (state == State.NORMAL)
                {
                    cur_inf = cur_inf + " HN" + currentClient.getClientHandler().HN;
                }
            }
            else if (aux.startsWith("HR"))
            {
                currentClient.getClientHandler().HR = aux.substring(2);
                cur_inf = cur_inf + " HR" + currentClient.getClientHandler().HR;
            }
            else if (aux.startsWith("HO"))
            {
                currentClient.getClientHandler().HO = aux.substring(2);
                cur_inf = cur_inf + " HO" + currentClient.getClientHandler().HO;
            }
            else if (aux.startsWith("TO"))
            {
                currentClient.getClientHandler().TO = aux.substring(2);
                cur_inf = cur_inf + " TO" + currentClient.getClientHandler().TO;
            }

            else if (aux.startsWith("AW"))
            {
                currentClient.getClientHandler().AW = aux.substring(2);
                cur_inf = cur_inf + " AW" + currentClient.getClientHandler().AW;
            }
            else if (aux.startsWith("CT"))
            {
                if (currentClient.getClientHandler().reg.overridespam)
                {
                    currentClient.getClientHandler().CT = aux.substring(2);
                    cur_inf = cur_inf + " CT" + currentClient.getClientHandler().CT;
                }
                else
                {
                    new STAError(currentClient,
                                 200 + Constants.STA_GENERIC_LOGIN_ERROR,
                                 "Not allowed to have CT field.");
                    return;
                }
            }
            else if (aux.startsWith("HI"))
            {

                currentClient.getClientHandler().HI = aux.substring(2);
                cur_inf = cur_inf + " HI" + currentClient.getClientHandler().HI;

            }

            else if (aux.startsWith("SU"))
            {
                currentClient.getClientHandler().SU = aux.substring(2);
                cur_inf = cur_inf + " SU" + currentClient.getClientHandler().SU;
            }
            else
            {
                //new STAError(handler,200+Constants.STA_GENERIC_PROTOCOL_ERROR,"Protocol Error.");
                //  return ;
                cur_inf = cur_inf + " " + aux;
            }
        }
        if (state == State.PROTOCOL)
        {
            if (currentClient.getClientHandler().ID == null)
            {
                new STAError(currentClient,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "ID");
                return;
            }
            else if (currentClient.getClientHandler().ID.equals(""))
            {
                new STAError(currentClient,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "ID");
                return;
            }
            if (currentClient.getClientHandler().PD == null)
            {
                new STAError(currentClient,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "PD");
                return;
            }
            else if (currentClient.getClientHandler().PD.equals(""))
            {
                new STAError(currentClient,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "PD");
                return;
            }

            if (currentClient.getClientHandler().NI == null)
            {
                new STAError(currentClient,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "NI");
                return;
            }
            else if (currentClient.getClientHandler().NI.equals(""))
            {
                new STAError(currentClient,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "NI");
                return;
            }
            if (currentClient.getClientHandler().HN == null)
            {
                new STAError(currentClient,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "HN");
                return;
            }
            else if (currentClient.getClientHandler().HN.equals(""))
            {
                new STAError(currentClient,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "HN");
                return;
            }

            currentClient.getClientHandler().reg = AccountsConfig.getnod(currentClient.getClientHandler().ID);

            if (currentClient.getClientHandler().reg == null)
            {
                currentClient.getClientHandler().reg = new Nod();
            }
        }


        /* TODO check if user is banned first */
        currentClient.getClientHandler().myban = BanList.getban(3, currentClient.getClientHandler().ID);
        if (currentClient.getClientHandler().myban == null)
        {
            currentClient.getClientHandler().myban = BanList.getban(2, (currentClient.getClientHandler().realIP));
        }
        if (currentClient.getClientHandler().myban == null)
        {
            currentClient.getClientHandler().myban = BanList.getban(1, currentClient.getClientHandler().NI);
        }
        if (currentClient.getClientHandler().myban != null) //banned
        {
            if (currentClient.getClientHandler().myban.time == -1)
            {
                String msg = "Hello there. You are permanently banned.\nOp who banned you: " +
                             currentClient.getClientHandler().myban.banop +
                             "\nReason: " +
                             currentClient.getClientHandler().myban.banreason +
                             "\n" +
							 Messages.BAN_MESSAGE;

                new STAError(currentClient, 200 + Constants.STA_PERMANENTLY_BANNED, msg);
                return;
            }

            long TL = System.currentTimeMillis() -
                      currentClient.getClientHandler().myban.timeofban -
                      currentClient.getClientHandler().myban.time;
            TL = -TL;
            if (TL > 0)
            {
                String msg = "Hello there. You are temporary banned.\nOp who banned you: " +
                             currentClient.getClientHandler().myban.banop +
                             "\nReason: " +
                             currentClient.getClientHandler().myban.banreason +
                             "\nThere are still " +
                             Long.toString(TL / 1000) +
                             " seconds remaining.\n" +
                             Messages.BAN_MESSAGE +
                             " TL" +
                             Long.toString(TL / 1000);

                new STAError(currentClient, 200 + Constants.STA_TEMP_BANNED, msg);
                return;
            }
        }

        // TODO Check nick availability
        for (Client client : ClientManager.getInstance().getClients())
        {

            if (!client.equals(currentClient))
            {
                if (client.getClientHandler().validated == 1)
                {
                    if (client.getClientHandler().NI.toLowerCase().equals(currentClient.getClientHandler().NI.toLowerCase()) &&
                        !client.getClientHandler().ID.equals(currentClient.getClientHandler().ID))
                    {
                        new STAError(currentClient,
                                     200 + Constants.STA_NICK_TAKEN,
                                     "Nick taken, please choose another");
                        return;
                    }
                }
                /* if(state.equals ("PROTOCOL"))
                if(SessionManager.users.containsKey(handler.ID) || temp.handler.ID.equals(handler.ID))//&& temp.handler.CIDsecure)
                {
                    new STAError(handler,200+Constants.STA_CID_TAKEN,"CID taken. Please go to Settings and pick new PID.");
                    return;
                }*/
            }
        }


        if (AccountsConfig.nickReserved(currentClient.getClientHandler().NI, currentClient.getClientHandler().ID))
        {
            int x = (state == State.PROTOCOL) ? 200 : 100;
            new STAError(currentClient,
                         x + Constants.STA_NICK_TAKEN,
                         "Nick reserved. Please choose another.");
            return;
        }
        // now must check if hub is full...
        if (state == State.PROTOCOL) //otherwise is already connected, no point in checking this
        {
            /** must check the hideme var*/
            if (currentClient.getClientHandler().reg.HideMe)
            {
                cur_inf = cur_inf + " HI1";
                currentClient.getClientHandler().HI = "1";
            }


            if (ConfigLoader.MAX_USERS <= ClientManager.getInstance().getClientsCount() &&
                !currentClient.getClientHandler().reg.overridefull)
            {
                new STAError(currentClient,
                             200 + Constants.STA_HUB_FULL,
                             "Hello there. Hub is full, there are " +
                             String.valueOf(ClientManager.getInstance().getClientsCount()) +
                             " users online.\n" +
                             Messages.HUB_FULL_MESSAGE);
                return;
            }
        }

        if (!currentClient.getClientHandler().reg.overridespam)
        {
            if (currentClient.getClientHandler().EM != null)
            {
                if (!ValidateField(currentClient.getClientHandler().EM))
                {
                    new STAError(currentClient,
                                 state == State.PROTOCOL ? 200 : 100,
                                 "E-mail contains forbidden words.");
                    return;
                }
            }
        }
        if (!currentClient.getClientHandler().reg.overridespam)
        {
            if (currentClient.getClientHandler().DE != null)
            {
                if (!ValidateField(currentClient.getClientHandler().DE))
                {
                    new STAError(currentClient,
                                 state == State.PROTOCOL ? 200 : 100,
                                 "Description contains forbidden words");
                    return;
                }
            }
        }

        if (!currentClient.getClientHandler().reg.overridespam)
        {
            if (currentClient.getClientHandler().SS == null && ConfigLoader.MIN_SHARE_SIZE != 0)
            {
                new STAError(currentClient,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Share too small, " + ConfigLoader.MIN_SHARE_SIZE + " MiB required.",
                             "FB",
                             "SS");
            }
        }
        if (!currentClient.getClientHandler().reg.overridespam)
        {
            if (currentClient.getClientHandler().SL == null && ConfigLoader.MIN_SLOT_COUNT != 0)
            {
                new STAError(currentClient,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Too few slots, open up more.",
                             "FB",
                             "SL");
            }
        }
        //TODO : add without tag allow ?
        try
        {
            //checking all:
            if (!currentClient.getClientHandler().reg.overridespam)
            {
                if (currentClient.getClientHandler().NI.length() > ConfigLoader.MAX_NICK_SIZE)
                {
                    new STAError(currentClient,
                                 200 + Constants.STA_NICK_INVALID,
                                 "Nick too large",
                                 "FB",
                                 "NI");
                    return;
                }
            }
            if (!currentClient.getClientHandler().reg.overridespam)
            {
                if (currentClient.getClientHandler().NI.length() < ConfigLoader.MIN_NICK_SIZE)
                {
                    new STAError(currentClient,
                                 200 + Constants.STA_NICK_INVALID,
                                 "Nick too small",
                                 "FB",
                                 "NI");
                    return;
                }
            }
            if (!currentClient.getClientHandler().reg.overridespam)
            {
                if (currentClient.getClientHandler().DE != null)
                {
                    if (currentClient.getClientHandler().DE.length() > ConfigLoader.MAX_DESCRIPTION_CHAR_COUNT)
                    {
                        new STAError(currentClient,
                                     200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                     "Description too large",
                                     "FB",
                                     "DE");
                        return;
                    }
                }
            }
            if (!currentClient.getClientHandler().reg.overridespam)
            {
                if (currentClient.getClientHandler().EM != null)
                {
                    if (currentClient.getClientHandler().EM.length() > ConfigLoader.MAX_EMAIL_CHAR_COUNT)
                    {
                        new STAError(currentClient,
                                     200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                     "E-mail too large",
                                     "FB",
                                     "EM");
                        return;
                    }
                }
            }
            if (!currentClient.getClientHandler().reg.overrideshare)
            {
                if (currentClient.getClientHandler().SS != null)
                {
                    if (Long.parseLong(currentClient.getClientHandler().SS) > 1024 * ConfigLoader.MAX_SHARE_SIZE * 1024)
                    {
                        new STAError(currentClient,
                                     200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                     "Share too large",
                                     "FB",
                                     "SS");
                        return;
                    }
                }
            }
            if (!currentClient.getClientHandler().reg.overrideshare)
            {
                if (currentClient.getClientHandler().SS != null)
                {
                    if (Long.parseLong(currentClient.getClientHandler().SS) < 1024 * ConfigLoader.MIN_SHARE_SIZE * 1024)
                    {
                        new STAError(currentClient,
                                     200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                     "Share too small " + ConfigLoader.MIN_SHARE_SIZE + " MiB required.",
                                     "FB",
                                     "SS");
                        return;
                    }
                }
            }
            if (!currentClient.getClientHandler().reg.overrideshare)
            {
                if (currentClient.getClientHandler().SL != null)
                {
                    if (Integer.parseInt(currentClient.getClientHandler().SL) < ConfigLoader.MIN_SLOT_COUNT)
                    {
                        new STAError(currentClient,
                                     200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                     "Too few slots, open up more.",
                                     "FB",
                                     "SL");
                        return;
                    }
                }
            }
            if (!currentClient.getClientHandler().reg.overrideshare)
            {
                if (currentClient.getClientHandler().SL != null)
                {
                    if (Integer.parseInt(currentClient.getClientHandler().SL) > ConfigLoader.MAX_SLOT_COUNT)
                    {
                        new STAError(currentClient,
                                     200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                     "Too many slots, close some.",
                                     "FB",
                                     "SL");
                        return;
                    }
                }
            }
            if (!currentClient.getClientHandler().reg.overridespam)
            {
                if (Integer.parseInt(currentClient.getClientHandler().HN) > ConfigLoader.MAX_HUBS_USERS)
                {
                    new STAError(currentClient,
                                 200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 "Too many hubs open, close some.",
                                 "FB",
                                 "HN");
                    return;
                }
            }
            if (!currentClient.getClientHandler().reg.overridespam)
            {
                if (currentClient.getClientHandler().HO != null)
                {
                    if (Integer.parseInt(currentClient.getClientHandler().HO) > ConfigLoader.MAX_OP_IN_HUB)
                    {
                        new STAError(currentClient,
                                     200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                     "You are operator on too many hubs. Sorry.",
                                     "FB",
                                     "HO");
                        return;
                    }
                }
            }
            if (!currentClient.getClientHandler().reg.overridespam)
            {
                if (currentClient.getClientHandler().HR != null)
                {
                    if (Integer.parseInt(currentClient.getClientHandler().HR) > ConfigLoader.MAX_HUBS_REGISTERED)
                    {
                        new STAError(currentClient,
                                     200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                     "You are registered on too many hubs. Sorry.",
                                     "FB",
                                     "HR");
                        return;
                    }
                }
            }
        }
        catch (NumberFormatException nfe)
        {
            new STAError(currentClient,
                         200 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Your client sent weird info, Protocol Error.");
            return;
        }

        if (currentClient.getClientHandler().ID.equals(ConfigLoader.OP_CHAT_CID))
        {
            new STAError(currentClient,
                         200 + Constants.STA_CID_TAKEN,
                         "CID taken. Please go to Settings and pick new PID.");
            return;
        }
        if (currentClient.getClientHandler().ID.equals(ConfigLoader.SECURITY_CID))
        {
            new STAError(currentClient,
                         200 + Constants.STA_CID_TAKEN,
                         "CID taken. Please go to Settings and pick new PID.");
            return;
        }
        if (currentClient.getClientHandler().NI.equalsIgnoreCase(ConfigLoader.OP_CHAT_NAME))
        {
            new STAError(currentClient,
                         200 + Constants.STA_NICK_TAKEN,
                         "Nick taken, please choose another");
            return;
        }
        if (currentClient.getClientHandler().NI.equalsIgnoreCase(ConfigLoader.BOT_CHAT_NAME))
        {
            new STAError(currentClient,
                         200 + Constants.STA_NICK_TAKEN,
                         "Nick taken, please choose another");
            return;
        }

        if (state == State.PROTOCOL)
        {
            try
            {
                Tiger myTiger = new Tiger();

                myTiger.engineReset();
                myTiger.init();
                byte[] bytepid = Base32.decode(currentClient.getClientHandler().PD);


                myTiger.engineUpdate(bytepid, 0, bytepid.length);

                byte[] finalTiger = myTiger.engineDigest();
                if (!Base32.encode(finalTiger).equals(currentClient.getClientHandler().ID))
                {
                    new STAError(currentClient,
                                 200 + Constants.STA_GENERIC_LOGIN_ERROR,
                                 "Invalid CID check.");
                    return;
                }
                if (currentClient.getClientHandler().PD.length() != 39)
                {
                    throw new IllegalArgumentException();
                }
            }


            catch (IllegalArgumentException iae)
            {
                new STAError(currentClient, 200 + Constants.STA_INVALID_PID, "Invalid PID supplied.");
                return;
            }
            catch (Exception e)
            {
                System.out.println(e);
                return;
            }
        }


        if (currentClient.getClientHandler().bas0 && currentClient.getClientHandler().base != 2)
        {
            new STAError(currentClient,
                         200 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Your client uses a very old AdcUtils version. Please update in order to connect to this hub. You can get a new version usually by visiting the developer's webpage from Help/About menu.");
        }


        if (currentClient.getClientHandler().SU != null)
        {
            if (!(currentClient.getClientHandler().SU.equals("")))
            {
                if (currentClient.getClientHandler().SU.contains("TCP4"))
                {
                    currentClient.getClientHandler().active = 1;
                }
                else
                {
                    currentClient.getClientHandler().active = 0;
                }
            }
        }
        /*------------ok now must see if the pid is registered...---------------*/

        if (state == State.PROTOCOL)
        {
            if (currentClient.getClientHandler().reg.isreg)
            {
                if (currentClient.getClientHandler().reg.Password.equals(""))//no pass defined ( yet)
                {
                    currentClient.getClientHandler().sendToClient(
							"ISTA 000 Registered,\\sno\\spassword\\srequired.\\sThough,\\sits\\srecomandable\\sto\\sset\\sone.");
                    currentClient.getClientHandler().sendToClient("ISTA 000 Authenticated.");


                    currentClient.getClientHandler().reg.LastNI = currentClient.getClientHandler().NI;
                    currentClient.getClientHandler().reg.LastIP = currentClient.getClientHandler().realIP;
                    completeLogIn();
                    return;
                }
                currentClient.getClientHandler().sendToClient("ISTA 000 Registered,\\stype\\syour\\spassword.");
                /* creates some hash for the GPA random data*/
                Tiger myTiger = new Tiger();

                myTiger.engineReset();
                myTiger.init();
                byte[] T =
                        Long.toString(System.currentTimeMillis()).getBytes(); //taken from cur time
                myTiger.engineUpdate(T, 0, T.length);

                byte[] finalTiger = myTiger.engineDigest();
                currentClient.getClientHandler().encryptionSolt = Base32.encode(finalTiger);
                currentClient.getClientHandler().sendToClient("IGPA " + currentClient.getClientHandler().encryptionSolt);
                currentClient.getClientHandler().state = State.VERIFY;
                return;
            }
            else
            {
                Nod k;
                k = AccountsConfig.isNickRegFl(currentClient.getClientHandler().NI);
                if (k != null)
                {
                    currentClient.getClientHandler().sendToClient(
							"ISTA 000 Nick\\sRegistered\\s(flyable\\saccount).\\sPlease\\sprovide\\spassword.");

                    /* creates some hash for the GPA random data*/
                    Tiger myTiger = new Tiger();

                    myTiger.engineReset();
                    myTiger.init();
                    byte[] T = Long.toString(System.currentTimeMillis())
                                   .getBytes(); //taken from cur time
                    myTiger.engineUpdate(T, 0, T.length);

                    byte[] finalTiger = myTiger.engineDigest();
                    currentClient.getClientHandler().encryptionSolt = Base32.encode(finalTiger);
                    currentClient.getClientHandler().sendToClient("IGPA " + currentClient.getClientHandler().encryptionSolt);
                    currentClient.getClientHandler().reg = k;
                    currentClient.getClientHandler().state = State.VERIFY;
                    return;
                }
                else if (ConfigLoader.MARK_REGISTRATION_ONLY)
                {
                    new STAError(currentClient, 200 + Constants.STA_REG_ONLY, "Registered only hub.");
                    return;
                }
            }

        }


        //ok now must send to handler client the inf of all others
        if (state == State.PROTOCOL)
        {
            boolean ok = pushUser();

            if (!ok)
            {
                new STAError(currentClient,
                             200 + Constants.STA_CID_TAKEN,
                             "CID taken. Please go to Settings and pick new PID.");
                return;
            }
            sendUsersInfs();

            currentClient.getClientHandler().sendToClient("BINF DCBA ID" +
																  ConfigLoader.SECURITY_CID +
																  " NI" +
																  AdcUtils.retADCStr(ConfigLoader.BOT_CHAT_NAME)
																  +
																  " CT5 DE" +
																  AdcUtils.retADCStr(ConfigLoader.BOT_CHAT_DESCRIPTION));

            currentClient.getClientHandler().putOpchat(true);
            currentClient.getClientHandler().sendToClient(currentClient.getClientHandler().getINF());  //sending inf about itself too

            //ok now must send INF to all clientsByCID
            Broadcast.getInstance().broadcast(currentClient.getClientHandler().getINF(), currentClient);


            if (currentClient.getClientHandler().ucmd == 1)
            {
                //ok, he is ucmd ok, so
                currentClient.getClientHandler().sendToClient("ICMD Test CT1 TTTest");
            }
            currentClient.getClientHandler().state = State.NORMAL;
            currentClient.getClientHandler().validated = 1; //user is OK, logged in and cool.
            currentClient.getClientHandler().sendFromBot(bigTextManager.getMOTD(currentClient));

            /** calling plugins...*/
            for (Module myMod : Modulator.myModules)
            {
                myMod.onConnect(currentClient.getClientHandler());
            }
            return;
        }

        Broadcast.getInstance().broadcast(cur_inf);
    }


    /**
     * Main command handling function, AdcUtils specific.
     */
    void HandleIssuedCommand()
            throws CommandException, STAException
    {


        if (command.length() < 4)
        {
            new STAError(currentClient,
                         100 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Incorrect command");
        }
        /*******************************INF COMMAND *****************************************/

        if (command.substring(1).startsWith("INF"))
        {

            //if (state.equals("IDENTIFY") || state.equals("VERIFY"))
            if ((state & (State.IDENTIFY | State.VERIFY)) != State.INVALID_STATE)
            {
                new STAError(currentClient,
                             200 + Constants.STA_INVALID_STATE,
                             "INF Invalid state.",
                             "FC",
                             command.substring(0, 4));
                return;
            }


            if (command.charAt(0) != 'B')
            {
                new STAError(currentClient, 100, "INF Invalid Context.");
                return;
            }
            if (!currentClient.getClientHandler().reg.overridespam)
            {
                switch (command.charAt(0))
                {
                    case 'B':
                        if (ConfigLoader.ADC_BINF != 1)
                        {
                            new STAError(currentClient, 100, "INF Invalid Context B");
                            return;
                        }
                        break;
                    case 'E':
                        if (ConfigLoader.ADC_EINF != 1)
                        {
                            new STAError(currentClient, 100, "INF Invalid Context E");
                            return;
                        }
                        break;
                    case 'D':
                        if (ConfigLoader.ADC_DINF != 1)
                        {
                            new STAError(currentClient, 100, "INF Invalid Context D");
                            return;
                        }
                        break;
                    case 'F':
                        if (ConfigLoader.ADC_FINF != 1)
                        {
                            new STAError(currentClient, 100, "INF Invalid Context F");
                            return;
                        }
                        break;
                    case 'H':
                        if (ConfigLoader.ADC_HINF != 1)
                        {
                            new STAError(currentClient, 100, "INF Invalid Context H");
                            return;
                        }

                }
            }


            handleINF();

        }

        /************************PAS COMMAND****************************/
        if (command.substring(1).startsWith("PAS"))
        {

            if (!currentClient.getClientHandler().reg.overridespam)
            {
                switch (command.charAt(0))
                {
                    case 'B':
                        if (ConfigLoader.ADC_BPAS != 1)
                        {
                            new STAError(currentClient, 100, "PAS Invalid Context B");
                            return;
                        }
                        break;
                    case 'E':
                        if (ConfigLoader.ADC_EPAS != 1)
                        {
                            new STAError(currentClient, 100, "PAS Invalid Context E");
                            return;
                        }
                        break;
                    case 'D':
                        if (ConfigLoader.ADC_DPAS != 1)
                        {
                            new STAError(currentClient, 100, "PAS Invalid Context D");
                            return;
                        }
                        break;
                    case 'F':
                        if (ConfigLoader.ADC_FPAS != 1)
                        {
                            new STAError(currentClient, 100, "PAS Invalid Context F");
                            return;
                        }
                        break;
                    case 'H':
                        if (ConfigLoader.ADC_HPAS != 1)
                        {
                            new STAError(currentClient, 100, "PAS Invalid Context H");
                            return;
                        }

                }
            }
            Nod k;

            if ((k = AccountsConfig.isNickRegFl(currentClient.getClientHandler().NI)) != null)
            {
                currentClient.getClientHandler().reg = k;
            }
            if (!currentClient.getClientHandler().reg.isreg)
            {
                new STAError(currentClient, 100, "Not registered.");
                return;
            }
            if (command.charAt(0) != 'H')
            {
                if (state == State.NORMAL)
                {
                    throw new CommandException("FAIL state:PROTOCOL reason:NOT BASE CLIENT");
                }
                else
                {
                    new STAError(currentClient, 100, "PAS Invalid Context.");
                    return;
                }
            }

            String realpas = "";
            try
            {
                Tiger myTiger = new Tiger();

                myTiger.engineReset();
                myTiger.init();
                // removed old adc support;
                //  byte [] bytecid=Base32.decode (handler.ID);
                byte[] pas = currentClient.getClientHandler().reg.Password.getBytes();
                byte[] random = Base32.decode(currentClient.getClientHandler().encryptionSolt);

                byte[] result = new byte[pas.length + random.length];
                //for(int i=0;i<bytecid.length;i++)
                //   result[i]=bytecid[i];

                System.arraycopy(pas,    0, result,          0, pas.length   );
                System.arraycopy(random, 0, result, pas.length, random.length);

                myTiger.engineUpdate(result, 0, result.length);

                byte[] finalTiger = myTiger.engineDigest();
                realpas = Base32.encode(finalTiger);

            }


            catch (IllegalArgumentException iae)
            {
                System.out.println(iae);
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
            if (realpas.equals(command.substring(5)))
            {
                currentClient.getClientHandler().sendToClient("IMSG Authenticated.");

                currentClient.getClientHandler().sendFromBot(bigTextManager.getMOTD(currentClient));

                //System.out.println ("pwla");
                currentClient.getClientHandler().reg.LastNI = currentClient.getClientHandler().NI;
                // handler.reg.LastNI=handler.NI;
                currentClient.getClientHandler().reg.LastIP = currentClient.getClientHandler().realIP;

                if (!currentClient.getClientHandler().ID.equals(currentClient.getClientHandler().reg.CID))
                {
                    currentClient.getClientHandler().sendToClient("IMSG Account\\sCID\\supdated\\sto\\s" + currentClient.getClientHandler().ID);
                }
                currentClient.getClientHandler().reg.CID = currentClient.getClientHandler().ID;
            }
            else
            {
                new STAError(currentClient, 200 + Constants.STA_INVALID_PASSWORD, "Invalid Password.");
                return;
            }

            //System.out.println (command);
            completeLogIn();
        }

        /**********************SUP COMMAND******************************/
        if (command.substring(1).startsWith("SUP"))
        {
            new SUP(currentClient, State.toString(state), command);
        }

        /********************************MSG COMMAND************************************/
        if (command.substring(1).startsWith("MSG"))
        {
            new MSG(currentClient, State.toString(state), command);
        }

        if (command.substring(1).startsWith("SCH"))
        {
            new SCH(currentClient, State.toString(state), command);
        }

        if (command.substring(1).startsWith("STA"))
        {
            new STA(currentClient, State.toString(state), command);
        }

        if (command.substring(1).startsWith("RES ")) //direct search result, only active to passive must send this
        {
            new RES(currentClient, State.toString(state), command);
        }
        else if (command.substring(1).startsWith("CTM ")) //direct connect to me
        {
            new CTM(currentClient, State.toString(state), command);
        }
        else if (command.substring(1).startsWith("RCM ")) //reverse connect to me
        {
            new RCM(currentClient, State.toString(state), command);
        }


        /** calling plugins...*/

        for (Module myMod : Modulator.myModules)
        {
            myMod.onRawCommand(currentClient.getClientHandler(), command);
        }
    }


    /**
     * Creates a new instance of Command with following params
     * CH of type ClientHandler identifies tha client to handle
     * @param client
     * @param command      Issued_command of String type actually identifies the given command
     *                      state also of type String Identifies tha state in which tha connection is,
     *                      meaning [ accordingly to arne's draft]:
     *                      PROTOCOL (feature support discovery), IDENTIFY (user identification, static checks),
     *                      VERIFY (password check), NORMAL (normal operation) and DATA (for binary transfers).
     *                      Calling function should send one of this params, that is calling function
     *                      request... Command class does not check params.
     * @throws CommandException Something wrong happend
     * @throws STAException
     */
    public Command(Client client, String command)
            throws STAException, CommandException
    {
        log.debug("Incoming command parser thread = " + Thread.currentThread().getName());
        currentClient = client;


        if (command.equals(""))
        {
            log.debug("Empty command from client with nick = \'" + client.getClientHandler().NI +
                      "\' and SID = \'" + client.getClientHandler().SID + "\'");
            return;
        }

        this.command = command;
        state = currentClient.getClientHandler().state;
        HandleIssuedCommand();
    }

}
