package ru.sincore;
/*
 * Command.java
 *
 * Created on 06 martie 2007, 16:20
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


import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.Modules.Modulator;
import ru.sincore.Modules.Module;
import ru.sincore.ProtoCmds.CTM;
import ru.sincore.ProtoCmds.MSG;
import ru.sincore.ProtoCmds.RCM;
import ru.sincore.ProtoCmds.RES;
import ru.sincore.ProtoCmds.SCH;
import ru.sincore.ProtoCmds.STA;
import ru.sincore.ProtoCmds.SUP;
import ru.sincore.TigerImpl.Base32;
import ru.sincore.TigerImpl.Tiger;
import ru.sincore.banning.BanList;
import ru.sincore.conf.Vars;
import ru.sincore.util.ADC;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

import java.util.*;

/**
 * Provides a parsing for each ADC command received from client, and makes the states transitions
 * Updates all information and ensures stability.
 *
 * @author Eugen Hristev
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-06
 */

public class Command
{
    Client currentClient;
    String command;
    String state;


    private void sendUsersInfs()
    {
        for (Client client : SessionManager.getUsers())
        {
            if (client.getClientHandler().userok == 1 && client.getClientHandler() != currentClient)
            {
                currentClient.sendToClient(client.getClientHandler().getINF());
            }


        }
        // if(!(Infs.equals("")))
        //handler.sendToClient(Infs);
    }


    private boolean pushUser()
    {
        // boolean ok=false;
        synchronized (SessionManager.Users)
        {
            // System.out.println("marimea este "+SessionManager.Users.size());
            if (SessionManager.Users.containsKey(currentClient.ID))
            {
                Client ch = SessionManager.Users.get(currentClient.ID);
                ch.dropMeImGhost();
            }


            SessionManager.Users.put(currentClient.ID, currentClient.myNod);
            currentClient.inside = true;
            //  ok=true;
            //System.out.println("a intrat "+handler.ID+", marimea este "+SessionManager.Users.size());


        }
        return true;
    }


    void completeLogIn()
            throws STAException
    {
        // must check if its op or not and move accordingly
        if (!currentClient.reg.key) //DO NOT increase HR count and put RG field to 1
        {
            //  handler.HR=String.valueOf(Integer.parseInt(handler.HR)+1);
            currentClient.CT = "2";
        }
        else //DO NOT increase HO count and put OP field to 1
        {
            //   handler.HO=String.valueOf(Integer.parseInt(handler.HO)+1);
            currentClient.CT = "4";
        }


        boolean ok = pushUser();

        if (!ok)
        {
            new STAError(currentClient,
                         200 + Constants.STA_CID_TAKEN,
                         "CID taken. Please go to Settings and pick new PID.");
            return;
        }


        //ok now must send to handler the inf of all others


        /*  IoSession [] x= Main.Server.SM.getSessions().toArray(new IoSession[0]);
      String inf="\n";
          for(int i=0;i<x.length;i++)
     {
          Client tempy=((ClientHandler)(x[i].getAttachment())).myNod;
          if(tempy.handler.userok==1 && !tempy.handler.equals (handler)) //if the user has some inf ... [ meaning he is ok]
                inf=inf.substring(0,inf.length()-1)+tempy.handler.getINF ()+"\n\n";
     }
        */
        sendUsersInfs();

        currentClient.sendToClient("BINF DCBA ID" +
                                Vars.SecurityCid +
                                " NI" +
                                ADC.retADCStr(Vars.bot_name)
                                +
                                " CT5 DE" +
                                ADC.retADCStr(Vars.bot_desc));
        currentClient.putOpchat(true);
        currentClient.sendToClient(currentClient.getINF());  //sending inf about itself too
        //handler.sendToClient(inf);


        //ok now must send INF to all clients
        Broadcast.getInstance().broadcast(currentClient.getINF(), currentClient.myNod);
        currentClient.userok = 1; //user is OK, logged in and cool.
        currentClient.reg.LastLogin = System.currentTimeMillis();
        currentClient.sendFromBot(ADC.MOTD);
        //System.out.println("gay");
        //handler.sendFromBot ("gay");
        currentClient.sendFromBot(currentClient.reg.HideMe ? "You are currently hidden." : "");

        currentClient.LoggedAt = System.currentTimeMillis();
        currentClient.State = "NORMAL";


        /** calling plugins...*/

        for (Module myMod : Modulator.myModules)
        {
            myMod.onConnect(currentClient);
        }
        //handler.sendFromBot( ADC.MOTD);
        currentClient.can_receive_cmds = true;


    }


