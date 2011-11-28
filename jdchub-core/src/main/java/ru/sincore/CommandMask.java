/*
 * CommandMask.java
 *
 * Created on 06 octombrie 2007, 15:37
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

/**
 * Basic actionName mask for each registered user, keeps registration information about
 * account possibilites.
 *
 * //TODO Not needed, must be removed
 *
 * @author Pietricica
 */
public class CommandMask implements Serializable
{
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
    public boolean adc, adcs, port, reg, ureg, listreg, mass, kick, drop, unban, banip, bancid,
            bannick, history, cmdhistory, info, hideme, password, mynick, rename, usercount, topic,
            cfg, gui, stats, about, help, restart, quit, listban, grant, chatcontrol, backup,
            plugmin, redirect;


    /**
     * Creates a new instance of CommandMask
     */
    public CommandMask()
    {
        adc = adcs = port = reg = ureg = listreg = mass = kick = drop = unban =
                banip = bancid = bannick = cmdhistory = info = hideme = rename = usercount = topic =
                        cfg = gui = stats = restart = quit =
                                listban = grant = chatcontrol = backup = plugmin = redirect = false;
        mynick = help = password = history = about = true;
    }


    public CommandMask(int i)
    {
        if (i == 1)
        {
            adc = adcs = port = reg = ureg = listreg = mass = kick = drop = unban = banip = bancid =
                    bannick = history =
                            cmdhistory = info = hideme = password = rename = usercount = topic =
                                    cfg = gui = stats = about = restart = quit = listban = grant =
                                            chatcontrol = backup = plugmin = redirect = true;
            mynick = help = true;
        }
    }

}
