/*
 * Vars.java
 *
 * Created on 02 decembrie 2007, 11:54
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

package dshub.conf;

import dshub.Main;
import dshub.TigerImpl.Base32;
import dshub.TigerImpl.Tiger;
import dshub.util.ADC;

import java.util.LinkedList;

/**
 * @author Pietricica
 */
public final class Vars
{
    public static boolean ValidateNick(String nick)
    {
        //   for(int i=0;i<nick.length ();i++)
        //   if(nick_chars.indexOf (nick.charAt (i))==-1)
        //       return false;
        if (!nick.matches(nick_chars))
        {
            return false;
        }

        if (ADC.isIP(nick))
        {
            return false;
        }
        if (ADC.isCID(nick))
        {
            return false;
        }

        int index = Main.listaBanate.isOK(nick);
        if (index != -1)
        {
            return false;
        }

        return true;
    }


    public static int Timeout_Login = 20;
    //public static int  Default_Port=411;

    public static String HubVersion = "DSHub Kappa";
    public static String HubDE      = "";
    public static String HubName    = "hub of " + System.getProperty("user.name");


    public static String About =
            "[Death Squad Hub]\n" +
            "[Version Kappa RC1]\n" +
            "[October 2008]\n" +
            "[Licensed under GNU GPL]\n" +
            ":The credits\n"
            + "::Written by: Eugen Hristev\n"
            + ":::Special thanks goes to \n"
            + ":::MAGY, Spader, Toast, Naccio, Catalaur and Ciprian Dobre \n"

            + ":::: Also Thanks to all Testers and Contributors.\n"
            + ":::: Many Thanks to who helped translating.";

    public static String Opchat_name = "OpChat";
    public static String Opchat_desc = "BoT";
    public static String bot_name    = "DSHub";
    public static String bot_desc    = "www.death-squad.ro/dshub";

    public static String SecurityCid;
    public static String OpChatCid;

    public static String Hub_Host = "127.0.0.1:2222";

    public static String Proxy_Host = "";
    public static int Proxy_Port;
    public static String redirect_url = "";
    public static String lang         = "";

    public static LinkedList<Port> activePorts;


    static
    {
        activePorts = new LinkedList<Port>();
        activePorts.add(new Port(2222));
    }


    static
    {
        Tiger myTiger = new Tiger();

        myTiger.engineReset();
        myTiger.init();
        byte[] T = Long.toString(System.currentTimeMillis()).getBytes();
        myTiger.engineUpdate(T, 0, T.length);

        byte[] finalTiger = myTiger.engineDigest();
        Vars.OpChatCid = Base32.encode(finalTiger);
        Tiger myTiger2 = new Tiger();

        myTiger2.engineReset();
        myTiger2.init();
        T = Long.toString(System.currentTimeMillis() + 2).getBytes();
        myTiger2.engineUpdate(T, 0, T.length);

        finalTiger = myTiger2.engineDigest();
        Vars.SecurityCid = Base32.encode(finalTiger);

    }


    public static Port getHostPort()
    {
        for (Port x : activePorts)
        {
            if (x.portValue ==
                Integer.parseInt(Vars.Hub_Host.substring(Vars.Hub_Host.indexOf(':') + 1)))
            {
                return x;
            }
        }
        return new Port(Integer.parseInt(Vars.Hub_Host.substring(Vars.Hub_Host.indexOf(':') + 1)));
    }


    public static int  max_ni        = 64;
    public static int  min_ni        = 1;
    public static int  max_de        = 128;
    public static long max_share     = 10485760L; //10 tebibytes //but are now in mibibytes
    public static long min_share     = 0;
    public static int  max_sl        = 1000;
    public static int  min_sl        = 0;
    public static int  max_em        = 128;
    public static int  max_hubs_op   = 70;
    public static int  max_hubs_reg  = 30;
    public static int  max_hubs_user = 200;
    public static int  min_sch_chars = 3;
    public static int  max_sch_chars = 256;
    public static int  max_chat_msg  = 512;
    public static int  command_pm    = 0; //if set ,all the command results are sent to PM

    public static int history_lines = 50;
    public static int kick_time     = 300;

    public static int reg_only  = 0;
    public static int max_users = 1000;

    public static boolean adcs_mode     = false;
    public static boolean certlogin     = false;
    public static int     chat_interval = 500;//millis

    public static int savelogs          = 1;
    public static int automagic_search  = 36;
    public static int search_log_base   = 2000;
    public static int search_steps      = 6;
    public static int search_spam_reset = 300;

    public static String Msg_Banned      = "Have a nice day and don't forget to smile !";
    public static String Msg_Full        = "Have a nice day and don't forget to smile !";
    // public static String nick_chars="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890[]()-.,;'`~*&^%$#@!+=_|{}<>:";
    public static String nick_chars      = "([\\w\\W]*)";
    public static String Msg_Search_Spam =
            "Search ticket Reserved.\nPlease be patient while search\nis being processed.\nDo NOT close this window or start other search\nor you will lose this search !"
                    .replaceAll(" ", "\\ ");

    public static String activePlugins = "";

    // ****** ADC advanced config ************* 

    public static int BMSG = 1;
    public static int DMSG = 1;
    public static int EMSG = 1;
    public static int FMSG = 1;
    public static int HMSG = 1;

    public static int BSTA = 0;
    public static int DSTA = 1;
    public static int ESTA = 1;
    public static int FSTA = 0;
    public static int HSTA = 1;

    public static int BCTM = 0;
    public static int DCTM = 1;
    public static int ECTM = 1;
    public static int FCTM = 0;
    public static int HCTM = 0;

    public static int BRCM = 0;
    public static int DRCM = 1;
    public static int ERCM = 1;
    public static int FRCM = 0;
    public static int HRCM = 0;

    public static int BINF = 1;
    public static int DINF = 0;
    public static int EINF = 0;
    public static int FINF = 0;
    public static int HINF = 0;

    public static int BSCH = 1;
    public static int DSCH = 1;
    public static int ESCH = 1;
    public static int FSCH = 1;
    public static int HSCH = 0;

    public static int BRES = 0;
    public static int DRES = 1;
    public static int ERES = 1;
    public static int FRES = 0;
    public static int HRES = 0;

    public static int BPAS = 0;
    public static int DPAS = 0;
    public static int EPAS = 0;
    public static int FPAS = 0;
    public static int HPAS = 1;

    public static int BSUP = 0;
    public static int DSUP = 0;
    public static int ESUP = 0;
    public static int FSUP = 0;
    public static int HSUP = 1;
}