    boolean ValidateField(String str)
    {
        return Main.listaBanate.isOK(str) == -1;
    }


    void handleINF()
            throws CommandException, STAException
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

        String cur_inf = "BINF " + currentClient.SessionID;

        String thesid = tok.nextToken();
        if (!thesid.equals(currentClient.SessionID))
        {
            new STAError(currentClient,
                         200 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Protocol Error.Wrong SID supplied.");
            return;
        }

        // handler.cur_inf="BINF ADDD EMtest NIbla";
        //command="ADDD NImu DEblah";
        //     synchronized(handler.cur_inf)
        //      {
        //    if(handler.cur_inf!=null)
        //     {
        //     StringTokenizer inftok=new StringTokenizer(handler.cur_inf.substring(9));

        //    while(inftok.hasMoreTokens())
        //    {
        //         String y=inftok.nextToken();
        //         if(command.contains(y.substring(0,2)))
        //        {
        //            handler.cur_inf=handler.cur_inf.substring(0,handler.cur_inf.indexOf(y))+handler.cur_inf.substring(handler.cur_inf.indexOf(y)+y.length());
        //           // inftok=new StringTokenizer(handler.cur_inf);
        //      }

        //   }
        //   command+=handler.cur_inf.substring(9);
        //   }
        tok = new StringTokenizer(command);
        tok.nextToken();
        //    }
        //   if(command.endsWith(" "))
        //         command=command.substring(0,command.length()-1);

        // System.out.println(command);
        while (tok.hasMoreElements())
        {

            String aux = tok.nextToken();


            // System.out.println(aux);
            if (aux.startsWith("ID"))//meaning we have the ID thingy
            {

                if (!state.equals("PROTOCOL"))
                {
                    new STAError(currentClient, 100, "Can't change CID while connected.");
                    return;
                }
                currentClient.ID = aux.substring(2);
                cur_inf = cur_inf + " ID" + currentClient.ID;
                //System.out.println (handler.ID);
            }
            else if (aux.startsWith("NI"))
            {


                if (!Vars.ValidateNick(aux.substring(2)))
                {
                    new STAError(currentClient,
                                 200 + Constants.STA_NICK_INVALID,
                                 "Nick not valid, please choose another");
                    return;
                }
                currentClient.NI = aux.substring(2);

                if (!state.equals("PROTOCOL"))
                {
                    if (currentClient.reg.isreg)
                    {
                        currentClient.reg.LastNI = currentClient.NI;
                    }
                }

                cur_inf = cur_inf + " NI" + currentClient.NI;
            }
            else if (aux.startsWith("PD"))//the PiD
            {


                if (!state.equals("PROTOCOL"))
                {
                    new STAError(currentClient, 100, "Can't change PID while connected.");
                    return;
                }


                currentClient.PD = aux.substring(2);
            }
            else if (aux.startsWith("I4"))
            {

                currentClient.I4 = aux.substring(2);
                if (aux.substring(2).equals("0.0.0.0") ||
                    aux.substring(2).equals("localhost"))//only if active client
                {
                    currentClient.I4 = currentClient.RealIP;
                }


                else if (!aux.substring(2).equals(currentClient.RealIP) && !aux.substring(2).equals("")
                         && !currentClient.RealIP.equals("127.0.0.1"))
                {
                    new STAError(currentClient,
                                 200 + Constants.STA_INVALID_IP,
                                 "Wrong IP address supplied.",
                                 "I4",
                                 currentClient.RealIP);
                    return;
                }
                cur_inf = cur_inf + " I4" + currentClient.I4;

            }
            else if (aux.startsWith("I6"))
            {
                currentClient.I6 = aux.substring(2);
                cur_inf = cur_inf + " I6" + currentClient.I6;
            }
            else if (aux.startsWith("U4"))
            {
                currentClient.U4 = aux.substring(2);
                cur_inf = cur_inf + " U4" + currentClient.U4;
            }
            else if (aux.startsWith("U6"))
            {
                currentClient.U6 = aux.substring(2);
                cur_inf = cur_inf + " U6" + currentClient.U6;
            }
            else if (aux.startsWith("SS"))
            {
                currentClient.SS = aux.substring(2);
                cur_inf = cur_inf + " SS" + currentClient.SS;
            }
            else if (aux.startsWith("SF"))
            {
                currentClient.SF = aux.substring(2);
                cur_inf = cur_inf + " SF" + currentClient.SF;
            }
            else if (aux.startsWith("VE"))
            {
                currentClient.VE = aux.substring(2);
                cur_inf = cur_inf + " VE" + currentClient.VE;
            }
            else if (aux.startsWith("US"))
            {
                currentClient.US = aux.substring(2);
                cur_inf = cur_inf + " US" + currentClient.US;
            }
            else if (aux.startsWith("DS"))
            {
                currentClient.DS = aux.substring(2);
                cur_inf = cur_inf + " DS" + currentClient.DS;
            }
            else if (aux.startsWith("SL"))
            {
                currentClient.SL = aux.substring(2);
                cur_inf = cur_inf + " SL" + currentClient.SL;
            }
            else if (aux.startsWith("AS"))
            {
                currentClient.AS = aux.substring(2);
                cur_inf = cur_inf + " AS" + currentClient.AS;
            }
            else if (aux.startsWith("AM"))
            {
                currentClient.AM = aux.substring(2);
                cur_inf = cur_inf + " AM" + currentClient.AM;
            }
            else if (aux.startsWith("EM"))
            {
                currentClient.EM = aux.substring(2);
                cur_inf = cur_inf + " EM" + currentClient.EM;
            }

            else if (aux.startsWith("DE"))
            {
                currentClient.DE = aux.substring(2);
                cur_inf = cur_inf + " DE" + currentClient.DE;
            }
            else if (aux.startsWith("HN"))
            {
                currentClient.HN = aux.substring(2);

                if (state.equals("NORMAL"))
                {
                    cur_inf = cur_inf + " HN" + currentClient.HN;
                }
            }
            else if (aux.startsWith("HR"))
            {
                currentClient.HR = aux.substring(2);
                cur_inf = cur_inf + " HR" + currentClient.HR;
            }
            else if (aux.startsWith("HO"))
            {
                currentClient.HO = aux.substring(2);
                cur_inf = cur_inf + " HO" + currentClient.HO;
            }
            else if (aux.startsWith("TO"))
            {
                currentClient.TO = aux.substring(2);
                cur_inf = cur_inf + " TO" + currentClient.TO;
            }

            else if (aux.startsWith("AW"))
            {
                currentClient.AW = aux.substring(2);
                cur_inf = cur_inf + " AW" + currentClient.AW;
            }
            else if (aux.startsWith("CT"))
            {
                if (currentClient.reg.overridespam)
                {
                    currentClient.CT = aux.substring(2);
                    cur_inf = cur_inf + " CT" + currentClient.CT;
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

                currentClient.HI = aux.substring(2);
                cur_inf = cur_inf + " HI" + currentClient.HI;

            }

            else if (aux.startsWith("SU"))
            {
                currentClient.SU = aux.substring(2);
                cur_inf = cur_inf + " SU" + currentClient.SU;
            }
            else
            {
                //new STAError(handler,200+Constants.STA_GENERIC_PROTOCOL_ERROR,"Protocol Error.");
                //  return ;
                cur_inf = cur_inf + " " + aux;
            }


        }
        if (state.equals("PROTOCOL"))
        {
            if (currentClient.ID == null)
            {
                new STAError(currentClient,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "ID");
                return;
            }
            else if (currentClient.ID.equals(""))
            {
                new STAError(currentClient,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "ID");
                return;
            }
            if (currentClient.PD == null)
            {
                new STAError(currentClient,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "PD");
                return;
            }
            else if (currentClient.PD.equals(""))
            {
                new STAError(currentClient,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "PD");
                return;
            }

            if (currentClient.NI == null)
            {
                new STAError(currentClient,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "NI");
                return;
            }
            else if (currentClient.NI.equals(""))
            {
                new STAError(currentClient,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "NI");
                return;
            }
            if (currentClient.HN == null)
            {
                new STAError(currentClient,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "HN");
                return;
            }
            else if (currentClient.HN.equals(""))
            {
                new STAError(currentClient,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "HN");
                return;
            }
            currentClient.reg = AccountsConfig.getnod(currentClient.ID);
            if (currentClient.reg == null)
            {
                currentClient.reg = new Nod();
            }
            //handler.reg.CH=handler;
            // if(!handler.reg.isreg)
            //         handler.HN=String.valueOf(Integer.parseInt(handler.HN)+1);
        }


        /* check if user is banned first*/
        currentClient.myban = BanList.getban(3, currentClient.ID);
        if (currentClient.myban == null)
        {

            currentClient.myban = BanList.getban(2, (currentClient.RealIP));
            //System.out.println(handler.mySession.getRemoteAddress().toString());
        }
        if (currentClient.myban == null)
        {
            currentClient.myban = BanList.getban(1, currentClient.NI);

        }
        if (currentClient.myban != null) //banned
        {
            if (currentClient.myban.time == -1)
            {
                String msg = "Hello there. You are permanently banned.\nOp who banned you: " +
                             currentClient.myban.banop +
                             "\nReason: " +
                             currentClient.myban.banreason +
                             "\n" +
                             Vars.Msg_Banned;
                //System.out.println(msg);
                new STAError(currentClient, 200 + Constants.STA_PERMANENTLY_BANNED, msg);

                return;
            }
            long TL =
                    System.currentTimeMillis() - currentClient.myban.timeofban - currentClient.myban.time;
            TL = -TL;
            if (TL > 0)
            {
                String msg = "Hello there. You are temporary banned.\nOp who banned you: " +
                             currentClient.myban.banop +
                             "\nReason: " +
                             currentClient.myban.banreason +
                             "\nThere are still " +
                             Long.toString(TL / 1000) +
                             " seconds remaining.\n" +
                             Vars.Msg_Banned +
                             " TL" +
                             Long.toString(TL / 1000);
                //System.out.println(msg);
                new STAError(currentClient, 200 + Constants.STA_TEMP_BANNED, msg);

                return;
            }
        }
        //else System.out.println("no nick ban");

        int i = 0;


        for (Client client : SessionManager.getUsers())
        {

            if (!client.getClientHandler().equals(currentClient))
            {
                if (client.getClientHandler().userok == 1)
                {
                    if (client.getClientHandler().NI.toLowerCase().equals(currentClient.NI.toLowerCase()) &&
                        !client.getClientHandler().ID.equals(currentClient.ID))
                    {
                        new STAError(currentClient,
                                     200 + Constants.STA_NICK_TAKEN,
                                     "Nick taken, please choose another");
                        return;
                    }
                }
                /* if(state.equals ("PROTOCOL"))
                if(SessionManager.Users.containsKey(handler.ID) || temp.handler.ID.equals(handler.ID))//&& temp.handler.CIDsecure)
                {
                    new STAError(handler,200+Constants.STA_CID_TAKEN,"CID taken. Please go to Settings and pick new PID.");
                    return;
                }*/

                // handler.CIDsecure=true;
                i++;
            }


        }


        if (AccountsConfig.nickReserved(currentClient.NI, currentClient.ID))
        {
            int x = (state.equals("PROTOCOL")) ? 200 : 100;
            new STAError(currentClient,
                         x + Constants.STA_NICK_TAKEN,
                         "Nick reserved. Please choose another.");
            return;
        }
        // now must check if hub is full...
        if (state.equals("PROTOCOL")) //otherwise is already connected, no point in checking this
        {
            /** must check the hideme var*/
            if (currentClient.reg.HideMe)
            {
                cur_inf = cur_inf + " HI1";
                currentClient.HI = "1";
            }


            if (Vars.max_users <= i && !currentClient.reg.overridefull)
            {
                new STAError(currentClient,
                             200 + Constants.STA_HUB_FULL,
                             "Hello there. Hub is full, there are " +
                             String.valueOf(i) +
                             " users online.\n" +
                             Vars.Msg_Full);
                return;
            }


        }

        if (!currentClient.reg.overridespam)
        {
            if (currentClient.EM != null)
            {
                if (!ValidateField(currentClient.EM))
                {
                    new STAError(currentClient,
                                 state.equals("PROTOCOL") ? 200 : 100,
                                 "E-mail contains forbidden words.");
                    return;
                }
            }
        }
        if (!currentClient.reg.overridespam)
        {
            if (currentClient.DE != null)
            {
                if (!ValidateField(currentClient.DE))
                {
                    new STAError(currentClient,
                                 state.equals("PROTOCOL") ? 200 : 100,
                                 "Description contains forbidden words");
                    return;
                }
            }
        }

        if (!currentClient.reg.overridespam)
        {
            if (currentClient.SS == null && Vars.min_share != 0)
            {
                new STAError(currentClient,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Share too small, " + Vars.min_share + " MiB required.",
                             "FB",
                             "SS");
            }
        }
        if (!currentClient.reg.overridespam)
        {
            if (currentClient.SL == null && Vars.min_sl != 0)
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
            if (!currentClient.reg.overridespam)
            {
                if (currentClient.NI.length() > Vars.max_ni)
                {
                    new STAError(currentClient,
                                 200 + Constants.STA_NICK_INVALID,
                                 "Nick too large",
                                 "FB",
                                 "NI");
                    return;
                }
            }
            if (!currentClient.reg.overridespam)
            {
                if (currentClient.NI.length() < Vars.min_ni)
                {
                    new STAError(currentClient,
                                 200 + Constants.STA_NICK_INVALID,
                                 "Nick too small",
                                 "FB",
                                 "NI");
                    return;
                }
            }
            if (!currentClient.reg.overridespam)
            {
                if (currentClient.DE != null)
                {
                    if (currentClient.DE.length() > Vars.max_de)
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
            if (!currentClient.reg.overridespam)
            {
                if (currentClient.EM != null)
                {
                    if (currentClient.EM.length() > Vars.max_em)
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
            if (!currentClient.reg.overrideshare)
            {
                if (currentClient.SS != null)
                {
                    if (Long.parseLong(currentClient.SS) > 1024 * Vars.max_share * 1024)
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
            if (!currentClient.reg.overrideshare)
            {
                if (currentClient.SS != null)
                {
                    if (Long.parseLong(currentClient.SS) < 1024 * Vars.min_share * 1024)
                    {
                        new STAError(currentClient,
                                     200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                     "Share too small " + Vars.min_share + " MiB required.",
                                     "FB",
                                     "SS");
                        return;
                    }
                }
            }
            if (!currentClient.reg.overrideshare)
            {
                if (currentClient.SL != null)
                {
                    if (Integer.parseInt(currentClient.SL) < Vars.min_sl)
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
            if (!currentClient.reg.overrideshare)
            {
                if (currentClient.SL != null)
                {
                    if (Integer.parseInt(currentClient.SL) > Vars.max_sl)
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
            if (!currentClient.reg.overridespam)
            {
                if (Integer.parseInt(currentClient.HN) > Vars.max_hubs_user)
                {
                    new STAError(currentClient,
                                 200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 "Too many hubs open, close some.",
                                 "FB",
                                 "HN");
                    return;
                }
            }
            if (!currentClient.reg.overridespam)
            {
                if (currentClient.HO != null)
                {
                    if (Integer.parseInt(currentClient.HO) > Vars.max_hubs_op)
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
            if (!currentClient.reg.overridespam)
            {
                if (currentClient.HR != null)
                {
                    if (Integer.parseInt(currentClient.HR) > Vars.max_hubs_reg)
                    {
                        new STAError(currentClient,
                                     200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                     "You are regged on too many hubs. Sorry.",
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

        if (currentClient.ID.equals(Vars.OpChatCid))
        {
            new STAError(currentClient,
                         200 + Constants.STA_CID_TAKEN,
                         "CID taken. Please go to Settings and pick new PID.");
            return;
        }
        if (currentClient.ID.equals(Vars.SecurityCid))
        {
            new STAError(currentClient,
                         200 + Constants.STA_CID_TAKEN,
                         "CID taken. Please go to Settings and pick new PID.");
            return;
        }
        if (currentClient.NI.equalsIgnoreCase(Vars.Opchat_name))
        {
            new STAError(currentClient,
                         200 + Constants.STA_NICK_TAKEN,
                         "Nick taken, please choose another");
            return;
        }
        if (currentClient.NI.equalsIgnoreCase(Vars.bot_name))
        {
            new STAError(currentClient,
                         200 + Constants.STA_NICK_TAKEN,
                         "Nick taken, please choose another");
            return;
        }

        if (state.equals("PROTOCOL"))

        {
            try
            {
                Tiger myTiger = new Tiger();

                myTiger.engineReset();
                myTiger.init();
                byte[] bytepid = Base32.decode(currentClient.PD);


                myTiger.engineUpdate(bytepid, 0, bytepid.length);

                byte[] finalTiger = myTiger.engineDigest();
                if (!Base32.encode(finalTiger).equals(currentClient.ID))
                {
                    new STAError(currentClient,
                                 200 + Constants.STA_GENERIC_LOGIN_ERROR,
                                 "Invalid CID check.");
                    return;
                }
                if (currentClient.PD.length() != 39)
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


        if (currentClient.bas0 && currentClient.base != 2)
        {
            new STAError(currentClient,
                         200 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Your client uses a very old ADC version. Please update in order to connect to this hub. You can get a new version usually by visiting the developer's webpage from Help/About menu.");
        }


        if (currentClient.SU != null)
        {
            if (!(currentClient.SU.equals("")))
            {
                if (currentClient.SU.contains("TCP4"))
                {
                    currentClient.ACTIVE = 1;
                }
                else
                {
                    currentClient.ACTIVE = 0;
                }
            }
        }
        /*------------ok now must see if the pid is registered...---------------*/

        if (state.equals("PROTOCOL"))
        {
            if (currentClient.reg.isreg)
            {
                if (currentClient.reg.Password.equals(""))//no pass defined ( yet)
                {
                    currentClient.sendToClient(
                            "ISTA 000 Registered,\\sno\\spassword\\srequired.\\sThough,\\sits\\srecomandable\\sto\\sset\\sone.");
                    currentClient.sendToClient("ISTA 000 Authenticated.");


                    currentClient.reg.LastNI = currentClient.NI;
                    currentClient.reg.LastIP = currentClient.RealIP;
                    completeLogIn();
                    return;

                }
                currentClient.sendToClient("ISTA 000 Registered,\\stype\\syour\\spassword.");
                /* creates some hash for the GPA random data*/
                Tiger myTiger = new Tiger();

                myTiger.engineReset();
                myTiger.init();
                byte[] T =
                        Long.toString(System.currentTimeMillis()).getBytes(); //taken from cur time
                myTiger.engineUpdate(T, 0, T.length);

                byte[] finalTiger = myTiger.engineDigest();
                currentClient.RandomData = Base32.encode(finalTiger);
                currentClient.sendToClient("IGPA " + currentClient.RandomData);
                currentClient.State = "VERIFY";
                return;
            }
            else
            {
                Nod k;
                k = AccountsConfig.isNickRegFl(currentClient.NI);
                if (k != null)
                {
                    currentClient.sendToClient(
                            "ISTA 000 Nick\\sRegistered\\s(flyable\\saccount).\\sPlease\\sprovide\\spassword.");

                    /* creates some hash for the GPA random data*/
                    Tiger myTiger = new Tiger();

                    myTiger.engineReset();
                    myTiger.init();
                    byte[] T = Long.toString(System.currentTimeMillis())
                                   .getBytes(); //taken from cur time
                    myTiger.engineUpdate(T, 0, T.length);

                    byte[] finalTiger = myTiger.engineDigest();
                    currentClient.RandomData = Base32.encode(finalTiger);
                    currentClient.sendToClient("IGPA " + currentClient.RandomData);
                    currentClient.reg = k;
                    currentClient.State = "VERIFY";
                    return;
                }
                else if (Vars.reg_only == 1)
                {
                    new STAError(currentClient, 200 + Constants.STA_REG_ONLY, "Registered only hub.");
                    return;
                }
            }

        }


        //ok now must send to handler client the inf of all others
        if (state.equals("PROTOCOL"))
        {
            //ok now must send to handler the inf of all others


            /* IoSession [] x= Main.Server.SM.getSessions().toArray(new IoSession[0]);
            String inf="\n";
                for(int j=0;j<x.length;j++)
           {
                Client tempy=((ClientHandler)(x[j].getAttachment())).myNod;
                if(tempy.handler.userok==1 && !tempy.handler.equals (handler)) //if the user has some inf ... [ meaning he is ok]
                      inf=inf.substring(0,inf.length()-1)+tempy.handler.getINF ()+"\n\n";
           }
              inf=inf.substring(0,inf.length()-1)+"BINF DCBA ID"+Vars.SecurityCid+" NI"+ADC.retADCStr(Vars.bot_name)
                    +" BO1 OP1 DE"+ADC.retADCStr(Vars.bot_desc)+"\n";

                    inf+=handler.getINF ();  //sending inf about itself too
            handler.sendToClient(inf);*/

            boolean ok = pushUser();

            if (!ok)
            {
                new STAError(currentClient,
                             200 + Constants.STA_CID_TAKEN,
                             "CID taken. Please go to Settings and pick new PID.");
                return;
            }
            sendUsersInfs();

            currentClient.sendToClient("BINF DCBA ID" +
                                    Vars.SecurityCid +
                                    " NI" +
                                    ADC.retADCStr(Vars.bot_name)
                                    +
                                    " CT5 DE" +
                                    ADC.retADCStr(Vars.bot_desc));
            //handler.sendToClient("BINF DCBA IDaa NIbla");
            //      if(true)return;
            currentClient.putOpchat(true);
            currentClient.sendToClient(currentClient.getINF());  //sending inf about itself too

            //ok now must send INF to all clients
            Broadcast.getInstance().broadcast(currentClient.getINF(), currentClient.myNod);
            // System.out.println("acum am trimis ca a intrat "+handler.ID);


            //Main.PopMsg(handler.NI+" with SID "+handler.SessionID+" just entered.");
            //  handler.sendFromBot(""+Main.Server.myPath.replaceAll (" ","\\ "));
            //ok now that we passed to normal state and user is ok, check if it has UCMD, and if so, send a test command
            if (currentClient.ucmd == 1)
            {
                //ok, he is ucmd ok, so
                currentClient.sendToClient("ICMD Test CT1 TTTest");
            }
            currentClient.State = "NORMAL";
            currentClient.userok = 1; //user is OK, logged in and cool.
            currentClient.sendFromBot(ADC.MOTD);

            /** calling plugins...*/

            for (Module myMod : Modulator.myModules)
            {
                myMod.onConnect(currentClient);
            }
            return;
        }

        //  if(state.equals ("NORMAL"))
        //  {
        //      if(System.currentTimeMillis()-handler.LastINF>(1000*120L))
        //      {
        Broadcast.getInstance().broadcast(cur_inf);
        //        handler.LastINF=System.currentTimeMillis();
        //        handler.cur_inf=null;
        //      }
        //      else
        //         handler.cur_inf=cur_inf;

        //   }

    }


    /**
     * Main command handling function, ADC specific.
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

            if (state.equals("IDENTIFY") || state.equals("VERIFY"))
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
            if (!currentClient.reg.overridespam)
            {
                switch (command.charAt(0))
                {
                    case 'B':
                        if (Vars.BINF != 1)
                        {
                            new STAError(currentClient, 100, "INF Invalid Context B");
                            return;
                        }
                        break;
                    case 'E':
                        if (Vars.EINF != 1)
                        {
                            new STAError(currentClient, 100, "INF Invalid Context E");
                            return;
                        }
                        break;
                    case 'D':
                        if (Vars.DINF != 1)
                        {
                            new STAError(currentClient, 100, "INF Invalid Context D");
                            return;
                        }
                        break;
                    case 'F':
                        if (Vars.FINF != 1)
                        {
                            new STAError(currentClient, 100, "INF Invalid Context F");
                            return;
                        }
                        break;
                    case 'H':
                        if (Vars.HINF != 1)
                        {
                            new STAError(currentClient, 100, "INF Invalid Context H");
                            return;
                        }

                }
            }


            handleINF();

        }

        /************************PAS COMMAND****************************/
        if (command.charAt(1) == 'P' &&
            command.charAt(2) == 'A' &&
            command.charAt(3) == 'S')
        {

            if (!currentClient.reg.overridespam)
            {
                switch (command.charAt(0))
                {
                    case 'B':
                        if (Vars.BPAS != 1)
                        {
                            new STAError(currentClient, 100, "PAS Invalid Context B");
                            return;
                        }
                        break;
                    case 'E':
                        if (Vars.EPAS != 1)
                        {
                            new STAError(currentClient, 100, "PAS Invalid Context E");
                            return;
                        }
                        break;
                    case 'D':
                        if (Vars.DPAS != 1)
                        {
                            new STAError(currentClient, 100, "PAS Invalid Context D");
                            return;
                        }
                        break;
                    case 'F':
                        if (Vars.FPAS != 1)
                        {
                            new STAError(currentClient, 100, "PAS Invalid Context F");
                            return;
                        }
                        break;
                    case 'H':
                        if (Vars.HPAS != 1)
                        {
                            new STAError(currentClient, 100, "PAS Invalid Context H");
                            return;
                        }

                }
            }
            Nod k;

            if ((k = AccountsConfig.isNickRegFl(currentClient.NI)) != null)
            {
                currentClient.reg = k;
            }
            if (!currentClient.reg.isreg)
            {
                new STAError(currentClient, 100, "Not registered.");
                return;
            }
            if (command.charAt(0) != 'H')
            {
                if (state.equals("NORMAL"))
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
                byte[] pas = currentClient.reg.Password.getBytes();
                byte[] random = Base32.decode(currentClient.RandomData);

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
                currentClient.sendToClient("IMSG Authenticated.");

                currentClient.sendFromBot(ADC.MOTD);

                //System.out.println ("pwla");
                currentClient.reg.LastNI = currentClient.NI;
                // handler.reg.LastNI=handler.NI;
                currentClient.reg.LastIP = currentClient.RealIP;

                if (!currentClient.ID.equals(currentClient.reg.CID))
                {
                    currentClient.sendToClient("IMSG Account\\sCID\\supdated\\sto\\s" + currentClient.ID);
                }
                currentClient.reg.CID = currentClient.ID;
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
        if (command.charAt(1) == 'S' &&
            command.charAt(2) == 'U' &&
            command.charAt(3) == 'P')
        {
            new SUP(currentClient, state, command);

        }


        /********************************MSG COMMAND************************************/
        if (command.charAt(1) == 'M' &&
            command.charAt(2) == 'S' &&
            command.charAt(3) == 'G')
        {
            new MSG(currentClient, state, command);
        }


        if (command.charAt(1) == 'S' &&
            command.charAt(2) == 'C' &&
            command.charAt(3) == 'H')
        {
            new SCH(currentClient, command, state);
        }
        if (command.charAt(1) == 'S' &&
            command.charAt(2) == 'T' &&
            command.charAt(3) == 'A')
        {
            new STA(currentClient, command, state);
        }
        if (command.substring(1)
                          .startsWith("RES ")) //direct search result, only active to passive must send this
        {
            new RES(currentClient, state, command);
        }
        else if (command.substring(1).startsWith("CTM ")) //direct connect to me
        {
            new CTM(currentClient, state, command);
        }
        else if (command.substring(1).startsWith("RCM ")) //reverse connect to me
        {
            new RCM(currentClient, state, command);
        }


        /** calling plugins...*/

        for (Module myMod : Modulator.myModules)
        {
            myMod.onRawCommand(currentClient, command);
        }
    }


    /**
     * Creates a new instance of Command with following params
     * CH of type ClientHandler identifies tha client to handle
     * Issued_command of String type actually identifies the given command
     * state also of type String Identifies tha state in which tha connection is,
     * meaning [ accordingly to arne's draft]:
     * PROTOCOL (feature support discovery), IDENTIFY (user identification, static checks),
     * VERIFY (password check), NORMAL (normal operation) and DATA (for binary transfers).
     * Calling function should send one of this params, that is calling function
     * request... Command class does not check params.
     * Function throws CommandException if smth is wrong.
     */
    public Command(ClientHandler CH, String Issued_command)
            throws STAException, CommandException
    {
        currentClient = CH;
        // System.out.printf("["+handler.NI+"]:%s\n",Issued_command);


        //System.out.printf("[Received]:%s\n",Issued_command);
        if (Issued_command.equals(""))
        {
            //System.out.println("("+handler.NI+")"+System.currentTimeMillis ()/1000);
            return;
        }


        command = Issued_command;
        state = currentClient.State;
        HandleIssuedCommand();
        // if(handler.NI.contains("Pietr"))
        //    new STAError(handler,201,"exception test bla.");
    }

}
