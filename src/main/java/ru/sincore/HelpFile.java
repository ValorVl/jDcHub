/*
 * HelpFile.java
 *
 * Created on 06 octombrie 2007, 15:44
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

import java.io.Serializable;

import ru.sincore.conf.Vars;

/**
 * Help File generator class, dynamically.
 *
 * @author Pietricica
 */
public class HelpFile implements Serializable
{
    Nod curAcc;


    /**
     * Creates a new instance of HelpFile
     */
    public HelpFile(Nod curAcc)
    {
        this.curAcc = curAcc;
    }


    /*
                            port x -- Where x is the new port hub is supposed to run on.
                            reg CID/online user nick -- Reg the new CID with no password ( by default) or the CID of the online user specified by nick.
                            ureg CID/online user nick -- Unregs the CID/user's CID from database.
                            listreg -- Lists the current registered CIDs
                        mass -- Broadcast message, can take extended params
                            kick -- Kicks out the user given by nick , add extra words for reason/Extended kick type kick for info
                            drop -- A kick with no reason or ban time, just drop/Extended drop type !drop for info
                            unban -- Unbans the specified, looking in CID/IP/nick order
                            banip -- Bans a given ip or the ip of the given online user
                            bancid -- Bans a given cid  or the cid of the given online user
                            bannick -- Bans a given nick, drops if nick online
                            history -- Lists the last history_lines from chat
                            cmdhistory -- Lists the last history_lines from given commands by logged users
                            info -- Lists some useful information about a user,ip or cid
                            hideme -- Toggles if you are hidden or not
                            password newpass -- Changes your current password, where newpass is the new password
                            mynick -- Changes your nick to new specified one
                            rename -- Renames the user given by nick to new nick given
                            usercount -- Info about the current user count.
                            topic newtopic -- Where newtopic is the new desired topic. Use just "topic" to delete current topic.
                            cfg -- The hub variables.
                            gui -- brings up the gui to server if available
                            stats -- Hub statistics.
                            about -- The program credits.
                            help -- This screen.
                            restart -- Restarts hub.
                            quit -- Shuts down hub.
    */
    public String getHelp()
    {
        String blah = "Death Squad Hub " + Vars.HubVersion + ". Running on " +
                      Main.Proppies.getProperty("os.name")
                      + " " + Main.Proppies.getProperty("os.version")
                      + " " + Main.Proppies.getProperty("os.arch") + "\n";
        String Help = blah +
                      "General issue about commands: Most commands have a help when used with no parameteres." +
                      "\n-------------------------------------------------------------\nAvailable Commands :\n";
        if (curAcc.myMask.about)
        {
            Help += "about -- The program credits.\n";
        }
        if (curAcc.myMask.adc)
        {
            Help += "adc -- ADC advanced configuration panel, setting contexts for each command.\n";
        }
        if (curAcc.myMask.adcs)
        {
            Help += "adcs -- A simple interface for setting up ADC Secure mode.\n";
        }
        if (curAcc.myMask.backup)
        {
            Help += "backup -- A way to save configuration to files for backup or other usage.\n";
        }
        if (curAcc.myMask.bancid)
        {
            Help += "bancid -- Bans a given cid or the cid of the given online user.\n";
        }
        if (curAcc.myMask.banip)
        {
            Help += "banip -- Bans a given ip or the ip of the given online user.\n";
        }
        if (curAcc.myMask.bannick)
        {
            Help += "bannick -- Bans a given nick, drops if nick online.\n";
        }
        if (curAcc.myMask.cfg)
        {
            Help += "cfg -- The hub variables.\n";
        }
        if (curAcc.myMask.chatcontrol)
        {
            Help += "chatcontrol -- An simple interface to handle chat control forbidden words.\n";
        }
        if (curAcc.myMask.cmdhistory)
        {
            Help +=
                    "cmdhistory -- Lists the last history_lines from given commands by logged users.\n";
        }
        if (curAcc.myMask.drop)
        {
            Help += "drop -- A kick with no reason or ban time, just drop/Extended drop.\n";
        }
        if (curAcc.myMask.grant)
        {
            Help +=
                    "grant -- Offers the posibility of editing an account's profile; use with no arguments for info.\n";
        }
        if (curAcc.myMask.gui)
        {
            Help += "gui -- Brings up the gui to server if available.\n";
        }
        if (curAcc.myMask.help)
        {
            Help += "help -- This screen.\n";
        }
        if (curAcc.myMask.hideme)
        {
            Help += "hideme -- Toggles if you are hidden or not in userlist.\n";
        }
        if (curAcc.myMask.history)
        {
            Help += "history -- Lists the last history_lines from chat.\n";
        }
        if (curAcc.myMask.info)
        {
            Help += "info -- Lists some useful information about a user,ip or cid.\n";
        }
        if (curAcc.myMask.kick)
        {
            Help +=
                    "kick -- Kicks out the user given by nick, add extra words for reason/Extended kick.\n";
        }
        if (curAcc.myMask.listban)
        {
            Help += "listban -- Lists the current banned CIDs/IPs/nicks.\n";
        }
        if (curAcc.myMask.listreg)
        {
            Help += "listreg -- Lists the current registered CIDs.\n";
        }
        if (curAcc.myMask.mass)
        {
            Help += "mass -- Broadcast message, takes extended parameters.\n";
        }
        if (curAcc.myMask.mynick)
        {
            Help += "mynick -- Changes your nick to new specified one.\n";
        }
        if (curAcc.myMask.password)
        {
            Help +=
                    "password newpass -- Changes your current password, where newpass is the new password.\n";
        }
        if (curAcc.myMask.plugmin)
        {
            Help +=
                    "plugmin -- Plugin Administration. Allows enabling/disabling and scanning plugins.\n";
        }
        if (curAcc.myMask.port)
        {
            Help += "port -- A simple interface to maintain current listening ports.\n";
        }
        if (curAcc.myMask.quit)
        {
            Help += "quit -- Shuts down hub.\n";
        }
        if (curAcc.myMask.redirect)
        {
            Help += "redirect -- Redirect user(s), takes extended parameteres.\n";
        }
        if (curAcc.myMask.reg)
        {
            Help +=
                    "reg CID/online user nick -- Reg the new CID with no password (by default) or the CID of the online user specified by nick.\n                                  If already registered, display registration info.\n";
        }
        if (curAcc.myMask.rename)
        {
            Help += "rename -- Renames the user given by nick to new nick given.\n";
        }
        if (curAcc.myMask.restart)
        {
            Help += "restart -- Restarts hub.\n";
        }
        if (curAcc.myMask.stats)
        {
            Help += "stats -- Hub statistics.\n";
        }
        if (curAcc.myMask.topic)
        {
            Help +=
                    "topic newtopic -- Where newtopic is the new desired topic; use with no arguments to delete current topic.\n";
        }
        if (curAcc.myMask.unban)
        {
            Help += "unban -- Unbans the specified, looking in CID/IP/nick order.\n";
        }
        if (curAcc.myMask.ureg)
        {
            Help += "ureg CID/online user nick -- Unregs the CID/user's CID from database.\n";
        }
        if (curAcc.myMask.usercount)
        {
            Help += "usercount -- Info about the current user count.";
        }


        return Help;
    }

}
