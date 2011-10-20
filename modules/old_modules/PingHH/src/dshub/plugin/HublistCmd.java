/*
 * HublistCmd.java
 *
 * Created on 22 ianuarie 2008, 17:54
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

package dshub.plugin;

import ru.sincore.ClientHandler;

import java.util.StringTokenizer;

/**
 *
 * @author Pietricica
 */
public class HublistCmd 
{

    public HublistCmd(ClientHandler cur_client, String cmd)
    {
        StringTokenizer ST=new StringTokenizer(cmd);
        ST.nextToken();
        if(!ST.hasMoreTokens())
        {
            cur_client.sendFromBot("[PingHH: ] Ping Extension Hublist Autoregistering Module Help:\nHublist command: "+
                    "\nhublist register  -- starts the autoregistration process."+
                    "\nhublist list  --  lists the current hublists along with their status." +
                    "\nhublist add <URL> --  adds the given URL to hublist list." +
                    "\nhublist del <URL> --  deletes the given URL from the hublist list." +
                    "\nhublist mod <oldURL> <newURL> --  modifies the given oldURL from the hublist list into newURL.");
            
            return;
        }
        
        String what=ST.nextToken();
        if(what.equalsIgnoreCase("register"))
        {
            cur_client.sendFromBot("[PingHH: ] Starting the registering process pelase be patient it might take a while...");
            new RegisterThread(cur_client);
            return;
        }
        if(what.equalsIgnoreCase("list"))
        {
            String tosend="[PingHH: ] Current Hublists:";
            for( Hublist hList : PluginMain.curlist.hList)
            
                tosend+="\nAddress: "+hList.URL+" Name:"+hList.Name+" Website:"+hList.Website+" STATUS:"+hList.status;
            cur_client.sendFromBot(tosend);
            return;
        }
        if(what.equalsIgnoreCase("add"))
        {
            if(!ST.hasMoreTokens())
            {
                cur_client.sendFromBot("[PingHH: ] Ping Extension Hublist Autoregistering Module Help:\nHublist command: "+
                    
                    "\nhublist add <URL> --  adds the given URL to hublist list." 
                    );
                return;
            }
            String URL=ST.nextToken();
            PluginMain.curlist.add(URL);
            cur_client.sendFromBot("[PingHH: ] Adding hublist entry successful.");
            return;
        }
        if(what.equalsIgnoreCase("del"))
        {
            if(!ST.hasMoreTokens())
            {
                cur_client.sendFromBot("[PingHH: ] Ping Extension Hublist Autoregistering Module Help:\nHublist command: "+
                    
                    "\nhublist del <URL> --  deletes the given URL from the hublist list."
                    );
                return;
            }
            String URL=ST.nextToken();
            if(PluginMain.curlist.rem(URL))
               cur_client.sendFromBot("[PingHH: ] Deleting hublist entry successful.");
            else
               cur_client.sendFromBot("[PingHH: ] Deleting hublist entry failed. Error."); 
            return;
        }
        if(what.equalsIgnoreCase("mod"))
        {
            if(!ST.hasMoreTokens())
            {
                cur_client.sendFromBot("[PingHH: ] Ping Extension Hublist Autoregistering Module Help:\nHublist command: "+
                    
                    "\nhublist mod <oldURL> <newURL> --  modifies the given oldURL from the hublist list into newURL."
                    );
                return;
            }
            String URL=ST.nextToken();
            if(!ST.hasMoreTokens())
            {
                cur_client.sendFromBot("[PingHH: ] Ping Extension Hublist Autoregistering Module Help:\nHublist command: "+
                    
                    "\nhublist mod <oldURL> <newURL> --  modifies the given oldURL from the hublist list into newURL."
                    );
                return;
            }
            String newURL=ST.nextToken();
            if(PluginMain.curlist.mod(URL,newURL))
               cur_client.sendFromBot("[PingHH: ] Modifying hublist entry successful.");
            else
               cur_client.sendFromBot("[PingHH: ] Modifying hublist entry failed. Error."); 
            return;
        }
        
        cur_client.sendFromBot("[PingHH: ] Invalid switch. Use with no arguments to see help.");
        
    }
}
