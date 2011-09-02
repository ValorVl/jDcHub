/*
 * HubtrackerCmd.java
 *
 * Created on 29 noiembrie 2007, 17:38
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

import dshub.*;
import java.util.StringTokenizer;

/**
 *
 * @author Pietricica
 */
public class HubtrackerCmd
{
    ClientHandler cur_client;
    /** Creates a new instance of HubtrackerCmd */
    public HubtrackerCmd(ClientHandler cur_client,String Issued_Command)
    {
        this.cur_client=cur_client;
        
        StringTokenizer ST=new StringTokenizer(Issued_Command);
        ST.nextToken();
        if(!ST.hasMoreTokens())
        {
            cur_client.sendFromBot("[hubtracker:] Command usage:\nhubtracker <user> <password> <e-mail>\n"+
                    "Where <user> is the desired/active username on hubtracker.com,\n" +
                    "<password> is the desired/active login password on hubtracker.com \n"+
                    "<e-mail> is the desired/active e-mail for the hubtracker.com account.\n" +
                    "Desired means that you dont have an account and you want to make one\n" +
                    "and active means that you already have an account and you want to use it.\n" +
                    "The hub autoregisters with the current hub_host and default_port.");
            return;
        }
        String user =ST.nextToken();
        if(!ST.hasMoreTokens())
        {
            cur_client.sendFromBot("[hubtracker:] Command usage:\nhubtracker <user> <password> <e-mail>\n"+
                    "Where <user> is the desired/active username on hubtracker.com,\n" +
                    "<password> is the desired/active login password on hubtracker.com \n"+
                    "<e-mail> is the desired/active e-mail for the hubtracker.com account.\n" +
                    "Desired means that you dont have an account and you want to make one\n" +
                    "and active means that you already have an account and you want to use it.\n" +
                    "The hub autoregisters with the current hub_host and default_port.");
            return;
        }
        String pass=ST.nextToken();
        if(!ST.hasMoreTokens())
        {
            cur_client.sendFromBot("[hubtracker:] Command usage:\nhubtracker <user> <password> <e-mail>\n"+
                    "Where <user> is the desired/active username on hubtracker.com,\n" +
                    "<password> is the desired/active login password on hubtracker.com \n"+
                    "<e-mail> is the desired/active e-mail for the hubtracker.com account.\n" +
                    "Desired means that you dont have an account and you want to make one\n" +
                    "and active means that you already have an account and you want to use it.\n" +
                    "The hub autoregisters with the current hub_host and default_port.");
            return;
        }
        String email=ST.nextToken();
        PluginMain.result="";
        new HubtrackerConnection(this,user,pass,email);
    }
    
}
