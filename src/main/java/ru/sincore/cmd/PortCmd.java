/*
 * PortCmd.java
 * 
 * Created on 13 January 2008, 21:00
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

import java.util.StringTokenizer;

import dshub.ClientHandler;
import dshub.Main;
import dshub.conf.Port;
import dshub.conf.Vars;

/**
 * @author Pietricica
 */
public class PortCmd
{


    public PortCmd(ClientHandler cur_client, String cmd)
    {
        StringTokenizer ST = new StringTokenizer(cmd);
        ST.nextToken();

        if (!ST.hasMoreTokens())
        {
            String send = "Port, a simple server port handling interface.\n" +
                          "Usage:   port -- displays this text along with current ports and their status\n" +
                          "   port add x -- adds the port x to ports, immediately starting to listen to it ( if possible, otherwise status DEAD )\n" +
                          "   port del x -- deletes the port x from ports, immediately closing listening to it. Note that this also DISCONNECTS clients connected using this port.\n";
            send += "Current ports among with their status :\n";
            for (Port port : Vars.activePorts)
            {
                send += port.portValue +
                        " " +
                        (port.getStatus() ? "LISTENING\n" : "DEAD  Reason: " + port.MSG + "\n");
            }

            if (cur_client != null)
            {
                cur_client.sendFromBot(send);
            }
            else
            {
                System.out.print(send);
            }
            return;

        }
        String what = ST.nextToken();
        if (what.equalsIgnoreCase("add"))
        {
            if (!ST.hasMoreTokens())
            {
                String send = "Port, a simple server port handling interface.\n" +
                              "Usage:   port add x -- adds the port x to ports, immediately starting to listen to it ( if possible, otherwise status DEAD )\n";
                if (cur_client != null)
                {
                    cur_client.sendFromBot(send);
                }
                else
                {
                    System.out.print(send);
                }
                return;
            }

            try
            {
                int newp = Integer.parseInt(ST.nextToken());
                Port newport = new Port(newp);
                if (Vars.activePorts.isEmpty() || !Vars.getHostPort().getStatus())
                {
                    int x = Vars.Hub_Host.indexOf(':');
                    if (x == -1 || x > Vars.Hub_Host.length() - 1)
                    {
                        return;
                    }
                    Vars.Hub_Host = Vars.Hub_Host.substring(0, x) + ":" + newp;
                }
                Vars.activePorts.add(newport);
                if (Main.Server.addPort(newport) == true)
                {
                    if (cur_client != null)
                    {
                        cur_client.sendFromBot("Adding successful. Server now listening also on " +
                                               newport.portValue);
                    }
                    else
                    {
                        System.out
                                .print("Adding successful. Server now listening also on " +
                                       newport.portValue);
                    }
                }
                else if (cur_client != null)
                {
                    cur_client.sendFromBot("Adding failed. Reason: " + newport.MSG);
                }
                else
                {
                    System.out.print("Adding failed. Reason: " + newport.MSG);
                }
            }
            catch (NumberFormatException nfe)
            {
                String send =
                        "Invalid port number.\n";
                if (cur_client != null)
                {
                    cur_client.sendFromBot(send);
                }
                else
                {
                    System.out.print(send);
                }
            }

            return;
        }

        if (what.equalsIgnoreCase("del"))
        {
            if (!ST.hasMoreTokens())
            {
                String send = "Port, a simple server port handling interface.\n" +
                              "Usage:   port del x -- deletes the port x from ports, immediately closing listening to it. Note that this also DISCONNECTS clients connected using this port.\n";
                if (cur_client != null)
                {
                    cur_client.sendFromBot(send);
                }
                else
                {
                    System.out.print(send);
                }
                return;
            }

            try
            {
                int newp = Integer.parseInt(ST.nextToken());
                for (Port newport : Vars.activePorts)
                {
                    if (newport.portValue == newp)
                    {
                        Vars.activePorts.remove(newport);

                        Main.Server.delPort(newport);
                        if (cur_client != null)
                        {
                            cur_client.sendFromBot("Removed port " + newport.portValue);
                        }
                        else
                        {
                            System.out.print("Removed port " + newport.portValue);
                        }
                        if (newp ==
                            Integer.parseInt(Vars.Hub_Host
                                                     .substring(Vars.Hub_Host.indexOf(':') + 1)))
                        {
                            if (!Vars.activePorts.isEmpty())
                            {
                                for (Port cur : Vars.activePorts)
                                {
                                    if (cur.getStatus())
                                    {
                                        Vars.Hub_Host = Vars.Hub_Host
                                                .replace(":" + newp,
                                                         ":" + String.valueOf(cur.portValue));
                                    }
                                }
                            }
                        }
                        return;

                    }
                }

                if (cur_client != null)
                {
                    cur_client.sendFromBot("Port " + newp + " doesn't exist.");
                }
                else
                {
                    System.out.print("Port " + newp + " doesn't exist");
                }
            }
            catch (NumberFormatException nfe)
            {
                String send =
                        "Invalid port number.\n";
                if (cur_client != null)
                {
                    cur_client.sendFromBot(send);
                }
                else
                {
                    System.out.print(send);
                }
            }

            return;
        }
        String send = "Port, a simple server port handling interface.\n" +
                      "  Unknown switch. Please use with no switches for help\n";
        if (cur_client != null)
        {
            cur_client.sendFromBot(send);
        }
        else
        {
            System.out.print(send);
        }

    }


}
