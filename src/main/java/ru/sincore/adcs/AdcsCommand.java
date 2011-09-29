/*
 * AdcsCommand.java
 *
 * Created on 02 octombrie 2008, 14:57
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

package ru.sincore.adcs;

import org.apache.log4j.Logger;
import ru.sincore.ClientHandler;
import ru.sincore.ConfigLoader;
import ru.sincore.HubServer;
import ru.sincore.Main;

import java.util.StringTokenizer;

/**
 * @author Eugen Hristev
 */

public class AdcsCommand
{
    public static final Logger log = Logger.getLogger(AdcsCommand.class);

    public AdcsCommand(ClientHandler cur_client, String cmd)
    {
        StringTokenizer curcmd = new StringTokenizer(cmd);
        curcmd.nextToken();
        if (!curcmd.hasMoreTokens())
        {
            String Help =
                    "\nThe adcs command. AdcUtils Secure is a standard AdcUtils extension that enables" +
                    " running the AdcUtils protocol over the TLS/SSL layer. To enable ADCS you need to setup your " +
                    " keys and certificate first, either by regenerating them or loading them from a file." +
                    " After this operation you may try to enable ADCS.\n" +
                    "Usage: adcs switch" +
                    "\n    Possible switches:  " +
                    "\n      regen -- regenerates (and overwrites if they exist) the current keys and certificate." +
                    "\n      load <path> -- loads the keys and certificate previously generated from the file specified in <path>." +
                    "\n      enable -- if keys and certificate are ok, then this command enables ADCS and restarts your hub." +
                    "\n      disable -- this command disables ADCS and restarts your hub in normal AdcUtils mode." +
                    "\n      certlogin [on|off] -- the on/off switch enables/disables the certificate based login ( no passwords )." +
                    "\nCurrently, ADCS mode is" +
                    (ConfigurationManager.instance().getBoolean(ConfigurationManager.ENABLE_ADCS) ? " running ok " : " disabled ") +
                    "and certificate based login is " +
                    (ConfigurationManager.instance().getBoolean(ConfigurationManager.CERT_LOGIN) ? "on." : "off.");
            cur_client.sendFromBot(Help);
            return;
        }
        String what = curcmd.nextToken();
        if (what.equalsIgnoreCase("regen"))
        {
            cur_client.sendFromBot("Regenerating keys and certificate... ( this may take a while )");
            boolean keygenerated = Main.server.sslmanager.getCertManager().recreateKeysCerts();
            // JOptionPane.showConfirmDialog(null,"New pair of keys and certificate were created and saved into key.crt",
            //        Vars.HubName,JOptionPane.OK_OPTION,JOptionPane.INFORMATION_MESSAGE);
            if (keygenerated)
            {
                log.info("New pair of keys and certificate were created and saved into key.crt");
                cur_client.sendFromBot(
                        "New pair of keys and certificate were created and saved into key.crt");
                Main.server.adcs_ok = true;
            }
            else
            {
                log.warn("Error creating keys and certificates. Check the log for details.");

                cur_client.sendFromBot(
                        "Error creating keys and certificates. Check the log for details.");
            }
            return;
        }
        if (what.equalsIgnoreCase("enable"))
        {
            HubServer.done_adcs = false;
            cur_client.sendFromBot("Attempting to enable ADCS.... ( Hub will restart )...");
            if (!Main.server.adcs_ok)
            {
                return;//cannot start adcs mode.. bug ?
            }
            ConfigurationManager.instance().getBoolean(ConfigurationManager.ENABLE_ADCS) = true;
            Main.Restart();
            log.info("AdcUtils Secure mode has been enabled");
            return;
        }

        if (what.equalsIgnoreCase("disable"))
        {
            HubServer.done_adcs = false;
            cur_client.sendFromBot("Disabling ADCS.... ( Hub will restart )...");
            ConfigurationManager.instance().getBoolean(ConfigurationManager.ENABLE_ADCS) = false;
            Main.Restart();
            log.warn("AdcUtils Secure mode has been disabled");
        }
        cur_client.sendFromBot("Unknown switch. Try to use with no parameters for details.");

    }
}
