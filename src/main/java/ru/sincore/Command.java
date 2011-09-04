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
 */

public class Command
{
    ClientHandler cur_client;
    String        Issued_Command;
    String        State;


    private void sendUsersInfs()
    {
//String Infs="";
        for (ClientNod iterator : SimpleHandler.getUsers())
        {
            if (iterator.cur_client.userok == 1 && iterator.cur_client != cur_client)
            {
                cur_client.sendToClient(iterator.cur_client.getINF());
            }


        }
        // if(!(Infs.equals("")))
        //cur_client.sendToClient(Infs);
    }


    private boolean pushUser()
    {
        // boolean ok=false;
        synchronized (SimpleHandler.Users)
        {
            // System.out.println("marimea este "+SimpleHandler.Users.size());
            if (SimpleHandler.Users.containsKey(cur_client.ID))
            {
                ClientNod ch = SimpleHandler.Users.get(cur_client.ID);
                ch.dropMeImGhost();
            }


            SimpleHandler.Users.put(cur_client.ID, cur_client.myNod);
            cur_client.inside = true;
            //  ok=true;
            //System.out.println("a intrat "+cur_client.ID+", marimea este "+SimpleHandler.Users.size());


        }
        return true;
    }


    void completeLogIn()
            throws STAException
    {
        // must check if its op or not and move accordingly
        if (!cur_client.reg.key) //DO NOT increase HR count and put RG field to 1
        {
            //  cur_client.HR=String.valueOf(Integer.parseInt(cur_client.HR)+1);
            cur_client.CT = "2";
        }
        else //DO NOT increase HO count and put OP field to 1
        {
            //   cur_client.HO=String.valueOf(Integer.parseInt(cur_client.HO)+1);
            cur_client.CT = "4";
        }


        boolean ok = pushUser();

        if (!ok)
        {
            new STAError(cur_client,
                         200 + Constants.STA_CID_TAKEN,
                         "CID taken. Please go to Settings and pick new PID.");
            return;
        }


        //ok now must send to cur_client the inf of all others


        /*  IoSession [] x= Main.Server.SM.getSessions().toArray(new IoSession[0]);
      String inf="\n";
          for(int i=0;i<x.length;i++)
     {
          ClientNod tempy=((ClientHandler)(x[i].getAttachment())).myNod;
          if(tempy.cur_client.userok==1 && !tempy.cur_client.equals (cur_client)) //if the user has some inf ... [ meaning he is ok]
                inf=inf.substring(0,inf.length()-1)+tempy.cur_client.getINF ()+"\n\n";
     }
        */
        sendUsersInfs();

        cur_client.sendToClient("BINF DCBA ID" +
                                Vars.SecurityCid +
                                " NI" +
                                ADC.retADCStr(Vars.bot_name)
                                +
                                " CT5 DE" +
                                ADC.retADCStr(Vars.bot_desc));
        cur_client.putOpchat(true);
        cur_client.sendToClient(cur_client.getINF());  //sending inf about itself too
        //cur_client.sendToClient(inf);


        //ok now must send INF to all clients
        Broadcast.getInstance().broadcast(cur_client.getINF(), cur_client.myNod);
        cur_client.userok = 1; //user is OK, logged in and cool.
        cur_client.reg.LastLogin = System.currentTimeMillis();
        cur_client.sendFromBot(ADC.MOTD);
        //System.out.println("gay");
        //cur_client.sendFromBot ("gay");
        cur_client.sendFromBot(cur_client.reg.HideMe ? "You are currently hidden." : "");

        cur_client.LoggedAt = System.currentTimeMillis();
        cur_client.State = "NORMAL";


        /** calling plugins...*/

        for (Module myMod : Modulator.myModules)
        {
            myMod.onConnect(cur_client);
        }
        //cur_client.sendFromBot( ADC.MOTD);
        cur_client.can_receive_cmds = true;


    }


    boolean ValidateField(String str)
    {
        return Main.listaBanate.isOK(str) == -1;
    }


