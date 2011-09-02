/*
 * 
 * PluginCmd.java
 *
 * Created on 16 decembrie 2007, 13:16
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

package dshub.cmd;

import dshub.ClientHandler;
import dshub.Modules.Modulator;
import dshub.Modules.Module;

import java.util.StringTokenizer;

/**
 * @author Pietricica
 */
public class PlugminCmd
{

    public PlugminCmd(ClientHandler cur_client, String Issued_Command)
    {
        StringTokenizer ST = new StringTokenizer(Issued_Command);
        ST.nextToken();
        if (!ST.hasMoreTokens())
        {
            String send = "Plugmin command. A way to administer plugins.\n" +
                          "Usage:\n" +
                          "   plugmin list -- lists the current scanned plugins, their status and their ID.\n" +
                          "   plugmin enable ID/name -- Enables the plugin given by ID or name.\n" +
                          "   plugmin disable ID/name -- Disables the plugin given by ID or name.\n" +
                          "   plugmin scan -- Rescans the modules directory and refreshes current plugins.";
            cur_client.sendFromBot(send);
            return;
        }
        String what = ST.nextToken();
        if (what.equalsIgnoreCase("list"))
        {
            String send = "Scanned modules list:";
            int ID = 0;
            for (Module myPlug : Modulator.myModules)
            {
                send += "\n" +
                        myPlug.getName() +
                        " ID " +
                        myPlug.getID() +
                        "   " +
                        (myPlug.isEnabled() ? "Enabled." : "Disabled.");
            }
            cur_client.sendFromBot(send);
            return;
        }
        else if (what.equalsIgnoreCase("enable"))
        {
            if (!ST.hasMoreTokens())
            {
                cur_client.sendFromBot("Must specify ID/name of plugin to enable");
                return;
            }
            String who = ST.nextToken();
            int ID = -1;
            Module myPlug;
            try
            {
                ID = Integer.parseInt(who);
                for (Module myx : Modulator.myModules)
                {
                    if (myx.getID() == ID)
                    {
                        myPlug = myx;
                        myPlug.setEnabled(true);
                        cur_client.sendFromBot("Enabled plugin " +
                                               myPlug.getName() +
                                               " with ID " +
                                               myPlug.getID() +
                                               ".");
                        return;
                    }

                }
            }
            catch (NumberFormatException nfe)
            {
                for (Module myx : Modulator.myModules)
                {
                    if (who.equals(myx.getName()))
                    {
                        myPlug = myx;
                        myPlug.setEnabled(true);
                        cur_client.sendFromBot("Enabled plugin " +
                                               myPlug.getName() +
                                               " with ID " +
                                               myPlug.getID() +
                                               ".");
                        return;
                    }

                }
            }


        }
        else if (what.equalsIgnoreCase("disable"))
        {
            if (!ST.hasMoreTokens())
            {
                cur_client.sendFromBot("Must specify ID/name of plugin to disable");
                return;
            }
            String who = ST.nextToken();
            int ID = -1;
            Module myPlug;
            try
            {
                ID = Integer.parseInt(who);
                for (Module myx : Modulator.myModules)
                {
                    if (myx.getID() == ID)
                    {
                        myPlug = myx;
                        myPlug.setEnabled(false);
                        cur_client.sendFromBot("Disabled plugin " +
                                               myPlug.getName() +
                                               " with ID " +
                                               myPlug.getID() +
                                               ".");
                        return;
                    }

                }
            }
            catch (NumberFormatException nfe)
            {
                for (Module myx : Modulator.myModules)
                {
                    if (who.equals(myx.getName()))
                    {
                        myPlug = myx;
                        myPlug.setEnabled(false);
                        cur_client.sendFromBot("Disabled plugin " +
                                               myPlug.getName() +
                                               " with ID " +
                                               myPlug.getID() +
                                               ".");
                        return;
                    }

                }
            }


        }
        else if (what.equalsIgnoreCase("scan"))
        {
            Modulator.findModules();
            cur_client.sendFromBot(
                    "Plugin scan successful. Use plugmin list to see current plugins.");
            return;
        }
        else
        {
            cur_client.sendFromBot("Unkown switch. Use with no parameters for help.");
            return;
        }
    }

}
