package ru.sincore.conf;
/*
 * Variables.java
 *
 * Created on 06 martie 2007, 16:04
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

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Basic hub variables to be kept in config file.
 * Serializable and static class.
 *
 * @author Pietricica
 */
public class Variables implements Serializable
{
    public int    Timeout_Login;
    /**
     * Default port on which to start hubbie
     */
    //int Default_Port;


    public String HubVersion;

    public String HubDE;

    public String HubName;


    public String About;

    public String Opchat_name;
    public String Opchat_desc;
    public String bot_name;
    public String bot_desc;

    public String Msg_Banned;

    public String nick_chars;
    public String Msg_Full;

    public String Msg_Search_Spam;

    public String SecurityCid;
    public String OpChatCid;

    public String Hub_Host;

    public String Proxy_Host;
    public int    Proxy_Port;

    public String redirect_url;

    public boolean adcs_mode;
    public boolean certlogin;

    public String lang;

    public int  max_ni;
    public int  min_ni;
    public int  max_de;
    public long max_share;
    public long min_share;
    public int  max_sl;
    public int  min_sl;
    public int  max_em;
    public int  max_hubs_op;
    public int  max_hubs_reg;
    public int  max_hubs_user;
    public int  min_sch_chars;
    public int  max_sch_chars;
    public int  max_chat_msg;

    public int command_pm;

    public int history_lines;
    public int kick_time;

    public int reg_only;
    public int max_users;


    public int chat_interval;

    public int savelogs;
    public int automagic_search;
    public int search_log_base;
    public int search_steps;
    public int search_spam_reset;


    public String activePlugins;

    // ****** ADC advanced config ************* 

    public int BMSG;
    public int DMSG;
    public int EMSG;
    public int FMSG;
    public int HMSG;

    public int BSTA;
    public int DSTA;
    public int ESTA;
    public int FSTA;
    public int HSTA;

    public int BCTM;
    public int DCTM;
    public int ECTM;
    public int FCTM;
    public int HCTM;

    public int BRCM;
    public int DRCM;
    public int ERCM;
    public int FRCM;
    public int HRCM;

    public int BINF;
    public int DINF;
    public int EINF;
    public int FINF;
    public int HINF;

    public int BSCH;
    public int DSCH;
    public int ESCH;
    public int FSCH;
    public int HSCH;

    public int BRES;
    public int DRES;
    public int ERES;
    public int FRES;
    public int HRES;

    public int BPAS;
    public int DPAS;
    public int EPAS;
    public int FPAS;
    public int HPAS;

    public int              BSUP;
    public int              DSUP;
    public int              ESUP;
    public int              FSUP;
    public int              HSUP;
    public LinkedList<Port> activePorts;


    public Variables()
    {
        Timeout_Login = Vars.Timeout_Login;
        // Default_Port=Vars.Default_Port;

        HubDE = Vars.HubDE;
        HubName = Vars.HubName;

        About = Vars.About;
        HubVersion = Vars.HubVersion;

        max_ni = Vars.max_ni;
        min_ni = Vars.min_ni;
        max_de = Vars.max_de;
        max_share = Vars.max_share; //10 tebibytes
        min_share = Vars.min_share;
        max_sl = Vars.max_sl;
        min_sl = Vars.min_sl;
        max_em = Vars.max_em;
        max_hubs_op = Vars.max_hubs_op;
        max_hubs_reg = Vars.max_hubs_reg;
        max_hubs_user = Vars.max_hubs_user;
        min_sch_chars = Vars.min_sch_chars;
        max_sch_chars = Vars.max_sch_chars;
        max_chat_msg = Vars.max_chat_msg;
        command_pm = Vars.command_pm;

        chat_interval = Vars.chat_interval;

        adcs_mode = Vars.adcs_mode;

        history_lines = Vars.history_lines;
        Opchat_name = Vars.Opchat_name;
        Opchat_desc = Vars.Opchat_desc;
        kick_time = Vars.kick_time;
        Msg_Banned = Vars.Msg_Banned;

        SecurityCid = Vars.SecurityCid;
        OpChatCid = Vars.OpChatCid;

        Hub_Host = Vars.Hub_Host;

        Proxy_Host = Vars.Proxy_Host;
        Proxy_Port = Vars.Proxy_Port;

        redirect_url = Vars.redirect_url;


        reg_only = Vars.reg_only;
        nick_chars = Vars.nick_chars;
        max_users = Vars.max_users;
        Msg_Full = Vars.Msg_Full;


        savelogs = Vars.savelogs;
        automagic_search = Vars.automagic_search;
        search_log_base = Vars.search_log_base;
        search_steps = Vars.search_steps;
        search_spam_reset = Vars.search_spam_reset;
        Msg_Search_Spam = Vars.Msg_Search_Spam;
        bot_name = Vars.bot_name;
        bot_desc = Vars.bot_desc;

        activePlugins = Vars.activePlugins;

        activePorts = Vars.activePorts;

        lang = Vars.lang;

        BMSG = Vars.BMSG;
        DMSG = Vars.DMSG;
        EMSG = Vars.EMSG;
        FMSG = Vars.FMSG;
        HMSG = Vars.HMSG;

        BSTA = Vars.BSTA;
        DSTA = Vars.DSTA;
        ESTA = Vars.ESTA;
        FSTA = Vars.FSTA;
        HSTA = Vars.HSTA;

        BCTM = Vars.BCTM;
        DCTM = Vars.DCTM;
        ECTM = Vars.ECTM;
        FCTM = Vars.FCTM;
        HCTM = Vars.HCTM;

        BRCM = Vars.BRCM;
        DRCM = Vars.DRCM;
        ERCM = Vars.ERCM;
        FRCM = Vars.FRCM;
        HRCM = Vars.HRCM;

        BINF = Vars.BINF;
        DINF = Vars.DINF;
        EINF = Vars.EINF;
        FINF = Vars.FINF;
        HINF = Vars.HINF;

        BSCH = Vars.BSCH;
        DSCH = Vars.DSCH;
        ESCH = Vars.ESCH;
        FSCH = Vars.FSCH;
        HSCH = Vars.HSCH;

        BRES = Vars.BRES;
        DRES = Vars.DRES;
        ERES = Vars.ERES;
        FRES = Vars.FRES;
        HRES = Vars.HRES;

        BPAS = Vars.BPAS;
        DPAS = Vars.DPAS;
        EPAS = Vars.EPAS;
        FPAS = Vars.FPAS;
        HPAS = Vars.HPAS;

        BSUP = Vars.BSUP;
        DSUP = Vars.DSUP;
        ESUP = Vars.ESUP;
        FSUP = Vars.FSUP;
        HSUP = Vars.HSUP;
    }
}
 