    void handleINF()
            throws CommandException, STAException
    {

        if (Issued_Command.length() < 10)
        {
            new STAError(cur_client,
                         100 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Incorrect protocol command");
            return;
        }
        Issued_Command = Issued_Command.substring(4);
        StringTokenizer tok = new StringTokenizer(Issued_Command);

        String cur_inf = "BINF " + cur_client.SessionID;

        String thesid = tok.nextToken();
        if (!thesid.equals(cur_client.SessionID))
        {
            new STAError(cur_client,
                         200 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Protocol Error.Wrong SID supplied.");
            return;
        }

        // cur_client.cur_inf="BINF ADDD EMtest NIbla";
        //Issued_Command="ADDD NImu DEblah";
        //     synchronized(cur_client.cur_inf)
        //      {
        //    if(cur_client.cur_inf!=null)
        //     {
        //     StringTokenizer inftok=new StringTokenizer(cur_client.cur_inf.substring(9));

        //    while(inftok.hasMoreTokens())
        //    {
        //         String y=inftok.nextToken();
        //         if(Issued_Command.contains(y.substring(0,2)))
        //        {
        //            cur_client.cur_inf=cur_client.cur_inf.substring(0,cur_client.cur_inf.indexOf(y))+cur_client.cur_inf.substring(cur_client.cur_inf.indexOf(y)+y.length());
        //           // inftok=new StringTokenizer(cur_client.cur_inf);
        //      }

        //   }
        //   Issued_Command+=cur_client.cur_inf.substring(9);
        //   }
        tok = new StringTokenizer(Issued_Command);
        tok.nextToken();
        //    }
        //   if(Issued_Command.endsWith(" "))
        //         Issued_Command=Issued_Command.substring(0,Issued_Command.length()-1);

        // System.out.println(Issued_Command);
        while (tok.hasMoreElements())
        {

            String aux = tok.nextToken();


            // System.out.println(aux);
            if (aux.startsWith("ID"))//meaning we have the ID thingy
            {

                if (!State.equals("PROTOCOL"))
                {
                    new STAError(cur_client, 100, "Can't change CID while connected.");
                    return;
                }
                cur_client.ID = aux.substring(2);
                cur_inf = cur_inf + " ID" + cur_client.ID;
                //System.out.println (cur_client.ID);
            }
            else if (aux.startsWith("NI"))
            {


                if (!Vars.ValidateNick(aux.substring(2)))
                {
                    new STAError(cur_client,
                                 200 + Constants.STA_NICK_INVALID,
                                 "Nick not valid, please choose another");
                    return;
                }
                cur_client.NI = aux.substring(2);

                if (!State.equals("PROTOCOL"))
                {
                    if (cur_client.reg.isreg)
                    {
                        cur_client.reg.LastNI = cur_client.NI;
                    }
                }

                cur_inf = cur_inf + " NI" + cur_client.NI;
            }
            else if (aux.startsWith("PD"))//the PiD
            {


                if (!State.equals("PROTOCOL"))
                {
                    new STAError(cur_client, 100, "Can't change PID while connected.");
                    return;
                }


                cur_client.PD = aux.substring(2);
            }
            else if (aux.startsWith("I4"))
            {

                cur_client.I4 = aux.substring(2);
                if (aux.substring(2).equals("0.0.0.0") ||
                    aux.substring(2).equals("localhost"))//only if active client
                {
                    cur_client.I4 = cur_client.RealIP;
                }


                else if (!aux.substring(2).equals(cur_client.RealIP) && !aux.substring(2).equals("")
                         && !cur_client.RealIP.equals("127.0.0.1"))
                {
                    new STAError(cur_client,
                                 200 + Constants.STA_INVALID_IP,
                                 "Wrong IP address supplied.",
                                 "I4",
                                 cur_client.RealIP);
                    return;
                }
                cur_inf = cur_inf + " I4" + cur_client.I4;

            }
            else if (aux.startsWith("I6"))
            {
                cur_client.I6 = aux.substring(2);
                cur_inf = cur_inf + " I6" + cur_client.I6;
            }
            else if (aux.startsWith("U4"))
            {
                cur_client.U4 = aux.substring(2);
                cur_inf = cur_inf + " U4" + cur_client.U4;
            }
            else if (aux.startsWith("U6"))
            {
                cur_client.U6 = aux.substring(2);
                cur_inf = cur_inf + " U6" + cur_client.U6;
            }
            else if (aux.startsWith("SS"))
            {
                cur_client.SS = aux.substring(2);
                cur_inf = cur_inf + " SS" + cur_client.SS;
            }
            else if (aux.startsWith("SF"))
            {
                cur_client.SF = aux.substring(2);
                cur_inf = cur_inf + " SF" + cur_client.SF;
            }
            else if (aux.startsWith("VE"))
            {
                cur_client.VE = aux.substring(2);
                cur_inf = cur_inf + " VE" + cur_client.VE;
            }
            else if (aux.startsWith("US"))
            {
                cur_client.US = aux.substring(2);
                cur_inf = cur_inf + " US" + cur_client.US;
            }
            else if (aux.startsWith("DS"))
            {
                cur_client.DS = aux.substring(2);
                cur_inf = cur_inf + " DS" + cur_client.DS;
            }
            else if (aux.startsWith("SL"))
            {
                cur_client.SL = aux.substring(2);
                cur_inf = cur_inf + " SL" + cur_client.SL;
            }
            else if (aux.startsWith("AS"))
            {
                cur_client.AS = aux.substring(2);
                cur_inf = cur_inf + " AS" + cur_client.AS;
            }
            else if (aux.startsWith("AM"))
            {
                cur_client.AM = aux.substring(2);
                cur_inf = cur_inf + " AM" + cur_client.AM;
            }
            else if (aux.startsWith("EM"))
            {
                cur_client.EM = aux.substring(2);
                cur_inf = cur_inf + " EM" + cur_client.EM;
            }

            else if (aux.startsWith("DE"))
            {
                cur_client.DE = aux.substring(2);
                cur_inf = cur_inf + " DE" + cur_client.DE;
            }
            else if (aux.startsWith("HN"))
            {
                cur_client.HN = aux.substring(2);

                if (State.equals("NORMAL"))
                {
                    cur_inf = cur_inf + " HN" + cur_client.HN;
                }
            }
            else if (aux.startsWith("HR"))
            {
                cur_client.HR = aux.substring(2);
                cur_inf = cur_inf + " HR" + cur_client.HR;
            }
            else if (aux.startsWith("HO"))
            {
                cur_client.HO = aux.substring(2);
                cur_inf = cur_inf + " HO" + cur_client.HO;
            }
            else if (aux.startsWith("TO"))
            {
                cur_client.TO = aux.substring(2);
                cur_inf = cur_inf + " TO" + cur_client.TO;
            }

            else if (aux.startsWith("AW"))
            {
                cur_client.AW = aux.substring(2);
                cur_inf = cur_inf + " AW" + cur_client.AW;
            }
            else if (aux.startsWith("CT"))
            {
                if (cur_client.reg.overridespam)
                {
                    cur_client.CT = aux.substring(2);
                    cur_inf = cur_inf + " CT" + cur_client.CT;
                }
                else
                {
                    new STAError(cur_client,
                                 200 + Constants.STA_GENERIC_LOGIN_ERROR,
                                 "Not allowed to have CT field.");
                    return;
                }
            }
            else if (aux.startsWith("HI"))
            {

                cur_client.HI = aux.substring(2);
                cur_inf = cur_inf + " HI" + cur_client.HI;

            }

            else if (aux.startsWith("SU"))
            {
                cur_client.SU = aux.substring(2);
                cur_inf = cur_inf + " SU" + cur_client.SU;
            }
            else
            {
                //new STAError(cur_client,200+Constants.STA_GENERIC_PROTOCOL_ERROR,"Protocol Error.");
                //  return ;
                cur_inf = cur_inf + " " + aux;
            }


        }
        if (State.equals("PROTOCOL"))
        {
            if (cur_client.ID == null)
            {
                new STAError(cur_client,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "ID");
                return;
            }
            else if (cur_client.ID.equals(""))
            {
                new STAError(cur_client,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "ID");
                return;
            }
            if (cur_client.PD == null)
            {
                new STAError(cur_client,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "PD");
                return;
            }
            else if (cur_client.PD.equals(""))
            {
                new STAError(cur_client,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "PD");
                return;
            }

            if (cur_client.NI == null)
            {
                new STAError(cur_client,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "NI");
                return;
            }
            else if (cur_client.NI.equals(""))
            {
                new STAError(cur_client,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "NI");
                return;
            }
            if (cur_client.HN == null)
            {
                new STAError(cur_client,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "HN");
                return;
            }
            else if (cur_client.HN.equals(""))
            {
                new STAError(cur_client,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Missing field",
                             "FM",
                             "HN");
                return;
            }
            cur_client.reg = AccountsConfig.getnod(cur_client.ID);
            if (cur_client.reg == null)
            {
                cur_client.reg = new Nod();
            }
            //cur_client.reg.CH=cur_client;
            // if(!cur_client.reg.isreg)
            //         cur_client.HN=String.valueOf(Integer.parseInt(cur_client.HN)+1);
        }


        /* check if user is banned first*/
        cur_client.myban = BanList.getban(3, cur_client.ID);
        if (cur_client.myban == null)
        {

            cur_client.myban = BanList.getban(2, (cur_client.RealIP));
            //System.out.println(cur_client.mySession.getRemoteAddress().toString());
        }
        if (cur_client.myban == null)
        {
            cur_client.myban = BanList.getban(1, cur_client.NI);

        }
        if (cur_client.myban != null) //banned
        {
            if (cur_client.myban.time == -1)
            {
                String msg = "Hello there. You are permanently banned.\nOp who banned you: " +
                             cur_client.myban.banop +
                             "\nReason: " +
                             cur_client.myban.banreason +
                             "\n" +
                             Vars.Msg_Banned;
                //System.out.println(msg);
                new STAError(cur_client, 200 + Constants.STA_PERMANENTLY_BANNED, msg);

                return;
            }
            long TL =
                    System.currentTimeMillis() - cur_client.myban.timeofban - cur_client.myban.time;
            TL = -TL;
            if (TL > 0)
            {
                String msg = "Hello there. You are temporary banned.\nOp who banned you: " +
                             cur_client.myban.banop +
                             "\nReason: " +
                             cur_client.myban.banreason +
                             "\nThere are still " +
                             Long.toString(TL / 1000) +
                             " seconds remaining.\n" +
                             Vars.Msg_Banned +
                             " TL" +
                             Long.toString(TL / 1000);
                //System.out.println(msg);
                new STAError(cur_client, 200 + Constants.STA_TEMP_BANNED, msg);

                return;
            }
        }
        //else System.out.println("no nick ban");

        int i = 0;


        for (ClientNod temp : SimpleHandler.getUsers())
        {

            if (!temp.cur_client.equals(cur_client))
            {
                if (temp.cur_client.userok == 1)
                {
                    if (temp.cur_client.NI.toLowerCase().equals(cur_client.NI.toLowerCase()) &&
                        !temp.cur_client.ID.equals(cur_client.ID))
                    {
                        new STAError(cur_client,
                                     200 + Constants.STA_NICK_TAKEN,
                                     "Nick taken, please choose another");
                        return;
                    }
                }
                /* if(State.equals ("PROTOCOL"))
                if(SimpleHandler.Users.containsKey(cur_client.ID) || temp.cur_client.ID.equals(cur_client.ID))//&& temp.cur_client.CIDsecure)
                {
                    new STAError(cur_client,200+Constants.STA_CID_TAKEN,"CID taken. Please go to Settings and pick new PID.");
                    return;
                }*/

                // cur_client.CIDsecure=true;
                i++;
            }


        }


        if (AccountsConfig.nickReserved(cur_client.NI, cur_client.ID))
        {
            int x = (State.equals("PROTOCOL")) ? 200 : 100;
            new STAError(cur_client,
                         x + Constants.STA_NICK_TAKEN,
                         "Nick reserved. Please choose another.");
            return;
        }
        // now must check if hub is full...
        if (State.equals("PROTOCOL")) //otherwise is already connected, no point in checking this
        {
            /** must check the hideme var*/
            if (cur_client.reg.HideMe)
            {
                cur_inf = cur_inf + " HI1";
                cur_client.HI = "1";
            }


            if (Vars.max_users <= i && !cur_client.reg.overridefull)
            {
                new STAError(cur_client,
                             200 + Constants.STA_HUB_FULL,
                             "Hello there. Hub is full, there are " +
                             String.valueOf(i) +
                             " users online.\n" +
                             Vars.Msg_Full);
                return;
            }


        }

        if (!cur_client.reg.overridespam)
        {
            if (cur_client.EM != null)
            {
                if (!ValidateField(cur_client.EM))
                {
                    new STAError(cur_client,
                                 State.equals("PROTOCOL") ? 200 : 100,
                                 "E-mail contains forbidden words.");
                    return;
                }
            }
        }
        if (!cur_client.reg.overridespam)
        {
            if (cur_client.DE != null)
            {
                if (!ValidateField(cur_client.DE))
                {
                    new STAError(cur_client,
                                 State.equals("PROTOCOL") ? 200 : 100,
                                 "Description contains forbidden words");
                    return;
                }
            }
        }

        if (!cur_client.reg.overridespam)
        {
            if (cur_client.SS == null && Vars.min_share != 0)
            {
                new STAError(cur_client,
                             200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             "Share too small, " + Vars.min_share + " MiB required.",
                             "FB",
                             "SS");
            }
        }
        if (!cur_client.reg.overridespam)
        {
            if (cur_client.SL == null && Vars.min_sl != 0)
            {
                new STAError(cur_client,
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
            if (!cur_client.reg.overridespam)
            {
                if (cur_client.NI.length() > Vars.max_ni)
                {
                    new STAError(cur_client,
                                 200 + Constants.STA_NICK_INVALID,
                                 "Nick too large",
                                 "FB",
                                 "NI");
                    return;
                }
            }
            if (!cur_client.reg.overridespam)
            {
                if (cur_client.NI.length() < Vars.min_ni)
                {
                    new STAError(cur_client,
                                 200 + Constants.STA_NICK_INVALID,
                                 "Nick too small",
                                 "FB",
                                 "NI");
                    return;
                }
            }
            if (!cur_client.reg.overridespam)
            {
                if (cur_client.DE != null)
                {
                    if (cur_client.DE.length() > Vars.max_de)
                    {
                        new STAError(cur_client,
                                     200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                     "Description too large",
                                     "FB",
                                     "DE");
                        return;
                    }
                }
            }
            if (!cur_client.reg.overridespam)
            {
                if (cur_client.EM != null)
                {
                    if (cur_client.EM.length() > Vars.max_em)
                    {
                        new STAError(cur_client,
                                     200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                     "E-mail too large",
                                     "FB",
                                     "EM");
                        return;
                    }
                }
            }
            if (!cur_client.reg.overrideshare)
            {
                if (cur_client.SS != null)
                {
                    if (Long.parseLong(cur_client.SS) > 1024 * Vars.max_share * 1024)
                    {
                        new STAError(cur_client,
                                     200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                     "Share too large",
                                     "FB",
                                     "SS");
                        return;
                    }
                }
            }
            if (!cur_client.reg.overrideshare)
            {
                if (cur_client.SS != null)
                {
                    if (Long.parseLong(cur_client.SS) < 1024 * Vars.min_share * 1024)
                    {
                        new STAError(cur_client,
                                     200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                     "Share too small " + Vars.min_share + " MiB required.",
                                     "FB",
                                     "SS");
                        return;
                    }
                }
            }
            if (!cur_client.reg.overrideshare)
            {
                if (cur_client.SL != null)
                {
                    if (Integer.parseInt(cur_client.SL) < Vars.min_sl)
                    {
                        new STAError(cur_client,
                                     200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                     "Too few slots, open up more.",
                                     "FB",
                                     "SL");
                        return;
                    }
                }
            }
            if (!cur_client.reg.overrideshare)
            {
                if (cur_client.SL != null)
                {
                    if (Integer.parseInt(cur_client.SL) > Vars.max_sl)
                    {
                        new STAError(cur_client,
                                     200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                     "Too many slots, close some.",
                                     "FB",
                                     "SL");
                        return;
                    }
                }
            }
            if (!cur_client.reg.overridespam)
            {
                if (Integer.parseInt(cur_client.HN) > Vars.max_hubs_user)
                {
                    new STAError(cur_client,
                                 200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 "Too many hubs open, close some.",
                                 "FB",
                                 "HN");
                    return;
                }
            }
            if (!cur_client.reg.overridespam)
            {
                if (cur_client.HO != null)
                {
                    if (Integer.parseInt(cur_client.HO) > Vars.max_hubs_op)
                    {
                        new STAError(cur_client,
                                     200 + Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                     "You are operator on too many hubs. Sorry.",
                                     "FB",
                                     "HO");
                        return;
                    }
                }
            }
            if (!cur_client.reg.overridespam)
            {
                if (cur_client.HR != null)
                {
                    if (Integer.parseInt(cur_client.HR) > Vars.max_hubs_reg)
                    {
                        new STAError(cur_client,
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
            new STAError(cur_client,
                         200 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Your client sent weird info, Protocol Error.");
            return;
        }

        if (cur_client.ID.equals(Vars.OpChatCid))
        {
            new STAError(cur_client,
                         200 + Constants.STA_CID_TAKEN,
                         "CID taken. Please go to Settings and pick new PID.");
            return;
        }
        if (cur_client.ID.equals(Vars.SecurityCid))
        {
            new STAError(cur_client,
                         200 + Constants.STA_CID_TAKEN,
                         "CID taken. Please go to Settings and pick new PID.");
            return;
        }
        if (cur_client.NI.equalsIgnoreCase(Vars.Opchat_name))
        {
            new STAError(cur_client,
                         200 + Constants.STA_NICK_TAKEN,
                         "Nick taken, please choose another");
            return;
        }
        if (cur_client.NI.equalsIgnoreCase(Vars.bot_name))
        {
            new STAError(cur_client,
                         200 + Constants.STA_NICK_TAKEN,
                         "Nick taken, please choose another");
            return;
        }

        if (State.equals("PROTOCOL"))

        {
            try
            {
                Tiger myTiger = new Tiger();

                myTiger.engineReset();
                myTiger.init();
                byte[] bytepid = Base32.decode(cur_client.PD);


                myTiger.engineUpdate(bytepid, 0, bytepid.length);

                byte[] finalTiger = myTiger.engineDigest();
                if (!Base32.encode(finalTiger).equals(cur_client.ID))
                {
                    new STAError(cur_client,
                                 200 + Constants.STA_GENERIC_LOGIN_ERROR,
                                 "Invalid CID check.");
                    return;
                }
                if (cur_client.PD.length() != 39)
                {
                    throw new IllegalArgumentException();
                }


            }


            catch (IllegalArgumentException iae)
            {
                new STAError(cur_client, 200 + Constants.STA_INVALID_PID, "Invalid PID supplied.");
                return;
            }
            catch (Exception e)
            {
                System.out.println(e);
                return;
            }
        }


        if (cur_client.bas0 && cur_client.base != 2)
        {
            new STAError(cur_client,
                         200 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Your client uses a very old ADC version. Please update in order to connect to this hub. You can get a new version usually by visiting the developer's webpage from Help/About menu.");
        }


        if (cur_client.SU != null)
        {
            if (!(cur_client.SU.equals("")))
            {
                if (cur_client.SU.contains("TCP4"))
                {
                    cur_client.ACTIVE = 1;
                }
                else
                {
                    cur_client.ACTIVE = 0;
                }
            }
        }
        /*------------ok now must see if the pid is registered...---------------*/

        if (State.equals("PROTOCOL"))
        {
            if (cur_client.reg.isreg)
            {
                if (cur_client.reg.Password.equals(""))//no pass defined ( yet)
                {
                    cur_client.sendToClient(
                            "ISTA 000 Registered,\\sno\\spassword\\srequired.\\sThough,\\sits\\srecomandable\\sto\\sset\\sone.");
                    cur_client.sendToClient("ISTA 000 Authenticated.");


                    cur_client.reg.LastNI = cur_client.NI;
                    cur_client.reg.LastIP = cur_client.RealIP;
                    completeLogIn();
                    return;

                }
                cur_client.sendToClient("ISTA 000 Registered,\\stype\\syour\\spassword.");
                /* creates some hash for the GPA random data*/
                Tiger myTiger = new Tiger();

                myTiger.engineReset();
                myTiger.init();
                byte[] T =
                        Long.toString(System.currentTimeMillis()).getBytes(); //taken from cur time
                myTiger.engineUpdate(T, 0, T.length);

                byte[] finalTiger = myTiger.engineDigest();
                cur_client.RandomData = Base32.encode(finalTiger);
                cur_client.sendToClient("IGPA " + cur_client.RandomData);
                cur_client.State = "VERIFY";
                return;
            }
            else
            {
                Nod k;
                k = AccountsConfig.isNickRegFl(cur_client.NI);
                if (k != null)
                {
                    cur_client.sendToClient(
                            "ISTA 000 Nick\\sRegistered\\s(flyable\\saccount).\\sPlease\\sprovide\\spassword.");

                    /* creates some hash for the GPA random data*/
                    Tiger myTiger = new Tiger();

                    myTiger.engineReset();
                    myTiger.init();
                    byte[] T = Long.toString(System.currentTimeMillis())
                                   .getBytes(); //taken from cur time
                    myTiger.engineUpdate(T, 0, T.length);

                    byte[] finalTiger = myTiger.engineDigest();
                    cur_client.RandomData = Base32.encode(finalTiger);
                    cur_client.sendToClient("IGPA " + cur_client.RandomData);
                    cur_client.reg = k;
                    cur_client.State = "VERIFY";
                    return;
                }
                else if (Vars.reg_only == 1)
                {
                    new STAError(cur_client, 200 + Constants.STA_REG_ONLY, "Registered only hub.");
                    return;
                }
            }

        }


        //ok now must send to cur_client client the inf of all others
        if (State.equals("PROTOCOL"))
        {
            //ok now must send to cur_client the inf of all others


            /* IoSession [] x= Main.Server.SM.getSessions().toArray(new IoSession[0]);
            String inf="\n";
                for(int j=0;j<x.length;j++)
           {
                ClientNod tempy=((ClientHandler)(x[j].getAttachment())).myNod;
                if(tempy.cur_client.userok==1 && !tempy.cur_client.equals (cur_client)) //if the user has some inf ... [ meaning he is ok]
                      inf=inf.substring(0,inf.length()-1)+tempy.cur_client.getINF ()+"\n\n";
           }
              inf=inf.substring(0,inf.length()-1)+"BINF DCBA ID"+Vars.SecurityCid+" NI"+ADC.retADCStr(Vars.bot_name)
                    +" BO1 OP1 DE"+ADC.retADCStr(Vars.bot_desc)+"\n";

                    inf+=cur_client.getINF ();  //sending inf about itself too
            cur_client.sendToClient(inf);*/

            boolean ok = pushUser();

            if (!ok)
            {
                new STAError(cur_client,
                             200 + Constants.STA_CID_TAKEN,
                             "CID taken. Please go to Settings and pick new PID.");
                return;
            }
            sendUsersInfs();

            cur_client.sendToClient("BINF DCBA ID" +
                                    Vars.SecurityCid +
                                    " NI" +
                                    ADC.retADCStr(Vars.bot_name)
                                    +
                                    " CT5 DE" +
                                    ADC.retADCStr(Vars.bot_desc));
            //cur_client.sendToClient("BINF DCBA IDaa NIbla");
            //      if(true)return;
            cur_client.putOpchat(true);
            cur_client.sendToClient(cur_client.getINF());  //sending inf about itself too

            //ok now must send INF to all clients
            Broadcast.getInstance().broadcast(cur_client.getINF(), cur_client.myNod);
            // System.out.println("acum am trimis ca a intrat "+cur_client.ID);


            //Main.PopMsg(cur_client.NI+" with SID "+cur_client.SessionID+" just entered.");
            //  cur_client.sendFromBot(""+Main.Server.myPath.replaceAll (" ","\\ "));
            //ok now that we passed to normal state and user is ok, check if it has UCMD, and if so, send a test command
            if (cur_client.ucmd == 1)
            {
                //ok, he is ucmd ok, so
                cur_client.sendToClient("ICMD Test CT1 TTTest");
            }
            cur_client.State = "NORMAL";
            cur_client.userok = 1; //user is OK, logged in and cool.
            cur_client.sendFromBot(ADC.MOTD);

            /** calling plugins...*/

            for (Module myMod : Modulator.myModules)
            {
                myMod.onConnect(cur_client);
            }
            return;
        }

        //  if(State.equals ("NORMAL"))
        //  {
        //      if(System.currentTimeMillis()-cur_client.LastINF>(1000*120L))
        //      {
        Broadcast.getInstance().broadcast(cur_inf);
        //        cur_client.LastINF=System.currentTimeMillis();
        //        cur_client.cur_inf=null;
        //      }
        //      else
        //         cur_client.cur_inf=cur_inf;

        //   }

    }


    /**
     * Main command handling function, ADC specific.
     */
    void HandleIssuedCommand()
            throws CommandException, STAException
    {


        if (Issued_Command.length() < 4)
        {
            new STAError(cur_client,
                         100 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Incorrect command");
        }
        /*******************************INF COMMAND *****************************************/

        if (Issued_Command.substring(1).startsWith("INF"))
        {

            if (State.equals("IDENTIFY") || State.equals("VERIFY"))
            {
                new STAError(cur_client,
                             200 + Constants.STA_INVALID_STATE,
                             "INF Invalid State.",
                             "FC",
                             Issued_Command.substring(0, 4));
                return;
            }


            if (Issued_Command.charAt(0) != 'B')
            {
                new STAError(cur_client, 100, "INF Invalid Context.");
                return;
            }
            if (!cur_client.reg.overridespam)
            {
                switch (Issued_Command.charAt(0))
                {
                    case 'B':
                        if (Vars.BINF != 1)
                        {
                            new STAError(cur_client, 100, "INF Invalid Context B");
                            return;
                        }
                        break;
                    case 'E':
                        if (Vars.EINF != 1)
                        {
                            new STAError(cur_client, 100, "INF Invalid Context E");
                            return;
                        }
                        break;
                    case 'D':
                        if (Vars.DINF != 1)
                        {
                            new STAError(cur_client, 100, "INF Invalid Context D");
                            return;
                        }
                        break;
                    case 'F':
                        if (Vars.FINF != 1)
                        {
                            new STAError(cur_client, 100, "INF Invalid Context F");
                            return;
                        }
                        break;
                    case 'H':
                        if (Vars.HINF != 1)
                        {
                            new STAError(cur_client, 100, "INF Invalid Context H");
                            return;
                        }

                }
            }


            handleINF();

        }

        /************************PAS COMMAND****************************/
        if (Issued_Command.charAt(1) == 'P' &&
            Issued_Command.charAt(2) == 'A' &&
            Issued_Command.charAt(3) == 'S')
        {

            if (!cur_client.reg.overridespam)
            {
                switch (Issued_Command.charAt(0))
                {
                    case 'B':
                        if (Vars.BPAS != 1)
                        {
                            new STAError(cur_client, 100, "PAS Invalid Context B");
                            return;
                        }
                        break;
                    case 'E':
                        if (Vars.EPAS != 1)
                        {
                            new STAError(cur_client, 100, "PAS Invalid Context E");
                            return;
                        }
                        break;
                    case 'D':
                        if (Vars.DPAS != 1)
                        {
                            new STAError(cur_client, 100, "PAS Invalid Context D");
                            return;
                        }
                        break;
                    case 'F':
                        if (Vars.FPAS != 1)
                        {
                            new STAError(cur_client, 100, "PAS Invalid Context F");
                            return;
                        }
                        break;
                    case 'H':
                        if (Vars.HPAS != 1)
                        {
                            new STAError(cur_client, 100, "PAS Invalid Context H");
                            return;
                        }

                }
            }
            Nod k;

            if ((k = AccountsConfig.isNickRegFl(cur_client.NI)) != null)
            {
                cur_client.reg = k;
            }
            if (!cur_client.reg.isreg)
            {
                new STAError(cur_client, 100, "Not registered.");
                return;
            }
            if (Issued_Command.charAt(0) != 'H')
            {
                if (State.equals("NORMAL"))
                {
                    throw new CommandException("FAIL state:PROTOCOL reason:NOT BASE CLIENT");
                }
                else
                {
                    new STAError(cur_client, 100, "PAS Invalid Context.");
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
                //  byte [] bytecid=Base32.decode (cur_client.ID);
                byte[] pas = cur_client.reg.Password.getBytes();
                byte[] random = Base32.decode(cur_client.RandomData);

                byte[] result = new byte[pas.length + random.length];
                //for(int i=0;i<bytecid.length;i++)
                //   result[i]=bytecid[i];
                for (int i = 0; i < pas.length; i++)
                {
                    result[i] = pas[i];
                }
                for (int i = pas.length; i < random.length + pas.length; i++)
                {
                    result[i] = random[i - pas.length];
                }

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
            if (realpas.equals(Issued_Command.substring(5)))
            {
                cur_client.sendToClient("IMSG Authenticated.");

                cur_client.sendFromBot(ADC.MOTD);

                //System.out.println ("pwla");
                cur_client.reg.LastNI = cur_client.NI;
                // cur_client.reg.LastNI=cur_client.NI;
                cur_client.reg.LastIP = cur_client.RealIP;

                if (!cur_client.ID.equals(cur_client.reg.CID))
                {
                    cur_client.sendToClient("IMSG Account\\sCID\\supdated\\sto\\s" + cur_client.ID);
                }
                cur_client.reg.CID = cur_client.ID;
            }
            else
            {
                new STAError(cur_client, 200 + Constants.STA_INVALID_PASSWORD, "Invalid Password.");
                return;
            }

            //System.out.println (Issued_Command);
            completeLogIn();
        }

        /**********************SUP COMMAND******************************/
        if (Issued_Command.charAt(1) == 'S' &&
            Issued_Command.charAt(2) == 'U' &&
            Issued_Command.charAt(3) == 'P')
        {
            new SUP(cur_client, State, Issued_Command);

        }


        /********************************MSG COMMAND************************************/
        if (Issued_Command.charAt(1) == 'M' &&
            Issued_Command.charAt(2) == 'S' &&
            Issued_Command.charAt(3) == 'G')
        {
            new MSG(cur_client, State, Issued_Command);
        }


        if (Issued_Command.charAt(1) == 'S' &&
            Issued_Command.charAt(2) == 'C' &&
            Issued_Command.charAt(3) == 'H')
        {
            new SCH(cur_client, Issued_Command, State);
        }
        if (Issued_Command.charAt(1) == 'S' &&
            Issued_Command.charAt(2) == 'T' &&
            Issued_Command.charAt(3) == 'A')
        {
            new STA(cur_client, Issued_Command, State);
        }
        if (Issued_Command.substring(1)
                          .startsWith("RES ")) //direct search result, only active to passive must send this
        {
            new RES(cur_client, State, Issued_Command);
        }
        else if (Issued_Command.substring(1).startsWith("CTM ")) //direct connect to me
        {
            new CTM(cur_client, State, Issued_Command);
        }
        else if (Issued_Command.substring(1).startsWith("RCM ")) //reverse connect to me
        {
            new RCM(cur_client, State, Issued_Command);
        }


        /** calling plugins...*/

        for (Module myMod : Modulator.myModules)
        {
            myMod.onRawCommand(cur_client, Issued_Command);
        }
    }


    /**
     * Creates a new instance of Command with following params
     * CH of type ClientHandler identifies tha client to handle
     * Issued_command of String type actually identifies the given command
     * state also of type String Identifies tha State in which tha connection is,
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
        cur_client = CH;
        // System.out.printf("["+cur_client.NI+"]:%s\n",Issued_command);


        //System.out.printf("[Received]:%s\n",Issued_command);
        if (Issued_command.equals(""))
        {
            //System.out.println("("+cur_client.NI+")"+System.currentTimeMillis ()/1000);
            return;
        }


        Issued_Command = Issued_command;
        State = cur_client.State;
        HandleIssuedCommand();
        // if(cur_client.NI.contains("Pietr"))
        //    new STAError(cur_client,201,"exception test bla.");
    }

}
